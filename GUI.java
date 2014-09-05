import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.ParseFiles;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.classify.ColumnDataClassifier;

import org.dom4j.DocumentException;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class GUI extends JFrame implements ActionListener, MouseListener {
    public Hashtable<String,ClassifierResult> hashclassifier;
    public Map<String,MappingResult> mapNP;
    public Map<String,MappingResult> mapS;
    public Map<String,Map<String,LinkedList<String>>> mapsentencesNP;
    public Map<String,Map<String,LinkedList<String>>> mapsentencesS;
    public Hashtable<String,LinkedList<String>> mapNPnotappear;
    public Hashtable<String,String> rolerelation = new Hashtable<String,String>(){{
                                                    put("0", "AM");
                                                    put("1", "CA");
                                                    put("2", "FS");
                                                    put("3", "AM^-1");
                                                    put("4", "CA^-1");
                                                    put("5", "FS^-1");
                                                    put("6", "IS");
                                                    put("7", "IS^-1");
                                                    }};
                                                    
    public LinkedList<Boolean> reverselist;                                             

	public JLabel label;
	// public JLabel labelfile;
	// public JTextField textfield;
	public JButton button;
	public JButton buttonfile;
	public JButton trainbutton;
	public JFileChooser filechooser;
	public JTextArea textarea;
	public JScrollPane scrollpane;
	public JScrollPane scrollpane2;
	public JScrollPane scrollpane3;
	public JTextPane textpane;
	public JTextPane textpane2;
	public JTextPane textpane3;
	public StyledDocument doc;
	public StyledDocument doc2;
	public StyledDocument doc3;
	public Style regularBlue;
	public Style regularGreen;
	public Style regularBlack;
	public Style regularRed;
	public Style regularOrange;
	public Style regularBrown;
	
	public ProcessData processdata = new ProcessData();;
	
	public GUI() {
		//FRAME
		super("Learning DL Based Concept Definitions");
		this.setSize(700,800);
		this.setVisible(true);
		this.getContentPane().setLayout(null);
		
		//LABEL
		// label = new JLabel();
		// label.setText("Input your text or open a file");
		// label.setBounds(50,5,200,50);
		// this.getContentPane().add(label);
		
		//FILE CHOOSER
        filechooser = new JFileChooser();
		
		//BUTTON FILE
		buttonfile = new JButton("Open a File");
		buttonfile.setBounds(50,10,100,20);
		this.getContentPane().add(buttonfile);
        buttonfile.addActionListener(this);
		
		//TEXTAREA
		textarea = new JTextArea();
		textarea.setBounds(170,10,480,20);
		this.getContentPane().add(textarea);
		
		
		
		//LABEL FILE
		// labelfile = new JLabel();
		// labelfile.setBounds(170,120,100,20);
		// this.getContentPane().add(labelfile);
		
		
		//BUTTON PROCESS
		button = new JButton("Process");
		button.setBounds(50,45,100,20);
		this.getContentPane().add(button);
		button.addActionListener(this);
		
		//TEXTPANE
		textpane = new JTextPane();
        doc = textpane.getStyledDocument();
        textpane.setContentType("text/plain");
        
		regularBlue = doc.addStyle("regularBlue", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(regularBlue, Color.BLUE);        
		//StyleConstants.setUnderline(regularBlue, true);
				
		regularBlack = doc.addStyle("regularBlack", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(regularBlack, Color.BLACK);
        
        regularOrange = doc.addStyle("regularOrange", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(regularOrange, Color.ORANGE);
        
        regularBrown = doc.addStyle("regularBrown", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(regularBrown, Color.MAGENTA);
        
		textpane.setEditable(false);
		textpane.addMouseListener(this);
		
		//SCROLLPANE
		scrollpane = new JScrollPane(textpane);
		scrollpane.setBounds(50,90,600,250);
		this.getContentPane().add(scrollpane);
		
		//TEXTPANE 2
		textpane2 = new JTextPane();
        doc2 = textpane2.getStyledDocument();
		regularRed = doc2.addStyle("regularRed", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(regularRed, Color.RED);
		regularGreen = doc2.addStyle("regularGreen", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(regularGreen, Color.GREEN);
        textpane2.setEditable(false);		
		
		//SCROLLPANE 2
		scrollpane2 = new JScrollPane(textpane2);
		scrollpane2.setBounds(50,350,600,150);
		this.getContentPane().add(scrollpane2);
        
        //TEXTPANE 3
		textpane3 = new JTextPane();
        doc3 = textpane3.getStyledDocument();		
		
		//SCROLLPANE 3
		scrollpane3 = new JScrollPane(textpane3);
		scrollpane3.setBounds(50,515,600,150);
		this.getContentPane().add(scrollpane3);
        
        //BUTTON TRAIN
		trainbutton = new JButton("Train");
		trainbutton.setBounds(50,670,100,20);
		this.getContentPane().add(trainbutton);
		trainbutton.addActionListener(this);
		
		repaint();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		GUI gui = new GUI();
        
	}
	
	public void actionPerformed(ActionEvent e) {
		String inputfile = "";
		try {
			if (e.getSource() == buttonfile) { // action for "Open File" button
                
				int returnVal = filechooser.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = filechooser.getSelectedFile();
					textarea.setText(file.getPath());
                    
				}
			} else if (e.getSource() == button) { // action for "Process" button
                doc.remove(0, doc.getLength());
                doc2.remove(0, doc2.getLength());
                doc3.remove(0, doc3.getLength());
                
				inputfile = textarea.getText();

				inputfile = inputfile.replace(" ", "\\ ");
				//inputfile = "\"" + inputfile + "\"";
			
                /** get the final result (hash classifier) from mainprocess in ProcessData.java **/
				hashclassifier = processdata.mainprocess(inputfile);
                
                mapNP = new HashMap<String,MappingResult>();
                mapS = new HashMap<String,MappingResult>();
                mapsentencesNP = new HashMap<String,Map<String,LinkedList<String>>>();
                mapsentencesS = new HashMap<String,Map<String,LinkedList<String>>>();
                mapNPnotappear = new Hashtable<String,LinkedList<String>>();
                reverselist = new LinkedList<Boolean>();
                
                /** call the Parser **/
                String[] param = {"-retainTMPSubcategories", "-outputFormat", "penn", "D:\\EMCL\\Semester1\\Project\\stanford-parser-full-2013-11-12\\edu\\stanford\\nlp\\models\\lexparser\\englishPCFG.ser.gz", inputfile}; 
                LexicalizedParser.main(param);
                ArrayList<Tree> listAnsTree = LexicalizedParser.listAnsTree;
                
                /** sort hashclassifier **/
                Map<String, Float> unsortMap = new HashMap<String, Float>();
                Enumeration hashenum = hashclassifier.keys();
                while(hashenum.hasMoreElements()) {
                    String key = hashenum.nextElement().toString();
					ClassifierResult cr = hashclassifier.get(key);
                    Float average = cr.getAverage() / cr.getCountElement();
                    unsortMap.put(key, average); // put key and classifier score in a map and then sort it
                    cr.setAverage(average); // average in cr already updated as "real" average      
                }
                
                Map<String, Float> sortedMapDesc = sortByComparator(unsortMap); // sorted hashclassifier 
				
				/** combine all NP in mapNP and all S in mapS **/
                
                for (Entry<String, Float> entry : sortedMapDesc.entrySet()) {
                    
                    String key = entry.getKey();              
                    
                    ClassifierResult cr = hashclassifier.get(key);                    
                              
                    String type1 = cr.getType1();
                    String type2 = cr.getType2();
                    String role = cr.getRole();
                    Float average = cr.getAverage();
                    LinkedList<String> ll = cr.getSentenceLocation();              
					int size = ll.size();                

                    System.out.println("before reversed:" + type1 + "," + role + "," + type2 + "," + average); // print single relation                            
                    
                    // turn around inverse role
                    Boolean reverse = false;
                    if(role.equals("3")) {
                        String temp = type1;
                        type1 = type2;
                        type2 = temp;
                        role = "0";
                        reverse = true;
                    } else if(role.equals("4")) {
                        String temp = type1;
                        type1 = type2;
                        type2 = temp;
                        role = "1";
                        reverse = true;
                    } else if(role.equals("5")) {
                        String temp = type1;
                        type1 = type2;
                        type2 = temp;
                        role = "2";
                        reverse = true;
                    } else if(role.equals("7")) {
                        String temp = type1;
                        type1 = type2;
                        type2 = temp;
                        role = "6";
                        reverse = true;
                    }
                    
                    System.out.print("after reversed:" + type1 + "," + role + "," + type2 + "," + average); // print single relation        
                    
					String loc, lw, bw, rw = "";
					for(int i = 0; i < size; i++) {
						loc = ll.get(i);
                        System.out.print("," + loc);				
					}
                    
					System.out.print("\n"); 

                    // threshold
                    //if(average >= 0.7f) {
                    
                    String newkey = type1 + "|" + type2; // new key because we turn around the type
                    
                    
                        // for loop sentence location and detect whether is NP or S
                        for(int i = 0; i < size; i++) {
                            loc = ll.get(i); // sentence location
                            String[] arr = loc.split("--"); //arr[1] = phrase1, arr[2] = phrase2, arr[3] = no
                            String numsentence = arr[3]; // number of sentence
                                
                            arr[1] = arr[1].replace('(',' ');
                            arr[2] = arr[2].replace('(',' ');
                            arr[1] = arr[1].replace(')',' ');
                            arr[2] = arr[2].replace(')',' ');
                            arr[1] = arr[1].replace('.',' ');
                            arr[2] = arr[2].replace('.',' ');
                            arr[1] = arr[1].replace(',',' ');
                            arr[2] = arr[2].replace(',',' ');
                            
                            Integer index = Integer.parseInt(arr[3]);
                            index = index-1;
                            String[] arrphrase1 = arr[1].split("\\s+");
                            String[] arrphrase2 = arr[2].split("\\s+");
                            Tree ansTree = listAnsTree.get(index);
                            
                            String relation = ParseFiles.processRelation(arrphrase1, arrphrase2, ansTree);
                            
                            // if it is already in role 6, it is must be in S relation
                            Boolean process = true;
                            if(role.equals("6") && !(relation.equals("S"))) {
                                relation = "S";
                                process = false;
                                //mapNPnotappear
                                    if(!(mapNPnotappear.containsKey(numsentence))) {
                                        LinkedList<String> linklist2 = new LinkedList<String>();
                                        linklist2.add(type2);
                                        mapNPnotappear.put(numsentence,linklist2);
                                    } else {
                                        LinkedList<String> linklist2 = mapNPnotappear.get(numsentence);
                                        linklist2.add(type2);
                                    }
                            } 
                             
                             /** Build mapNP and mapS, mapsentencesNP and mapsentencesS. Split the sentence according to relation NP or S **/
                            if(process) {
                                if(relation.equals("NP")) {
                                    System.out.print(type1 + " (NP) " + role + " " + type2 + "\n");
                                   
                                    // mapNP
                                    if(!(mapNP.containsKey(newkey))) {
                                        LinkedList<String> llsln = new LinkedList<String>();
                                        llsln.add(loc);
                                        MappingResult crNP = new MappingResult(type1, type2, role, average, llsln, reverse);
                                        mapNP.put(newkey, crNP);
                                    } else {
                                        MappingResult crNP = mapNP.get(newkey);
                                        LinkedList<String> llsln = crNP.getSentenceLocation();
                                        llsln.add(loc);
                                        crNP.setSentenceLocation(llsln);
                                    }
                                    
                                    // mapsentencesNP
                                    if(!(mapsentencesNP.containsKey(numsentence))) {
                                        Map<String,LinkedList<String>> map = new HashMap<String,LinkedList<String>>();
                                        LinkedList<String> linklist = new LinkedList<String>();
                                        linklist.add(type2);
                                        map.put(type1, linklist);                            
                                        mapsentencesNP.put(numsentence, map);         

                                        //mapNPnotappear
                                        if(!(mapNPnotappear.containsKey(numsentence))) {
                                            LinkedList<String> linklist2 = new LinkedList<String>();
                                            linklist2.add(type2);
                                            mapNPnotappear.put(numsentence,linklist2);
                                        } else {
                                            LinkedList<String> linklist2 = mapNPnotappear.get(numsentence);
                                            linklist2.add(type2);
                                        }
                                        
                                    } else {
                                        Map<String,LinkedList<String>> map = mapsentencesNP.get(numsentence);
                                        
                                        if(!(map.containsKey(type1))) {
                                            LinkedList<String> linklist = new LinkedList<String>();
                                            linklist.add(type2);
                                            map.put(type1, linklist);
                                        } else {
                                            LinkedList<String> linklist = map.get(type1);
                                            linklist.add(type2);
                                        }   
                                            
                                        //mapNPnotappear
                                        if(!(mapNPnotappear.containsKey(numsentence))) {
                                            LinkedList<String> linklist2 = new LinkedList<String>();
                                            linklist2.add(type2);
                                            mapNPnotappear.put(numsentence,linklist2);
                                        } else {
                                            LinkedList<String> linklist2 = mapNPnotappear.get(numsentence);
                                            linklist2.add(type2);
                                        }
                                        
                                    }
                                    
                                } else {
                                     System.out.print(type1 + " (S) " + role + " " + type2 + "\n");
                                
                                    // mapS
                                    if(!(mapS.containsKey(newkey))) {
                                        LinkedList<String> llsln = new LinkedList<String>();
                                        llsln.add(loc);
                                        MappingResult crS = new MappingResult(type1, type2, role, average, llsln, reverse);
                                        mapS.put(newkey, crS);
                                    } else {
                                        MappingResult crS = mapS.get(newkey);
                                        LinkedList<String> llsln = crS.getSentenceLocation();
                                        llsln.add(loc);
                                        crS.setSentenceLocation(llsln);
                                    }
                                    
                                    // mapsentencesS
                                    if(!(mapsentencesS.containsKey(numsentence))) {
                                        Map<String,LinkedList<String>> map = new HashMap<String,LinkedList<String>>();
                                        LinkedList<String> linklist = new LinkedList<String>();
                                        linklist.add(type2);
                                        map.put(type1, linklist);                            
                                        mapsentencesS.put(numsentence, map);                                
                                    } else {
                                        Map<String,LinkedList<String>> map = mapsentencesS.get(numsentence);
                                        
                                        if(!(map.containsKey(type1))) {
                                            LinkedList<String> linklist = new LinkedList<String>();
                                            linklist.add(type2);
                                            map.put(type1, linklist);
                                        } else {
                                            LinkedList<String> linklist = map.get(type1);
                                            linklist.add(type2);
                                        }                 
                                    }
                                    
                                }
                            } // end if process 
                            
                        } // end for loop sentence location
                    
                    System.out.print("\n");                    
                } // end for combining NP and S
                
                /** print mapsentencesS **/
                for (Entry<String, Map<String,LinkedList<String>>> entry : mapsentencesS.entrySet()) {
                    String numsentence = entry.getKey();
                    Map<String,LinkedList<String>> map = entry.getValue();
                    
                    for (Entry<String, LinkedList<String>> entries : map.entrySet()) {
                        String type1 = entries.getKey();
                        LinkedList<String> linklist = entries.getValue();
                            
                        // for loop every new definition                        
                        for(int i = 0; i < linklist.size(); i++) {
                            String type2 = linklist.get(i);    

                            // check to be print or not
                            Boolean print = true;
                            
                            if(mapNPnotappear.containsKey(numsentence)) {
                                LinkedList<String> notprint = mapNPnotappear.get(numsentence);                        
                                
                                for(int n = 0; n < notprint.size(); n++) {
                                    if(type1.equals(notprint.get(n)) || type2.equals(notprint.get(n))) print = false;
                                }
                            }

                            if(print) {
                                String keyS = type1 + "|" + type2;                            
                                String keylinklistener = numsentence+type1;
                                LinkListener linklistener = new LinkListener(keylinklistener);	
                                LinkedList<String> keytrain = new LinkedList<String>();
                                LinkedList<Boolean> reversetrain = new LinkedList<Boolean>();
                                Float avg = 1.0f;
                                LinkedHashMap<String,String> allsentenceloc = new LinkedHashMap<String,String>();
                                
                                // checking whether type 1 or type 2 is complex
                                if(mapsentencesNP.containsKey(numsentence)) {
                                    Map<String, LinkedList<String>> checkmapNP = mapsentencesNP.get(numsentence);
                                    if(checkmapNP.containsKey(type1)) {
                                        //collect all of complex type1
                                        System.out.print(type1 + " ");
                                        doc.insertString(doc.getLength(),type1 + " ", regularBlack);
                                        
                                        LinkedList<String> linklistNP = checkmapNP.get(type1);
                                        for(int k = 0; k < linklistNP.size(); k++) {
                                            String type2np = linklistNP.get(k);                            
                                            String keyNP = type1 + "|" + type2np;
                                            
                                            MappingResult crNP = mapNP.get(keyNP);
                                            System.out.print("(NP) " + rolerelation.get(crNP.getRole()) + " " + type2np + " ");
                                            if((rolerelation.get(crNP.getRole())).equals("IS"))
                                                doc.insertString(doc.getLength(), "\u2293 " + type2np + " ", regularBlack);
                                            else doc.insertString(doc.getLength(), "\u2293 \u2203 " + rolerelation.get(crNP.getRole()) + ". " + type2np + " ", regularBlack);
                                            keytrain.add(type1 + " | " + rolerelation.get(crNP.getRole()) + " | " + type2np);
                                            reversetrain.add(crNP.getReverseFlag());
                                            if(crNP.getAverage() < avg)
                                                avg = crNP.getAverage();
                                            LinkedList<String> llsl = crNP.getSentenceLocation();
                                            for(int j = 0; j < llsl.size(); j++) {
                                                //System.out.print(llsl.get(j) + " ");
                                                String stcloc = llsl.get(j);
                                                if(!(allsentenceloc.containsKey(stcloc))) {
                                                    linklistener.addLocation(stcloc);
                                                    allsentenceloc.put(stcloc,"");
                                                }
                                            }
                                        }         
                                        
                                    } else {
                                        //just print single type1
                                        System.out.print(type1);
                                        doc.insertString(doc.getLength(), type1, regularBlack);
                                    }
                                    
                                    // print subsumption, role, and single type2
                                    MappingResult cr = mapS.get(keyS);
                                    System.out.print(" (S) " + rolerelation.get(cr.getRole()) + " " + type2 + " ");
                                    
                                    if((rolerelation.get(cr.getRole())).equals("IS")) 
                                        doc.insertString(doc.getLength(), " \u2291 " + type2 + " ", regularBlack);
                                    else doc.insertString(doc.getLength(), " \u2291 \u2203 " + rolerelation.get(cr.getRole()) + ". " + type2 + " ", regularBlack);
                                    keytrain.add(type1 + " | " + rolerelation.get(cr.getRole()) + " | " + type2);
                                    reversetrain.add(cr.getReverseFlag());
                                    if(cr.getAverage() < avg)
                                        avg = cr.getAverage();
                                    LinkedList<String> llsl = cr.getSentenceLocation();
                                    for(int j = 0; j < llsl.size(); j++) {
                                        String stcloc = llsl.get(j);
                                        if(!(allsentenceloc.containsKey(stcloc))) {
                                            linklistener.addLocation(stcloc);
                                            allsentenceloc.put(stcloc,"");
                                        }
                                    } 
                                    
                                    // if type2 in complex form, collect all of them
                                    if(checkmapNP.containsKey(type2)) {
                                        //collect all of complex type2
                                        LinkedList<String> linklistNP = checkmapNP.get(type2);
                                        for(int k = 0; k < linklistNP.size(); k++) {
                                            String type2np = linklistNP.get(k);                            
                                            String keyNP = type2 + "|" + type2np;
                                            
                                            MappingResult crNP = mapNP.get(keyNP);
                                            System.out.print("(NP) " + rolerelation.get(crNP.getRole()) + " " + type2np + " ");
                                            if((rolerelation.get(crNP.getRole())).equals("IS"))
                                                doc.insertString(doc.getLength(), "\u2293 " + type2np + " ", regularBlack);
                                            else doc.insertString(doc.getLength(), "\u2293 \u2203 " + rolerelation.get(crNP.getRole()) + ". " + type2np + " ", regularBlack);
                                            keytrain.add(type2 + " | " + rolerelation.get(crNP.getRole()) + " | " + type2np);
                                            reversetrain.add(crNP.getReverseFlag());
                                            if(crNP.getAverage() < avg)
                                                avg = crNP.getAverage();
                                            LinkedList<String> llslNP = crNP.getSentenceLocation();
                                            for(int j = 0; j < llslNP.size(); j++) {
                                                //System.out.print(llslNP.get(j) + " ");
                                                String stcloc = llslNP.get(j);
                                                if(!(allsentenceloc.containsKey(stcloc))) {
                                                    linklistener.addLocation(stcloc);
                                                    allsentenceloc.put(stcloc,"");
                                                }
                                            }
                                        }     
                                    } 
                                    
                                } else { // A and B occur in sentence x as S relation and sentence x does not have any NP relation
                                    System.out.print(type1);
                                    doc.insertString(doc.getLength(), type1, regularBlack);
                                    MappingResult cr = mapS.get(keyS);
                                    System.out.print(" (S) " + rolerelation.get(cr.getRole()) + " " + type2 + " ");
                                    if((rolerelation.get(cr.getRole())).equals("IS"))
                                        doc.insertString(doc.getLength(), " \u2291 " + type2 + " ", regularBlack);
                                    else doc.insertString(doc.getLength(), " \u2291 \u2203 " + rolerelation.get(cr.getRole()) + ". " + type2 + " ", regularBlack);
                                    keytrain.add(type1 + " | " + rolerelation.get(cr.getRole()) + " | " + type2);
                                    reversetrain.add(cr.getReverseFlag());
                                    if(cr.getAverage() < avg)
                                        avg = cr.getAverage();
                                    LinkedList<String> llsl = cr.getSentenceLocation();
                                    for(int j = 0; j < llsl.size(); j++) {
                                        String stcloc = llsl.get(j);
                                        if(!(allsentenceloc.containsKey(stcloc))) {
                                            linklistener.addLocation(stcloc);
                                            allsentenceloc.put(stcloc,"");
                                        }
                                    } 
                                }
                                
                                System.out.print(avg + " ");
                                doc.insertString(doc.getLength(), avg + " ", regularOrange);
                                
                                for(Entry<String,String> entrystc : allsentenceloc.entrySet()) {
                                    System.out.print(entrystc.getKey() + " ");
                                    //doc.insertString(doc.getLength(), entrystc.getKey() + " ", regularOrange);
                                }                                              

                                regularBlue.addAttribute("linktext", linklistener);
                                doc.insertString(doc.getLength(), "  ?", regularBlue);	  
                                regularBrown.addAttribute("truetrain", new TrainListener(keytrain, reversetrain));
                                doc.insertString(doc.getLength(), "   V", regularBrown);                    
                                doc.insertString(doc.getLength(), "\n", regularBlack);      
                                System.out.println("\n");
                                doc.insertString(doc.getLength(), "\n", regularBlack);
                            } // end if print
                        } // end for loop every new definition   
                                                
                        //System.out.println("\n");
                    }
                } // end for print mapsentencesS
                
                
                
			} else if (e.getSource() == trainbutton) { // action for "Train" button
                String newtrain = textpane3.getText();
                
                String[] arr = newtrain.split("[\\r\\n]+");
            
                BufferedWriter writer = new BufferedWriter(new FileWriter("positive.types.explicit.train", true));	
                for(int i = 0; i < arr.length; i++) {
                    String[] arr2 = arr[i].split(" \\| "); //arr2[0] = type1, arr2[1] = role, arr2[2] = type2
                    String type1 = arr2[0];
                    String type2 = arr2[2];
                    
                    String newrole = "";
                    if(arr2[1].equals("AM")) newrole = "0";
                    else if(arr2[1].equals("CA")) newrole = "1";
                    else if(arr2[1].equals("FS")) newrole = "2";    
                    else if(arr2[1].equals("AM-1")) newrole = "3";    
                    else if(arr2[1].equals("CA-1")) newrole = "4";    
                    else if(arr2[1].equals("FS-1")) newrole = "5";    
                    else if(arr2[1].equals("IS")) newrole = "6";    
                    else if(arr2[1].equals("IS-1")) newrole = "7"; 
                    
                    String key = type1 + "|" + type2; 
                    ClassifierResult cr = hashclassifier.get(key);                    
                    
                    LinkedList<String> ll = cr.getSentenceLocation();
                    for(int j = 0; j < ll.size(); j++) {
                                                 
                        String t1 = cr.getSemanticType1();
                        String t2 = cr.getSemanticType2();
                        String lw = cr.getLeftWords().get(j);
                        String bw = cr.getBetweenWords().get(j);
                        String rw = cr.getRightWords().get(j);       
                        
                        writer.append(newrole + "\t" + lw + "\t" + bw + "\t" + rw + "\t" + t1 + "\t" + t2 + "\t" + type1 + "|" + type2 + "|" + cr.getSentenceLocation().get(j) + "|" + "\n"); // between words and type are not written
                    }
                     
                }
                writer.close();
                                
                System.out.println("Classifier Train Process");
                
                String[] param = {"-prop", "D:\\EMCL\\Semester1\\Project\\CODEFINAL\\ontotrain.prop"};		
                ColumnDataClassifier.main(param);
                
                JOptionPane.showMessageDialog(null,"Training done successfully","Info",JOptionPane.INFORMATION_MESSAGE);    
                textpane3.setText("");                   
                reverselist =  new LinkedList<Boolean>(); // make reverselist empty again                 
             }
            
		} catch (NumberFormatException | IOException | InterruptedException | BadLocationException | DocumentException e1) {
			e1.printStackTrace();
		}
		
	}
    
    /* source: http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values (with adaptation) */
    private static Map<String, Float> sortByComparator(Map<String, Float> unsortMap)
    {

        List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Float>>()
        {
            public int compare(Entry<String, Float> o1,
                    Entry<String, Float> o2)
            {
                    return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        for (Entry<String, Float> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    // to handle action when user click "?" or "V"
    public void mouseClicked(MouseEvent e) {	      
		Element ele = doc.getCharacterElement(textpane.viewToModel(e.getPoint()));
		AttributeSet as = ele.getAttributes();
		LinkListener lls = (LinkListener)as.getAttribute("linktext");
		TrainListener tls = (TrainListener)as.getAttribute("truetrain");
		if(lls != null) { // if user click "?"
            textpane2.setText("");
			ArrayList<String> location = lls.getLocation();
		
			for(int i = 0; i < location.size(); i++) {
				String[] arr = lls.execute(location.get(i));
				Hashtable<String,String> hashstc = processdata.hashsentence;
				String key = "WikipediaAnnotated\\" + arr[0];  
				String phrase1 = arr[1];
				String phrase2 = arr[2];
				String sentence = hashstc.get(key);
                int index1 = sentence.indexOf(phrase1);
                int index2 = sentence.indexOf(phrase2);
                String front = sentence.substring(0,index1);
                String middle = sentence.substring(index1+phrase1.length(), index2);
                String last = sentence.substring(index2+phrase2.length(), sentence.length());
				try {
					doc2.insertString(doc2.getLength(), front, regularBlack);
					doc2.insertString(doc2.getLength(), phrase1, regularRed);
					doc2.insertString(doc2.getLength(), middle, regularBlack);
					doc2.insertString(doc2.getLength(), phrase2, regularBlue);
					doc2.insertString(doc2.getLength(), last + "\n\n", regularBlack);
                    
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		} else if(tls != null) { // if user click "V"
            LinkedList<String> key = tls.getKey();
            LinkedList<Boolean> rvslist = tls.getReverse();
            
            try {
                for(int i = 0; i < key.size(); i++) {
                    if(rvslist.get(i) == true) {
                        reverselist.add(rvslist.get(i));
                        //System.out.println(rvslist.get(i));
                        String[] linearr = (key.get(i)).split(" \\| ");
                        String type1 = linearr[2];
                        String type2 = linearr[0];
                        String role = "";
                        if(linearr[1].equals("AM")) role = "AM-1";
                        else if(linearr[1].equals("CA")) role = "CA-1";
                        else if(linearr[1].equals("FS")) role = "FS-1";
                        doc3.insertString(doc3.getLength(), type1 + " | " + role + " | " + type2 + "\n", regularBlack);
                    } else {                         
                        reverselist.add(rvslist.get(i));
                        //System.out.println(rvslist.get(i));
                        doc3.insertString(doc3.getLength(), key.get(i) + "\n", regularBlack);
                    }
                }
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
        }
     }
			
	
}

class LinkListener extends AbstractAction {
	public ArrayList<String> location;
	String key;

	LinkListener(String key) {
		this.key = key;
		this.location = new ArrayList<String>();
	}

	public String[] execute(String loc) {
		String[] arr = loc.split("--");
		return arr;
	}
	
	public void addLocation(String loc) {
		this.location.add(loc);
	}
	
	public ArrayList<String> getLocation() {
		return this.location;
	}

	public void actionPerformed(ActionEvent e) {
	}
}

class TrainListener extends AbstractAction {
	LinkedList<String> key;
    LinkedList<Boolean> reverse;

	TrainListener(LinkedList<String> key, LinkedList<Boolean> reverse) {
		this.key = key;
        this.reverse = reverse;
	}

	// public String[] execute(String loc) {
		// String[] arr = loc.split("--");
		// return arr;
	// }
    
    public LinkedList<String> getKey() {
        return this.key;
    }
    
    public LinkedList<Boolean> getReverse() {
        return this.reverse;
    }
	
	public void actionPerformed(ActionEvent e) {
	}
}

class MappingResult {
	public String type1;	
	public String type2;	
	public String role;
	public Float average;
	public LinkedList<String> sentencelocation;
    public Boolean reverseFlag;
	
	public MappingResult(String type1, String type2, String role, Float average, LinkedList<String> sentencelocation, Boolean reverseFlag) {
		this.type1 = type1;
		this.type2 = type2;
		this.role = role;
		this.average = average;
		this.sentencelocation = sentencelocation;
        this.reverseFlag = reverseFlag;
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
    public void setReverseFlag(Boolean reverseFlag) {
		this.reverseFlag = reverseFlag;
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
    
    public Boolean getReverseFlag() {
		return reverseFlag;
	}
	
	
}