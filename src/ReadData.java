import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class ReadData {

	public static void main(String[] args) {
		File file = new File("dataset_TSMC2014_NYC.txt");
		File fileout = new File("datalocation.csv");
		BufferedReader reader = null;
		BufferedWriter writer = null;
		long mil = System.currentTimeMillis();
		try {
			reader = new BufferedReader(new FileReader(file));
			writer = new BufferedWriter(new FileWriter(fileout));
			String tempString = null;
			HashMap<String, Integer> hm = new HashMap<String, Integer>();
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			int number = 0;
			while ((tempString = reader.readLine()) != null) {
				number++;
				String[] arr = tempString.split("\t");
				if (hm.containsKey(arr[0])) {
					int count = hm.get(arr[0]);
					hm.put(arr[0], ++count);
				} else {
					hm.put(arr[0], 1);
				}
				if (map.containsKey(arr[1])) {
					int count = map.get(arr[1]);
					map.put(arr[1], ++count);
				} else {
					map.put(arr[1], 1);
				}
			}
			System.out.println(number);
			Set<Entry<String, Integer>> entry = hm.entrySet();
			Set<Entry<String, Integer>> et = map.entrySet();
			int[] location = new int[1083];
			int[] user = new int[et.size()];
			int i = 0;
			for (Entry<String, Integer> entry1 : entry) {
				// System.out.println("user:  "+entry1.getKey()+"   visited   "+entry1.getValue()+" locations");
				location[i++] = entry1.getValue();
			}
			int t = 0;
			for (Entry<String, Integer> entry1 : et) {
				// System.out.println("location: "+
				// entry1.getKey()+"   visited by "+entry1.getValue()+" users");
				user[t++] = entry1.getValue();
			}
			Arrays.sort(location);
			Arrays.sort(user);
			System.out.println(map.size());
			// 用户访问位置的频数的百分位
			// for(int j = 1 ;j <= 1083 ;j++){
			// BigDecimal big1 = new BigDecimal(j);
			// BigDecimal big2 = new BigDecimal(1083);
			// BigDecimal big3 = new BigDecimal(100);
			// BigDecimal percent =
			// big1.divide(big2,5,BigDecimal.ROUND_HALF_UP);
			// BigDecimal percentage = percent.multiply(big3);
			// System.out.println(location[j-1]+"  "+percentage+"%");
			// }
			// 位置被多个用户访问的百分位
			// for(int j = 1 ;j <= map.size() ;j++){
			// BigDecimal big1 = new BigDecimal(j);
			// BigDecimal big2 = new BigDecimal(map.size());
			// BigDecimal big3 = new BigDecimal(100);
			// BigDecimal percent =
			// big1.divide(big2,5,BigDecimal.ROUND_HALF_UP);
			// BigDecimal percentage = percent.multiply(big3);
			// System.out.println(user[j-1]+"  "+percentage+"%");
			// }
			// 打印出各个百分位的频数
			int pos = 0;
			for (float a = 0; a <= 1; a += 0.01) {
				pos = (int) (map.size() * a);
				System.out.println(pos + "对应的频数：" + user[pos]);
				writer.write(String.valueOf(user[pos]));
				writer.newLine();
			}
			// int pos = 0;
			// for(float a = 0 ;a <= 1; a+=0.01){
			// pos = (int)(hm.size()*a);
			// System.out.println(pos+"对应的频次："+location[pos]);
			// writer.write(String.valueOf(location[pos]));
			// writer.newLine();
			// }

			long mil1 = System.currentTimeMillis();
			System.out.println((mil1 - mil) / 1000);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
