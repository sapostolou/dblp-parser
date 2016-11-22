package dblpParser;

import java.util.ArrayList;

public class PersonCollection{
	private static ArrayList<Person> collection;
	private static Integer maxID;

	public PersonCollection(){
		collection = new ArrayList<Person>();
		maxID =0;
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
		return p;
	}

	public ArrayList<Person> getCollection(){
		return collection;
	}
}