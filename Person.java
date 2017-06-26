package dblpParser;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class Person{
	String name;
	Integer ID;

	HashMap<String,Integer> conferences; // mapping of conferences to number of publications in this conferences
	HashMap<String,Integer> fields; // mapping of fields to number of publications in this field
	HashMap<Integer,Integer> yearsCount; // mapping of years to number of publications in this year

	String mostCommonConf;
	Integer mostCommonConfCount;

	String mostCommonField;
	Integer mostCommonFieldCount;

	Integer totalConfCount;

	Integer oldestPublicationYear, newestPublicationYear;

	public Person(String n, Integer id){
		name = n;
		ID = id;
		conferences = new HashMap<String,Integer>();
		fields = new HashMap<String,Integer>();
		yearsCount = new HashMap<Integer,Integer>();
		mostCommonConfCount =0;
		mostCommonConf = null;
		mostCommonFieldCount =0;
		mostCommonField = null;
		oldestPublicationYear = null;
		newestPublicationYear = null;
		totalConfCount = 0;
	}

	Integer getCareerYears(){
		return newestPublicationYear - oldestPublicationYear + 1;
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

	public float getConfCountAvgPerActiveYear(){
		return totalConfCount / getCareerYears();
	}

	public String getMostCommonConf(){
		return mostCommonConf;
	}

	public String getMostCommonField(){
		return mostCommonField;
	}

	public void addSeniorityYear(Integer y){
		if( oldestPublicationYear == null || y < oldestPublicationYear){
			oldestPublicationYear = y;
		}

		if( newestPublicationYear == null || y > newestPublicationYear){
			newestPublicationYear = y;
		}

		totalConfCount = totalConfCount + 1;
	}

	public void addPublication(String confName, String fieldName, Integer y){

		// PROCESS CONFERENCE NAME
		// check if conference already in map
		Integer count;
		if(conferences.containsKey(confName)){
			// if existent update value
			count = conferences.get(confName) + 1;
			conferences.put(confName,count);
		}
		else{
			// if not existent put in map
			conferences.put(confName,1);
			count = 1;
		}
		if (count>mostCommonConfCount){
			mostCommonConf = confName;
			mostCommonConfCount = count;
		}

		// PROCESS FIELD NAME
		// check if field already in map
		if(fields.containsKey(fieldName)){
			// if existent update value
			count = fields.get(fieldName) + 1;
			fields.put(fieldName,count);
		}
		else{
			// if not existent put in map
			fields.put(fieldName,1);
			count = 1;
		}
		if (count>mostCommonFieldCount){
			mostCommonField = fieldName;
			mostCommonFieldCount = count;
		}

		// PROCESS YEAR
		// Update yearsCount
		count = yearsCount.get(y);
		if( count == null){
			yearsCount.put(y,1);
		}
		else{
			count=count+1;
			yearsCount.put(y,count);
		}

		if( oldestPublicationYear == null || y < oldestPublicationYear){
			oldestPublicationYear = y;
		}

		if( newestPublicationYear == null || y > newestPublicationYear){
			newestPublicationYear = y;
		}

		totalConfCount = totalConfCount + 1;

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

	Float getConfCountAvgPerYear(){
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