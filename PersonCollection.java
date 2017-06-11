package dblpParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.PrintWriter;
import java.util.Arrays;

public class PersonCollection{
	private static ArrayList<Person> collection;
	private static Integer maxID;
	private static Integer count;

	public PersonCollection(){
		collection = new ArrayList<Person>();
		maxID =0;
		count=0;
	}

	public static Person getPersonIfExists(String n){
		for (Person p : collection){
			if (p.getName().equals(n)){
				return p;
			}
		}
		return null;
	}

	public static Person putPersonInCollection(String n){
		Person p = new Person(n,maxID);
		maxID = maxID +1;
		collection.add(p);
		count = count + 1;
		return p;
	}

	public ArrayList<Person> getCollection(){
		return collection;
	}

	public Integer getCount(){
		return count;
	}

	public void writeSeniorityHistogram(PrintWriter w){
		HashMap<Integer,Integer> h = new HashMap<Integer,Integer>();

		// Notes: 3.1 to 3.9 seniority maps to 4 in histogram

		for (Person p : collection){
			float sen = p.getConfCountAvgPerYear();
			Integer ceiling = Math.toIntExact(Math.round(Math.ceil(sen)));
			Integer currentCount = h.get(ceiling);
			if(currentCount == null){
				h.put(ceiling,1);
			}
			else{
				h.put(ceiling,currentCount+1);
			}
		}

		for(int y : h.keySet()){
			w.println(Integer.toString(y)+","+Integer.toString(h.get(y)));
		}
		w.close();

	}
}