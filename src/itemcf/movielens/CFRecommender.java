package itemcf.movielens;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CFRecommender {

	public static void main(String[] args) throws Exception {
		// 获得HashMap
		CFRecommender ca = new CFRecommender();
		File file = new File("u1.base");
		Map<Integer, List<Integer>> datamap = null;
		try {
			datamap = ca.dataProcess(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 保存item-item的共同用户数
		Map<Integer, Map<Integer, Integer>> relationmap = ca.itemToItem(datamap);
		// 每个item的用户数
		HashMap<Integer, Integer> everyusernum = ca.itemNum(datamap);
		// 计算相似度
		Map<Integer, Map<Integer, Double>> similarity = comSimilarity(relationmap, everyusernum);
		// 选出待推荐用户的待推荐物品集合
		File fileusertest = new File("u1.test");
		List<Integer> recomuser = null;
		try {
			recomuser = ca.getUser(fileusertest);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Map<Integer, List<RecommendationItem>> result = ca.getRecomresult(recomuser, datamap, similarity);
		Map<Integer, Set<Integer>> testdata = ca.getTestdata(fileusertest);
		List<Double> precision = ca.comPrecision(result, testdata);
		System.out.println("precision :" + precision.get(0));
		System.out.println("recall : " + precision.get(1));
	}

	private Map<Integer, Set<Integer>> getTestdata(File fileusertest) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileusertest));
		Map<Integer, Set<Integer>> test = new HashMap<Integer, Set<Integer>>();
		String line;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			int a = Integer.parseInt(arr[0]);
			int b = Integer.parseInt(arr[1]);
			if (test.containsKey(a)) {
				Set<Integer> hs = test.get(a);
				hs.add(b);
				test.put(a, hs);
			} else {
				Set<Integer> hs = new HashSet<Integer>();
				hs.add(b);
				test.put(a, hs);
			}
		}
		br.close();
		return test;
	}

	private List<Double> comPrecision(Map<Integer, List<RecommendationItem>> result, Map<Integer, Set<Integer>> testdata) {
		int hit = 0;
		int resultsum = 0;
		int testdatasum = 0;
		List<Double> prerec = new ArrayList<Double>();
		Set<Entry<Integer, List<RecommendationItem>>> set = result.entrySet();
		for (Entry<Integer, List<RecommendationItem>> e : set) {
			int userid = e.getKey();
			List<RecommendationItem> itemid = e.getValue();
			Set<Integer> itemtestid = testdata.get(userid);
			int i = 0;
			while (i < itemid.size()) {
				resultsum = resultsum + 1;
				int tempid = itemid.get(i).getItemid();
				if (itemtestid.contains(tempid)) {
					hit = hit + 1;
				}
				i++;
			}
		}
		Set<Entry<Integer, Set<Integer>>> haha = testdata.entrySet();
		for (Entry<Integer, Set<Integer>> e : haha) {
			Set<Integer> item = e.getValue();
			Iterator<Integer> ite = item.iterator();
			while (ite.hasNext()) {
				testdatasum = testdatasum + 1;
				ite.next();
			}
		}

		double precision = (double) hit / (double) resultsum;
		double recall = (double) hit / (double) testdatasum;
		prerec.add(precision);
		prerec.add(recall);
		return prerec;
	}

	private Map<Integer, List<RecommendationItem>> getRecomresult(List<Integer> recomuser, Map<Integer, List<Integer>> datamap,
			Map<Integer, Map<Integer, Double>> similarity) {
		Map<Integer, List<RecommendationItem>> result = new HashMap<Integer, List<RecommendationItem>>();
		int i = 0;
		while (i < recomuser.size()) {
			int userid = recomuser.get(i);
			// 　　待推荐用户的List,
			List<Integer> useridlist = datamap.get(userid);
			// 待推荐用户的待推荐item
			List<Integer> recomitem = getRecomitem(userid, datamap, similarity);
			// 待推荐item的打分
			HashMap<Integer, Double> score = getScore(recomitem, similarity, useridlist);
			// 待推荐Item的排序
			List<RecommendationItem> list = getItem(score);
			Collections.sort(list);
			List<RecommendationItem> newlist = new ArrayList<RecommendationItem>();
			int j = 0;
			while (j < 50) {
				newlist.add(list.get(j));
				j++;
			}
			result.put(userid, newlist);
			i++;
		}
		return result;
	}

	private List<Integer> getUser(File fileusertest) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileusertest));
		List<Integer> user = new ArrayList<Integer>();
		String line;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			int a = Integer.parseInt(arr[0]);
			if (!user.contains(a))
				user.add(a);
		}
		br.close();
		return user;
	}

	private List<RecommendationItem> getItem(HashMap<Integer, Double> score) {
		List<RecommendationItem> li = new ArrayList<RecommendationItem>();
		Set<Entry<Integer, Double>> entry = score.entrySet();
		for (Entry<Integer, Double> e : entry) {
			RecommendationItem r = new RecommendationItem(e.getKey(), e.getValue());
			li.add(r);
		}
		return li;
	}

	private HashMap<Integer, Double> getScore(List<Integer> recomitem, Map<Integer, Map<Integer, Double>> similarity,
			List<Integer> useridlist) {
		HashMap<Integer, Double> score = new HashMap<Integer, Double>();
		int n = 0;
		while (n < recomitem.size()) {
			int itemtempid = recomitem.get(n);
			double sim = 0;
			int k = 0;
			while (k < useridlist.size()) {
				int itemid = useridlist.get(k);
				Map<Integer, Double> item = similarity.get(itemid);
				if (item.get(itemtempid) == null)
					sim = sim + 0;
				else {
					sim = sim + item.get(itemtempid);
				}
				k++;
			}
			score.put(itemtempid, sim);
			n++;
		}

		return score;
	}

	private List<Integer> getRecomitem(int userid, Map<Integer, List<Integer>> datamap, Map<Integer, Map<Integer, Double>> similarity) {
		List<Integer> useritem = datamap.get(userid);
		List<Integer> itemitem = new ArrayList<Integer>();
		int m = 0;
		while (m < useritem.size()) {
			int itemid = useritem.get(m);
			Map<Integer, Double> item = similarity.get(itemid);
			Set<Entry<Integer, Double>> entry = item.entrySet();
			for (Entry<Integer, Double> e : entry) {
				if (!itemitem.contains(e.getKey())) {
					itemitem.add(e.getKey());
				}
			}
			m++;
		}
		int n = 0;
		while (n < useritem.size()) {
			int id = useritem.get(n);
			if (itemitem.contains(id))
				itemitem.remove(new Integer(id));
			n++;
		}
		return itemitem;
	}

	private static Map<Integer, Map<Integer, Double>> comSimilarity(Map<Integer, Map<Integer, Integer>> relationmap,
			HashMap<Integer, Integer> everyusernum) {
		Map<Integer, Map<Integer, Double>> similarity = new HashMap<Integer, Map<Integer, Double>>();
		Set<Entry<Integer, Map<Integer, Integer>>> entry = relationmap.entrySet();
		for (Entry<Integer, Map<Integer, Integer>> e : entry) {
			int firstid = e.getKey();
			Map<Integer, Integer> value = e.getValue();
			Set<Entry<Integer, Integer>> ee = value.entrySet();
			Map<Integer, Double> mm = new HashMap<Integer, Double>();
			for (Entry<Integer, Integer> temp : ee) {
				int secondid = temp.getKey();
				int commonnum = temp.getValue();
				double sim = (double) commonnum / (double) Math.sqrt(everyusernum.get(firstid) * everyusernum.get(secondid));
				mm.put(secondid, sim);
			}
			similarity.put(firstid, mm);
		}
		return similarity;
	}

	public HashMap<Integer, Integer> itemNum(Map<Integer, List<Integer>> datamap) {

		HashMap<Integer, Integer> everyusernum = new HashMap<Integer, Integer>();
		Set<Entry<Integer, List<Integer>>> entry = datamap.entrySet();
		for (Entry<Integer, List<Integer>> e : entry) {
			List<Integer> list = e.getValue();
			int i = 0;
			while (i < list.size()) {
				int firstid = list.get(i);
				if (!everyusernum.containsKey(firstid)) {
					everyusernum.put(firstid, 1);
				} else {
					int num = everyusernum.get(firstid) + 1;
					everyusernum.put(firstid, num);
				}
				i++;
			}
		}
		return everyusernum;
	}

	public Map<Integer, List<Integer>> dataProcess(File file) throws Exception {
		Map<Integer, List<Integer>> hm = new HashMap<Integer, List<Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			int userid = Integer.parseInt(arr[0]);
			int itemid = Integer.parseInt(arr[1]);

			if (!hm.containsKey(userid)) {
				List<Integer> list = new ArrayList<Integer>();
				list.add(itemid);
				hm.put(userid, list);
			} else {
				List<Integer> templist = hm.get(userid);
				templist.add(itemid);
				hm.put(userid, templist);
			}
		}
		br.close();
		return hm;
	}

	public Map<Integer, Map<Integer, Integer>> itemToItem(Map<Integer, List<Integer>> datamap) {
		Map<Integer, Map<Integer, Integer>> relationmap = new HashMap<Integer, Map<Integer, Integer>>();
		Set<Entry<Integer, List<Integer>>> entry = datamap.entrySet();
		for (Entry<Integer, List<Integer>> e : entry) {
			List<Integer> list = e.getValue();
			int i = 0;
			while (i < list.size()) {
				int firstid = list.get(i);
				if (!relationmap.containsKey(firstid)) {
					Map<Integer, Integer> innermap = new HashMap<Integer, Integer>();
					relationmap.put(firstid, innermap);
				}
				Map<Integer, Integer> tempinnermap = relationmap.get(firstid);
				int j = i + 1;
				while (j < list.size()) {
					int secondid = list.get(j);
					if (!tempinnermap.containsKey(secondid)) {
						tempinnermap.put(secondid, 1);
					} else {
						int num = tempinnermap.get(secondid) + 1;
						tempinnermap.put(secondid, num);
					}
					j++;
				}
				i++;
			}
		}
		return relationmap;
	}
}
