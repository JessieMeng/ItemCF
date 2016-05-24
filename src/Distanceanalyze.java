import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class Distanceanalyze {
	public static void main(String[] args){
	File file = new File("dataset_TSMC2014_NYC.txt");
	File fileout = new File("locationdistanceminus.csv");
    BufferedReader reader = null;
    BufferedWriter writer = null;
    long mil = System.currentTimeMillis();
    try{
    	reader = new BufferedReader(new FileReader(file));
    	writer = new BufferedWriter(new FileWriter(fileout));
    	HashMap<String,List<Checkin>> hm = new HashMap<String,List<Checkin>>();
        String tempString = null;
    	while((tempString = reader.readLine())!=null){
    		String[] arr = tempString.split("\t");
    		
    		if(hm.containsKey(arr[0])){
    		    Checkin checkin = new Checkin();
    		    checkin.venueid = arr[1];
    			checkin.longtitude = arr[4];
    			checkin.latitude = arr[5];
    			List<Checkin> li = hm.get(arr[0]);
    			li.add(checkin);
    			hm.put(arr[0], li);
    		}else{
    			List<Checkin> list = new ArrayList<Checkin>();
    			Checkin checkin = new Checkin();
    		    checkin.venueid = arr[1];
    			checkin.longtitude = arr[4];
    			checkin.latitude = arr[5];
    			list.add(checkin);
    			hm.put(arr[0], list);
    		}
    	}
    	Set<Entry<String,List<Checkin>>> entry = hm.entrySet();
    	Iterator<Entry<String,List<Checkin>>> ite = entry.iterator();
    	List<Double> ld  = new ArrayList<Double>();
    	 while(ite.hasNext()){
    			Entry<String,List<Checkin>> en = ite.next();
    			if(en.getKey().equals("772")){
    			  List<Checkin> ll = en.getValue();
    			  
    		      for(int i = 0 ;i < ll.size();i++){
    			  
    			     for(int j = i+1 ; j< ll.size();j++){
    				
    				double minus = discom(ll.get(i),ll.get(j));
    				System.out.println(minus);
    				ld.add(minus);
    			//writer.write(String.valueOf(minus));
    				//writer.newLine();
    			}
    			break;
    		}
    	 }
    	}
    	Collections.sort(ld);
        for(int k = 0;k < ld.size() ; k++){
    	   writer.write(String.valueOf(ld.get(k)));
    	   writer.newLine();
        } 
    }catch(IOException e){
    	e.printStackTrace();
    }finally{
    	if(reader!=null){
    		try{
    			reader.close();
    		}catch(IOException e1){
    			e1.printStackTrace();
    		}
    	}
    	if(writer!=null){
    		try{
    			writer.close();
    		}catch(IOException e1){
    			e1.printStackTrace();
    		}
    	}
    }
	}
	public static double discom(Checkin c1,Checkin c2){
		
		double earth = 6378137;
		double radLat1 = rad(Double.parseDouble(c1.latitude));
		double radLat2 = rad(Double.parseDouble(c2.latitude));
		double a = radLat1-radLat2;
		
		double b = rad(Double.parseDouble(c1.longtitude))-rad(Double.parseDouble(c2.longtitude));
		
		double s = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2)+
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2), 2)));
		s = s * earth;
       
		return s;
	}
	public static double rad(double d){
		return d*Math.PI/180.0;
	}
}
class Checkin{
	String venueid;
	String longtitude;//jingdu
	String latitude;//weidu
	String time;
	
}
