package dblpParser;

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


public class Parser2 {
	public static void main(String[] args){
		try {	
			File inputFile = new File("../test/dblp.xml");

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

            int maxYear = Integer.parseInt(args[0]);
			UserHandler userhandler = new UserHandler(maxYear);
			saxParser.parse(inputFile, userhandler);
			
			PrintWriter idAndConf = null;
			PrintWriter idAndSen = null;
			PrintWriter idAndName = null;
			PrintWriter idAndCommonConfs = null;
			PrintWriter idAndCommonFields = null;
			PrintWriter idAndField = null;

			try{
				idAndConf = new PrintWriter("idAndConf.txt","UTF-8");
				idAndCommonConfs = new PrintWriter("idAndCommonConfs.txt","UTF-8");
				idAndField = new PrintWriter("idAndField.txt","UTF-8");
				idAndCommonFields = new PrintWriter("idAndCommonFields.txt","UTF-8");
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

				idAndCommonConfs.print(p.getID());
                ArrayList<Pair> confs = p.getXMostCommonConferences(3);
                for(Pair pr : confs){
                    idAndCommonConfs.print("\t" + pr.getConf());
                }
				idAndCommonConfs.print("\n");

				idAndField.println(p.getID() + "\t" + p.getMostCommonField());
				
				idAndCommonFields.print(p.getID());
                ArrayList<Pair> fields = p.getXMostCommonFields(3);
                for(Pair pr : fields){
                    idAndCommonFields.print("\t" + pr.getConf());
                }
				idAndCommonFields.print("\n");
			}
			idAndConf.close();
			idAndSen.close();
			idAndName.close();
            idAndField.close();
            idAndCommonFields.close();
            idAndCommonConfs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}


class UserHandler extends DefaultHandler {
	int MAX_YEAR;
	boolean insideConf = false;
	int inproceedingsCount = 0;
	String content;
	String year;
	boolean insidePerson;
	HashMap<String,Integer> nameToID = new HashMap<String,Integer>();
	int maxID = 0;
	ArrayList<Integer> persons;
	String key, elementName;
 
	PersonCollection personCollection = new PersonCollection();
	String pubType, confName;
	boolean foundInproceedings=false;
	HashSet<String> conferences = new HashSet<String>();
    HashMap<String,String> confToField = new HashMap<String,String>();

	PrintWriter edgeListWriter;

	//PrintWriter debug = new PrintWriter("debug.txt","UTF-8");


	public UserHandler(int maxYear){
		MAX_YEAR = maxYear;
		try{
			//PrintWriter conferencesList = new PrintWriter("conferencesList.txt","UTF-8");
			BufferedReader br = new BufferedReader(new FileReader("conferencesList.txt"));
			String line;
			while((line = br.readLine()) != null){
                String[] parts = line.split(" ");
				//System.out.println(line);
				conferences.add(parts[0]);
                confToField.put(parts[0],parts[1]);
			}
			
			edgeListWriter = new PrintWriter("edgeList.txt","UTF-8");

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public PersonCollection getPersonCollection(){
		return personCollection;
	}

	@Override
	public void startElement(String uri, String localName, String eName, Attributes attributes) throws SAXException {
		elementName = eName;
		String k = attributes.getcontent("key");
		if (insidePerson = (elementName.equals("author") || elementName.equals("editor"))) {
			content = "";
			return;
		}

		// START OF INPROCEEDINGS ELEMENT
		if((attributes.getLength()>0) && k != null){
			year = Integer.parseInt(attributes.getcontent("mdate").split("-")[0]);
			pubType = k.split("/")[0];
			confName = k.split("/")[1];

			if( year < MAX_YEAR || !conferences.contains(confName)) {
				return;
			}

			if(pubType.equals("conf") && elementName.equals("inproceedings")){
				foundInproceedings=true;
				insideConf = true;
				
				persons = new ArrayList<Integer>();
			}
    	}
    }

    @Override
    public void endElement(String uri, String localName, String eName) throws SAXException {
    	if ((eName.equals("author") || eName.equals("editor")) && insideConf){	// author or editor tag inside inproceedings tag
			String conferenceName = confName;
			Person p;
			if ((p = PersonCollection.getPersonIfExists(content)) == null){
				// person doesn't exist already in collection
				p = PersonCollection.putPersonInCollection(content);
			}
			p.addConference(conferenceName);
            p.addField(confToField.get(conferenceName));
			p.addYear(year);
			persons.add(p.getID());
			insidePerson=false;
    	}	
    	else if (eName.equals(elementName) && insideConf){		// closing element is inproceedings
    		inproceedingsCount = inproceedingsCount + 1;
    		System.out.print(inproceedingsCount + "\r");
    		
    		// persons is an arraylist of all ids of persons in the currently closing inproceedings record.

    		if (persons.isEmpty()){
    			return;
    		}
			
    		printEdges(persons);

    		insideConf = false;
    	}
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
    	if (insidePerson)
    		content += new String(ch, start, length);
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
