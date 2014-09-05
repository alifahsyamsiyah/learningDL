
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

/**
Class: Role
0: AM
1: CA
2: FS
3: AM^-1
4: CA^-1
5: FS^-1
*/



public class ProcessData {
	public static Hashtable<String,String> hashsentence;
    public static Hashtable<String,String> hashrole;

	public Hashtable<String,ClassifierResult> mainprocess(String inputfile)	throws IOException, NumberFormatException, InterruptedException, DocumentException {
		String[] arr = new String[]{};
		String line = null;
	
		URL location = ProcessData.class.getProtectionDomain().getCodeSource().getLocation();
		String currentpath = location.getPath();
		currentpath = currentpath.substring(1);
		System.out.println("current path:" + currentpath);
		System.out.println("input file:" + inputfile);
	
		BufferedReader reader;
		BufferedWriter writer;	
		
		
		
		// /********************************************** Read file input from user ***********************************************/
		
		
		/*********************************************** Metamap ***********************************************/
		
		System.out.println("Metamap Processing");
		
		String[] array = {"cmd.exe", "/C", currentpath + "public_mm/bin/skrmedpostctl_start.bat"};
		Process process = Runtime.getRuntime().exec(array);
		
		String[] array2 = {"cmd.exe", "/C", currentpath + "public_mm/bin/wsdserverctl_start.bat"};
		Process process2 = Runtime.getRuntime().exec(array2);
		
		String[] array3 = {"cmd.exe", "/C", currentpath + "public_mm/bin/metamap13", "--XMLf", inputfile, currentpath+"outputmetamap.xml"};
		Process process3 = Runtime.getRuntime().exec(array3);
		
		process3.waitFor();		
		process.destroy();
		process2.destroy();
		process3.destroy();
				
		/*********************************************** Convert c2s.csv to hash ***********************************************/

		reader = new BufferedReader(new FileReader("./Metathesaurus/c2s.csv"));
		
		Hashtable<String,LinkedList> hashdic = new Hashtable<String,LinkedList>();
		
		while((line = reader.readLine()) != null) {
			arr = line.split("\\|");
			
			if(!hashdic.containsKey(arr[0])) {
				LinkedList<String> linklistdic = new LinkedList<String>();
				linklistdic.add(arr[1]);
				hashdic.put(arr[0],linklistdic);
			} else {
				hashdic.get(arr[0]).add(arr[1]);
			}
		}	
		
		/*********************************************** Convert from .xml to .annotated ***********************************************/
		
		reader.close();
		writer = new BufferedWriter(new FileWriter("./WikipediaAnnotated/@New.annotated"));	
        
        SAXReader saxreader = new SAXReader(); 
		Document document = saxreader.read("outputmetamap.xml");         
        Element root = document.getRootElement(); 	
        
        hashsentence = new Hashtable<String,String>(); 
		int counter = 0;        
		
		List listphrases = document.selectNodes( "//Phrases" );
        for(Iterator iter0 = listphrases.iterator(); iter0.hasNext(); ) {
            DefaultElement phrases = (DefaultElement) iter0.next(); 
            List listphrase = phrases.selectNodes( ".//Phrase" );
            counter++;
            String sentence = "";
            
            for(Iterator iter1 = listphrase.iterator(); iter1.hasNext(); ) { 
                DefaultElement phrase = (DefaultElement) iter1.next(); 
                Node nodephrasetext = phrase.selectSingleNode(".//PhraseText");
                List listsyntaxunit = phrase.selectNodes(".//SyntaxUnits//SyntaxUnit");
                String phrasetext = nodephrasetext.getText();
                writer.write(phrasetext);
                sentence = sentence + phrasetext + " ";
                boolean processflag = true;
                
                // for loop syntax unit to get syntax type and input match
                for(Iterator iter3 = listsyntaxunit.iterator(); iter3.hasNext(); ) {
                    DefaultElement syntaxunit = (DefaultElement) iter3.next(); 
                    Node syntaxtype = syntaxunit.selectSingleNode(".//SyntaxType");
                    if(syntaxtype.getText().equals("verb")) {
                        processflag = false;
                    }              
                }
                
                if(processflag) {
                    List listmapping = phrase.selectNodes(".//Mappings//Mapping");
                    
                    // for loop candidate (1 phrase can have several candidates, select if matched word equal with one of input match, and also the isHead must equal to yes). if it is negated put -
                    for (Iterator iter2 = listmapping.iterator(); iter2.hasNext(); ) {
                        DefaultElement mapping = (DefaultElement) iter2.next(); 
                        List listmappingcandidates = mapping.selectNodes(".//MappingCandidates//Candidate");
                        
                        for(Iterator iter4 = listmappingcandidates.iterator(); iter4.hasNext(); ) {
                            DefaultElement candidate = (DefaultElement) iter4.next(); 
                            Node ishead = candidate.selectSingleNode(".//IsHead");
                            
                            if(ishead.getText().equals("yes")) {
                                Node nodecandidatecui = candidate.selectSingleNode(".//CandidateCUI");
                                String candidatecui = nodecandidatecui.getText();
                                LinkedList<String> listsnomed = hashdic.get(candidatecui); // linked list yg isinya SNOMED ID
                                Node negated = candidate.selectSingleNode(".//Negated");
                                if(negated.getText().equals("1")) {
                                    if(listsnomed != null) {									
                                        int s = listsnomed.size();
                                        for(int i = 0; i < s; i++) {
                                            writer.write("|" + "-" + listsnomed.get(i));
                                        }
                                    } 
                                } else {
                                    if(listsnomed != null) {									
                                        int s = listsnomed.size();
                                        for(int i = 0; i < s; i++) {
                                            writer.write("|" + listsnomed.get(i));
                                        }
                                    } 
                                }  
                            }                            
                        }
                    }
                }                
                writer.write("\n");                
            }
            writer.write("\n");
            String key = "WikipediaAnnotated\\@New" + Integer.toString(counter); 
            hashsentence.put(key,sentence); 
        }	
		
		reader.close();
		writer.close();
		
		
		/*********************************************** Python ***********************************************/
		
		System.out.println("Python Process");
		
		String[] array4 = {"python", "generatePositiveExamplesOriTest.py"};
		Process process4 = Runtime.getRuntime().exec(array4);
		
		process4.waitFor();
        process4.destroy();
		
		/*********************************************** Classifier ***********************************************/
		
		System.out.println("Classifier Process");		
		String[] array5 = {"java", "-jar", "stanford-new.jar", "-prop", "ontotest.prop"};
		Process process5 = Runtime.getRuntime().exec(array5);	
		reader = new BufferedReader(new InputStreamReader(process5.getInputStream()));  
		
		/*********************************************** Processing output of classifier ***********************************************/
        
		Hashtable<String,ClassifierResult> hashclassifier = new Hashtable<String,ClassifierResult>();
		
		while((line = reader.readLine()) != null) {
			arr = line.split("\t"); //arr[0] = type1|type2|sl--phrase1--phrase2--no|lw|bw|rw, arr[1] = goldanswer, arr[2] = role, arr[3] = average
			String[] temp = arr[0].split("\\|"); //temp[0] = type1, temp[1] = type2, temp[2] = sl--phrase1--phrase2--no, temp[3] = lw, temp[4] = bw, temp[5] = rw, temp[6] = semantictype1, temp[7] = semantictype2
			String key = temp[0] + "|" + temp[1];
			String sl = temp[2];
			float prob = Float.valueOf(arr[3]);
   
			
			if(!(hashclassifier.containsKey(key))) {
				LinkedList<String> ll = new LinkedList<String>();
				LinkedList<String> ll2 = new LinkedList<String>();
				LinkedList<String> ll3 = new LinkedList<String>();
				LinkedList<String> ll4 = new LinkedList<String>();
				ll.add(sl);
                ll2.add(temp[3]);
                ll3.add(temp[4]);
                ll4.add(temp[5]);
				ClassifierResult cr = new ClassifierResult(temp[0], temp[1], arr[2], prob, ll, ll2, ll3, ll4, 1, temp[6], temp[7]);	
				hashclassifier.put(key,cr);
			} else {
				ClassifierResult cr = hashclassifier.get(key);
				LinkedList<String> ll = cr.getSentenceLocation();
                LinkedList<String> ll2 = cr.getLeftWords();
                LinkedList<String> ll3 = cr.getBetweenWords();
                LinkedList<String> ll4 = cr.getRightWords();
				ll.add(sl); 
				ll2.add(temp[3]); 
				ll3.add(temp[4]); 
				ll4.add(temp[5]); 
				cr.setSentenceLocation(ll);
				cr.setLeftWords(ll2);
				cr.setBetweenWords(ll3);
				cr.setRightWords(ll4);
				Float av = cr.getAverage();
				av = prob + av; // counting probability, not calculating average yet
				cr.setAverage(av);
                Integer ce = cr.getCountElement();
                ce = ce + 1; // counting element
                cr.setCountElement(ce);
			}
		}               
        
        process5.destroy();		
		return(hashclassifier);
	}
}


class ClassifierResult {
	public String type1;	
	public String type2;	
	public String role;
	public Float average;
	public LinkedList<String> sentencelocation;
    public LinkedList<String> leftWords;
    public LinkedList<String> betweenWords;
    public LinkedList<String> rightWords;
    public Integer countElement;
    public String semantictype1;	
	public String semantictype2;
	
	public ClassifierResult(String type1, String type2, String role, Float average, LinkedList<String> sentencelocation, LinkedList<String> leftWords, LinkedList<String> betweenWords, LinkedList<String> rightWords, Integer countElement, String semantictype1, String semantictype2) {
		this.type1 = type1;
		this.type2 = type2;
		this.role = role;
		this.average = average;
		this.sentencelocation = sentencelocation;
		this.leftWords = leftWords;
		this.betweenWords = betweenWords;
		this.rightWords = rightWords;
        this.countElement = countElement;
        this.semantictype1 = semantictype1;
        this.semantictype2 = semantictype2;
	}
	
	// Mutator function
	
	public void setType1(String type1) {
		this.type1 = type1;
	}
	
	public void setType2(String type2) {
		this.type2 = type2;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public void setAverage(Float average) {
		this.average = average;
	}
	
	public void setSentenceLocation(LinkedList<String> sentencelocation) {
		this.sentencelocation = sentencelocation;
	}
    
    public void setLeftWords(LinkedList<String> leftWords) {
		this.leftWords = leftWords;
	}
    
    public void setBetweenWords(LinkedList<String> betweenWords) {
		this.betweenWords = betweenWords;
	}
    
    public void setRightWords(LinkedList<String> rightWords) {
		this.rightWords = rightWords;
	}
    
    public void setCountElement(Integer countElement) {
		this.countElement = countElement;
	}
    
    public void setSemanticType1(String semantictype1) {
		this.semantictype1 = semantictype1;
	}
    
    public void setSemanticType2(String semantictype2) {
		this.semantictype2 = semantictype2;
	}
	
	// Accessor function 
	
	public String getType1() {
		return type1;
	}
	public String getType2() {
		return type2;
	}
	
	public String getRole() {
		return role;
	}
	
	public Float getAverage() {
		return average;
	}
	
	public LinkedList<String> getSentenceLocation() {
		return sentencelocation;
	}
    
    public LinkedList<String> getLeftWords() {
		return leftWords;
	}
    
    public LinkedList<String> getBetweenWords() {
		return betweenWords;
	}
    
    public LinkedList<String> getRightWords() {
		return rightWords;
	}
    
    public Integer getCountElement() {
		return countElement;
	}
    
    public String getSemanticType1() {
		return semantictype1;
	}
    
    public String getSemanticType2() {
		return semantictype2;
	}
	
	
}
