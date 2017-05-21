package dblpParser;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class Person{
	String name;
	Integer ID;
	HashMap<String,Integer> conferences;
	HashMap<String,Integer> fields;

	String mostCommonConf;
	Integer mostCommonConfCount;

	String mostCommonField;
	Integer mostCommonFieldCount;
	HashMap<String,Integer> yearsCount;

	Integer oldestPublicationYear, newestPublicationYear;

	public Person(String n, Integer id){
		name = n;
		ID = id;
		conferences = new HashMap<String,Integer>();
		fields = new HashMap<String,Integer>();
		yearsCount = new HashMap<String,Integer>();
		mostCommonConfCount =0;
		mostCommonConf = null;
		mostCommonFieldCount =0;
		mostCommonField = null;
		oldestPublicationYear = null;
		newestPublicationYear = null;

	Integer getCareerYears(){
		return newestPublicationYear - oldestPublicationYear;
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

	public String getMostCommonField(){
		return mostCommonField;
	}

	public ArrayList<Pair> getXMostCommonConferences(int X){
		ArrayList<Pair> XMostCommon = new ArrayList<Pair>(X);
		HashMap<String,Integer> mapCopy = new HashMap<String,Integer>(conferences);

		int max = 0;
		String maxKey=null;

		if(mapCopy.size()<X){
			int ind = 0;
			for(Map.Entry<String,Integer> pair : mapCopy.entrySet()){
				XMostCommon.add(ind,new Pair(pair.getKey(), pair.getValue()));
				ind +=1;
			}
		}
		else{
			for(int j=0;j<X;j++){
				max=0;

				for(Map.Entry<String,Integer> pair : mapCopy.entrySet()){
					if(pair.getValue() >= max){
						max = pair.getValue();
						maxKey = pair.getKey();
					}
				}
				Pair maxPair = new Pair(maxKey, max);
				XMostCommon.add(j,maxPair);
				mapCopy.remove(maxKey);

			}
		}
		
		return XMostCommon;
	}

	public ArrayList<Pair> getXMostCommonFields(int X){
		ArrayList<Pair> XMostCommon = new ArrayList<Pair>(X);
		HashMap<String,Integer> mapCopy = new HashMap<String,Integer>(fields);

		int max = 0;
		String maxKey=null;

		if(mapCopy.size()<X){
			int ind = 0;
			for(Map.Entry<String,Integer> pair : mapCopy.entrySet()){
				XMostCommon.add(ind,new Pair(pair.getKey(), pair.getValue()));
				ind +=1;
			}
		}
		else{
			for(int j=0;j<X;j++){
				max=0;

				for(Map.Entry<String,Integer> pair : mapCopy.entrySet()){
					if(pair.getValue() >= max){
						max = pair.getValue();
						maxKey = pair.getKey();
					}
				}
				Pair maxPair = new Pair(maxKey, max);
				XMostCommon.add(j,maxPair);
				mapCopy.remove(maxKey);

			}
		}
		
		return XMostCommon;
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

	void addField(String n){
		// check if conference already in map
		Integer count;
		if(fields.containsKey(n)){
			// if existent update value
			count = fields.get(n) + 1;
			fields.put(n,count);
		}
		else{
			// if not existent put in map
			fields.put(n,1);
			count = 1;
		}
		if (count>mostCommonFieldCount){
			mostCommonField = n;
			mostCommonFieldCount = count;
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

		Integer year = Integer.parseInt(y);

		if( oldestPublicationYear == null || year < oldestPublicationYear){
			oldestPublicationYear = year;
	}

		if( newestPublicationYear == null || year > newestPublicationYear){
			newestPublicationYear = year;
		}
	}
		int sum =0;
		int count=0;
		for(Integer s : yearsCount.values()){
			count += 1;
			sum += s;
		}
		float avg = (float)sum / count;
		return avg;
	}
}