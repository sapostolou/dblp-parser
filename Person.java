package dblpParser3;

import java.util.HashMap;

public class Person{
	String name;
	Integer ID;
	HashMap<String,Integer> conferences;
	String mostCommonConf;
	Integer mostCommonConfCount;
	HashMap<String,Integer> yearsCount;

	public Person(String n, Integer id){
		name = n;
		ID = id;
		conferences = new HashMap<String,Integer>();
		yearsCount = new HashMap<String,Integer>();
		mostCommonConfCount =0;
		mostCommonConf = null;
	}

	HashMap<String,Integer> getConf(){
		return conferences;
	}

	Integer getID(){
		return ID;
	}

	public String getName(){
		return name;
	}

	public String getMostCommonConf(){
		return mostCommonConf;
	}

	void addConference(String n){
		// check if conference already in map
		Integer count;
		if(conferences.containsKey(n)){
			// if existent update value
			count = conferences.get(n) + 1;
			conferences.put(n,count);
		}
		else{
			// if not existent put in map
			conferences.put(n,1);
			count = 1;
		}
		if (count>mostCommonConfCount){
			mostCommonConf = n;
			mostCommonConfCount = count;
		}
	}

	void addYear(String y){
		Integer count = yearsCount.get(y);
		if( count == null){
			yearsCount.put(y,1);
		}
		else{
			count=count+1;
			yearsCount.put(y,count);
		}
	}

	Float getAvg(){
		int sum =0;
		int count=0;
		for(Integer s : yearsCount.values()){
			count += 1;
			sum += s;
		}
		float avg = (float)sum/count;
		/*if(avg > 10 ){
			return 1;
		}
		else{
			return 0;
		}*/
		return avg;
	}
}