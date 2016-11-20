package dblpParser3;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Parser {
	public static void main(String[] args){
		try {	
			File inputFile = new File("dblp.xml");

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
            int maxYear = Integer.parseInt(args[0]);
			UserHandler userhandler = new UserHandler(maxYear);
			saxParser.parse(inputFile, userhandler);
			
			PrintWriter idAndConf = null;
			PrintWriter idAndSen = null;
			PrintWriter idAndName = null;

			try{
				idAndConf = new PrintWriter("idAndConf.txt","UTF-8");
				idAndSen = new PrintWriter("seniority.txt","UTF-8");
				idAndName = new PrintWriter("idToName.txt","UTF-8");
			} catch (Exception e){
				e.printStackTrace();
			}
			PersonCollection personCollection = userhandler.getPersonCollection();
			for (Person p : personCollection.getCollection()){
				idAndName.println(p.getID() + "\t" + p.getName());
				idAndConf.println(p.getID() + "\t" + p.getMostCommonConf());
				idAndSen.println(p.getID() + "\t" + p.getAvg());
			}
			idAndConf.close();
			idAndSen.close();
			idAndName.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}


class UserHandler extends DefaultHandler {
	int MAX_YEAR;
	boolean bFirstName = false;
	boolean bLastName = false;
	boolean bNickName = false;
	boolean bMarks = false;
	boolean insideConf = false;
	int count =0;
	String Value;
	String year;
	boolean insidePerson;
	HashMap<String,Integer> nameToID = new HashMap<String,Integer>();
	int maxID = 0;
	ArrayList<Integer> persons;
	String key, recordTag;
	int yearOfPub;
 
	PersonCollection personCollection = new PersonCollection();
	String pubType, confName;
	boolean foundInproceedings=false;
	HashSet<String> conferences = new HashSet<String>();

	PrintWriter edgeListWriter;

	//PrintWriter debug = new PrintWriter("debug.txt","UTF-8");


	public UserHandler(int maxYear){
		MAX_YEAR=maxYear;
		try{
			//PrintWriter conferencesList = new PrintWriter("conferencesList.txt","UTF-8");
			BufferedReader br = new BufferedReader(new FileReader("conferencesList.txt"));
			String line;
			while((line = br.readLine()) != null){
				System.out.println(line);
				conferences.add(line);
			}
			//personList = new ArrayList<Person>();
			/*conferences.add("kdd");
			conferences.add("icde");
			conferences.add("www");
			conferences.add("sigmod");
			conferences.add("sdm");
			conferences.add("vldb");
			conferences.add("cikm");
			conferences.add("icdm");
			conferences.add("pkdd");
			conferences.add("wsdm");
			/////////////////////////////////// 10 conferences mark
			conferences.add("infocom");
			conferences.add("siggraph");
			conferences.add("cvpr");
			conferences.add("stoc");
			conferences.add("focs");
			conferences.add("chi");
			conferences.add("mobicom");
			conferences.add("aaai");
			conferences.add("popl");*/
			
			/////////////////////////////////// 20 conferences mark
			
			edgeListWriter = new PrintWriter("edgeList.txt","UTF-8");

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public PersonCollection getPersonCollection(){
		return personCollection;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// START OF AUTHOR OR EDITOR ELEMENT
		String k;
		if (insidePerson = (qName.equals("author") || qName.equals("editor"))) {
			Value = "";
			return;
		}

		// START OF INPROCEEDINGS ELEMENT
		if((attributes.getLength()>0) && (k = attributes.getValue("key"))!=null){
			year = attributes.getValue("mdate").split("-")[0];
			/*if(Integer.parseInt(year)<2011){
				return;
			}*/
			//String k = attributes.getValue("key");
			pubType = k.split("/")[0];
			confName = k.split("/")[1];
			if( Integer.parseInt(year)<MAX_YEAR || !conferences.contains(confName)) {
				return;
			}
			yearOfPub = Integer.parseInt(year);
			if(pubType.equals("conf") && qName.equals("inproceedings")){
				foundInproceedings=true;
				insideConf = true;
				/*key = confName;*/
				recordTag = qName;
				//conferences.add(confName);
				persons = new ArrayList<Integer>();
			}
    	}
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if ((qName.equals("author") || qName.equals("editor")) && insideConf){	// author or editor tag inside inproceedings tag
			String conferenceName = confName;
			Person p;
			if ((p = PersonCollection.getPersonIfExists(Value)) == null){
				// person doesn't exist already in collection
				p = PersonCollection.putPersonInCollection(Value);
			}
			p.addConference(conferenceName);
			p.addYear(year);
			persons.add(p.getID());
			insidePerson=false;
    	}	
    	else if (qName.equals(recordTag) && insideConf){		// closing element is inproceedings
    		count = count +1;
    		System.out.print(count + "\r");
    		// number of inproceedings records is 1252573
    		/*if ( count ==1252573 ){
    			PrintWriter idAndConf = null;
    			PrintWriter idAndSen = null;
    			PrintWriter idAndName = null;

    			try{
    				idAndConf = new PrintWriter("idAndConf.txt","UTF-8");
    				idAndSen = new PrintWriter("seniority.txt","UTF-8");
    				idAndName = new PrintWriter("idToName.txt","UTF-8");
    			} catch (Exception e){
    				e.printStackTrace();
    			}

				for (Person p : personCollection.getCollection()){
					idAndName.println(p.getID() + "\t" + p.getName());
					idAndConf.println(p.getID() + "\t" + p.getMostCommonConf());
					idAndSen.println(p.getID() + "\t" + p.getAvg());
				}
				idAndConf.close();
				idAndSen.close();
				idAndName.close();

	    		throw new MySAXTerminatorException();
    		}*/
    		//insideConf=false;

    		// persons is an arraylist of all ids of persons in the currently closing inproceedings record.

    		if (persons.isEmpty()){
    			return;
    		}
    		/*for (Integer i : persons){
    			if (IDtoConfCount.containsKey(one)){
    				personMap = IDtoConfCount.getValue(one);
    				if(personMap.containsKey(recordTag)){
    					personMap.put(recordTag,personMap.get(recordTag)+1);
    				}
    				else{
    					personMap.put(recordTag,1);
    				}
    			}
    			else{
    				tempMap = new HashMap<String,Integer>();
    				tempMap.put(recordTag,1);
    				IDtoConfCount.put(one,tempMap);
    			}

    		}*/
    		// if(yearOfPub>=2015){
    		// 	printEdges(persons);
    		// }
    		// yearOfPub=0;
    		printEdges(persons);
    		insideConf = false;
    	}
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
    	if (insidePerson)
    		Value += new String(ch, start, length);
    }

    void printEdges(ArrayList<Integer> list){
    	for(int i=0;i<list.size();i++){
    		for(int j=i+1;j<list.size();j++){
    			int one = list.get(i);
    			int two = list.get(j);
    			if (one <= two){
    				edgeListWriter.println(one + "\t" + two);
    			}
    			else{
    				edgeListWriter.println(two + "\t" + one);
    			}
    		}
    	}
    }
}
