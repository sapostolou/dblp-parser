package dblpParser3;

import java.util.HashMap;

public class Pair{
	private conf, freq;
	public Pair(conf,freq){
		this.conf = conf;
		this.freq = freq;
	}

	public getConf(){
		return conf;
	}

	public getFreq(){
		return freq;
	}
}

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

	public Person(String n, Integer id){
		name = n;
		ID = id;
		conferences = new HashMap<String,Integer>();
		yearsCount = new HashMap<String,Integer>();
		mostCommonConfCount =0;
		mostCommonConf = null;
		mostCommonFieldCount =0;
		mostCommonField = null;
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

	public String getXMostCommonConferences(int X){
		List<Pair> XMostCommon = new ArrayList<Pair>();
		Map<String,Integer> copy = new HashMap<String,Integer>(conferences);

		int max = 0, maxKey;

		for(int j=0;j<X;j++){
			max=0;

			for(copy.Entry<String,Integer> pair : copy.entrySet()){
				if(pair.getValue() >= max){
					max = pair.getValue();
					maxKey = pair.getKey();
				}
			}
			Pair maxPair = new Pair(maxKey, max);
			XMostCommon[j]=maxPair;
			copy.remove(maxKey);

		}
		return XMostCommon;
	}

	public String getXMostCommonFields(int X){
		List<Pair> XMostCommon = new ArrayList<Pair>();
		Map<String,Integer> copy = new HashMap<String,Integer>(fields);

		int max = 0, maxKey;

		for(int j=0;j<X;j++){
			max=0;

			for(copy.Entry<String,Integer> pair : copy.entrySet()){
				if(pair.getValue() >= max){
					max = pair.getValue();
					maxKey = pair.getKey();
				}
			}
			Pair maxPair = new Pair(maxKey, max);
			XMostCommon[j]=maxPair;
			copy.remove(maxKey);

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