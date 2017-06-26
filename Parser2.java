package dblpParser;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import java.text.Format;

import java.time.*;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Parser2 {
    public static void main(String[] args){
        try {

            // Path to config file
            // File configFile = new File("dblpParser/config.xml");


            // Initialize the config handler
            ConfigHandler configHandler = new ConfigHandler("dblpParser/config.txt");

            // Parse config
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // SAXParser configParser = factory.newSAXParser();
            // configParser.parse(configFile, configHandler);

            // // Get MAX_YEAR from command line
            // int maxYear = Integer.parseInt(args[0]);

            // Initialize the dbml xml handler
            UserHandler userhandler = new UserHandler(configHandler);

            // Path to dblp.xml
            File inputFile = new File(configHandler.getDataPath());

            // Parse dblp xml
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputFile, userhandler);

            // Initialize output file writers
            PrintWriter idAndSen = null; // author id and seniority (numerical or nominal)
            PrintWriter idAndSen2 = null; // author id and seniority (numerical or nominal)
            PrintWriter idAndSen3 = null; // author id and seniority (numerical or nominal)
            PrintWriter idAndSen4 = null; // author id and seniority (numerical or nominal)
            PrintWriter idAndName = null; // author id and name
            PrintWriter idAndCommonConfs = null; // author id and most common conferences (multiple)
            PrintWriter idAndCommonFields = null; // author id and most common fields (multiple)
            PrintWriter stats = null;
            PrintWriter hist = null;

            String path = configHandler.getPath();

            try{
                idAndCommonConfs = new PrintWriter(path+"/idAndCommonConfs.txt","UTF-8");
                idAndCommonFields = new PrintWriter(path+"/idAndCommonFields.txt","UTF-8");
                // idAndSen = new PrintWriter(path+"/idAndAvgConfsPerYear.txt","UTF-8");
                idAndSen2 = new PrintWriter(path+"/idAndAvgConfsPerYearActive.txt","UTF-8");
                // idAndSen3 = new PrintWriter(path+"/idAndAvgConfsPerYearNominal.txt","UTF-8");
                idAndSen4 = new PrintWriter(path+"/idAndAvgConfsPerYearActiveNominal.txt","UTF-8");
                idAndName = new PrintWriter(path+"/idToName.txt","UTF-8");
                stats = new PrintWriter(path+"/stats.txt","UTF-8");
                hist = new PrintWriter(path+"/hist.csv","UTF-8");
            } 
            catch (Exception e){
                e.printStackTrace();
            }

            // Get the persons collection
            PersonCollection personCollection = userhandler.getPersonCollection();

            Integer countLow=0, countMed=0, countHigh=0; // used to count seniority of people

            // For every person fill up the output files
            for (Person p : personCollection.getCollection()){

                idAndName.println(p.getID() + "\t" + p.getName());

                // idAndSen.println(p.getID() + "\t" + p.getConfCountAvgPerYear());
                // idAndSen3.println(p.getID() + "\t" + configHandler.convertNumericalToNominalSeniority(p.getConfCountAvgPerYear()));
                float senNum = p.getConfCountAvgPerActiveYear();
                idAndSen2.println(p.getID() + "\t" + senNum);
                String senNom = configHandler.convertNumericalToNominalSeniority(senNum);
                switch(senNom){
                    case "low":
                        countLow = countLow + 1;
                        break;
                    case "med":
                        countMed = countMed + 1;
                        break;
                    case "high":
                        countHigh = countHigh + 1;
                        break;
                }
                idAndSen4.println(p.getID() + "\t" + senNom);

                idAndCommonConfs.print(p.getID());
                ArrayList<Pair> confs = p.getXMostCommonConferences(configHandler.getNumberOfSkillsPerWorker());
                for(Pair pr : confs){
                    idAndCommonConfs.print("\t" + pr.getConf());
                }
                idAndCommonConfs.print("\n");
                
                idAndCommonFields.print(p.getID());
                ArrayList<Pair> fields = p.getXMostCommonFields(configHandler.getNumberOfSkillsPerWorker());
                for(Pair pr : fields){
                    idAndCommonFields.print("\t" + pr.getConf());
                }
                idAndCommonFields.print("\n");
            }

            stats.println("Number of persons: "+personCollection.getCount());
            stats.println("Conference records examined: "+userhandler.inproceedingsCount);
            stats.println("Total edges: "+userhandler.edgesPrinted);
            stats.println("Persons with low seniority (upper bound : "+configHandler.lowSeniorityUpperBound+"): "+countLow);
            stats.println("Persons with medium seniority (upper bound : "+configHandler.medSeniorityUpperBound+"): "+countMed);
            stats.println("Persons with high seniority: "+countHigh);
            configHandler.writeConfigToNewDir();
            configHandler.writeConfFileToNewDir();

            personCollection.writeSeniorityHistogram(hist);

            // Close output writers
            stats.close();
            // idAndSen.close();
            idAndSen2.close();
            // idAndSen3.close();
            idAndSen4.close();
            idAndName.close();
            idAndCommonFields.close();
            idAndCommonConfs.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ConfigHandler{
    Integer fromDate;
    Integer toDate;
    String datasetPath;
    int numberOfSkillsPerWorker;
    String pathToConferencesList;
    String currentDateTime;
    String content;
    String allContents, confFileContents;
    Integer lowSeniorityUpperBound;
    Integer medSeniorityUpperBound;

    Integer getFromDate(){
        return fromDate;
    }

    Integer getToDate(){
        return toDate;
    }

    String getDirName(){
        return fromDate.toString() + "-" + toDate.toString();
    }

    String getPath(){
        return "./"+fromDate.toString() + "-" + toDate.toString()+"/"+currentDateTime;
    }

    String getDataPath(){
        return datasetPath;
    }

    String getConferencesListPath(){
        return pathToConferencesList;
    }

    String getCurrentDateTime(){
        return currentDateTime;
    }

    int getNumberOfSkillsPerWorker(){
        return numberOfSkillsPerWorker;
    }

    public String convertNumericalToNominalSeniority(float sen){
        if(sen<=lowSeniorityUpperBound){
            return "low";
        }
        else if(sen>lowSeniorityUpperBound && sen<=medSeniorityUpperBound){
            return "med";
        }
        else if(sen>medSeniorityUpperBound){
            return "high";
        }
        else{
            return "na";
        }
    }

    void writeConfigToNewDir(){
        PrintWriter newConfig = null;
        try{
            newConfig = new PrintWriter(getPath()+"/config.txt","UTF-8");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        newConfig.print(allContents);
        newConfig.close();
    }

    void writeConfFileToNewDir(){
        PrintWriter newFile = null;
        try{
            newFile = new PrintWriter(getPath()+"/conferencesList.txt","UTF-8");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        newFile.print(confFileContents);
        newFile.close();
    }

    public ConfigHandler(String pathToConfigFile){
        allContents = "";
        try{

            FileReader configFile = new FileReader(pathToConfigFile);
            BufferedReader bufRead = new BufferedReader(configFile);
            String myLine = null;

            while ( (myLine = bufRead.readLine()) != null)
            {    
                allContents += myLine + "\n";
                String[] array1 = myLine.split(":");
                // check to make sure you have valid data
                switch(array1[0]){
                    case "from_date":
                        fromDate = Integer.parseInt(array1[1]);
                        break;
                    case "to_date":
                        toDate = Integer.parseInt(array1[1]);
                        break;
                    case "path_to_dblp_xml":
                        datasetPath = array1[1];
                        break;
                    case "number_of_sklls_per_worker":
                        numberOfSkillsPerWorker = Integer.parseInt(array1[1]);
                        break;
                    case "path_to_conferences_list":
                        pathToConferencesList = array1[1];
                        break;
                    case "seniority_low_max":
                        lowSeniorityUpperBound = Integer.parseInt(array1[1]);
                        break;
                    case "seniority_med_max":
                        medSeniorityUpperBound = Integer.parseInt(array1[1]);
                        break;
                }
            }
            
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd-HH_mm_ss");
            currentDateTime = now.format(format);

        } catch(Exception e){
            e.printStackTrace();
        }

    }
}

class UserHandler extends DefaultHandler {
    Integer fromDate, toDate;
    boolean insideConf = false;
    int inproceedingsCount = 0;
    int edgesPrinted = 0;
    String content;
    int year;
    boolean insidePerson;
    int maxID = 0;
    ArrayList<Integer> persons;
    String elementName;
 
    PersonCollection personCollection = new PersonCollection();
    String confName;
    HashSet<String> conferences = new HashSet<String>(); // a set containing all the required conferences
    HashMap<String,String> confToField = new HashMap<String,String>(); // a mapping of conferences to their respected fields

    PrintWriter edgeListWriter;

    public UserHandler(ConfigHandler configObject){

        fromDate = configObject.getFromDate();
        toDate = configObject.getToDate();

        try{
            BufferedReader br = new BufferedReader(new FileReader(configObject.getConferencesListPath()));

            String line;
            while((line = br.readLine()) != null){
                configObject.confFileContents += line + "\n";
                String[] parts = line.split(" ");
                
                conferences.add(parts[0]);
                confToField.put(parts[0],parts[1]);
            }
            File d = new File(configObject.getDirName()+"/"+configObject.getCurrentDateTime());
            d.mkdirs();
            File f = new File(d,"/edgeList.txt");
            f.createNewFile();
            edgeListWriter = new PrintWriter(f ,"UTF-8");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public PersonCollection getPersonCollection(){
        return personCollection;
    }

    @Override
    public void startElement(String uri, String localName, String eName, Attributes attributes) throws SAXException {
        elementName = eName; // looking for inproceedings
        String k = attributes.getValue("key");	
        if (insidePerson = (elementName.equals("author"))) {
            content = "";
            return;
        }

        // START OF INPROCEEDINGS ELEMENT
        if((attributes.getLength()>0) && k != null){
            year = Integer.parseInt(attributes.getValue("mdate").split("-")[0]);
            Integer dat = Integer.parseInt(attributes.getValue("mdate").replace("-", ""));

            String [] key_tokens = k.split("/");	
            String pubType = key_tokens[0]; // conf
            confName = key_tokens[1]; // examples: kdd, www etc
            

            // if older than MAX_DATE and conference not included in the specified list, continue
            if( dat >= fromDate && dat < toDate && conferences.contains(confName) && pubType.equals("conf") && elementName.equals("inproceedings")) {
                
                if(inproceedingsCount%1000==0){
                    System.out.println(dat);
                }
                insideConf = true;
                
                // create new persons array to put the authors of the new paper
                persons = new ArrayList<Integer>(); 
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String eName) throws SAXException {

        // If the closing element is an author and insideConf is ON process the data
        if (eName.equals("author") && insideConf){

            // Find person from collection (content is the authors name in this case)
            Person p = PersonCollection.getPersonIfExists(content);

            // If the person doesnt exist in the Collection put him in
            if (p == null){
                p = PersonCollection.putPersonInCollection(content);
            }
            
            // Add conference to his conferences list, add Field and year
            p.addPublication(confName, confToField.get(confName), year);

            // Add person to persons i.e. the array of people in this specific paper
            persons.add(p.getID());

            // Toggle insidePerson to stop recording the content
            insidePerson=false;
        }
        // Otherwise if closing element is 
        else if (eName.equals(elementName) && insideConf){		// closing element is inproceedings
            inproceedingsCount = inproceedingsCount + 1;
            // System.out.print(inproceedingsCount + "\r");
            
            
            // persons is an arraylist of all ids of persons in the currently closing inproceedings record.

            if (persons.isEmpty()){
                return;
            }
            
            int newEdges = printEdges(persons);
            edgesPrinted = edgesPrinted+newEdges;

            insideConf = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (insidePerson)
            content += new String(ch, start, length);
    }

    int printEdges(ArrayList<Integer> list){
        int count = 0;
        for(int i=0;i<list.size();i++){
            for(int j=i+1;j<list.size();j++){
                int one = list.get(i);
                int two = list.get(j);
                if (one <= two){
                    edgeListWriter.println(one + "\t" + two);
                    count = count + 1;
                }
                else{
                    edgeListWriter.println(two + "\t" + one);
                    count = count + 1;
                }
            }
        }
        return count;
    }
}
