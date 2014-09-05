import java.io.*;
import java.util.*;

public class TextProcessing {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("positive.types.explicit.train"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("positive.types.explicit.train2"));
        
        String line = "";
        while((line = reader.readLine()) != null) {
            String[] input = line.split("\t");
            
            if((input[0]).equals("6") || (input[0]).equals("7")) {
                if((input[2]).equalsIgnoreCase("is") || (input[2]).equalsIgnoreCase("are")) {
                    writer.write(line+"\n");
                }
            } else {
            
                writer.write(line+"\n");
            
                // for(int j = 0; j < input.length; j++) {
                
                    // if(j == 2) {
                    
                        // String bw = input[2];
                        
                        // String[] betweenwords = bw.split(" ");
                        
                        // for(int i = 0; i < betweenwords.length; i++) {
                            // if((betweenwords[i]).equals("is") || (betweenwords[i]).equals("are")) {
                            // } else {
                                // writer.write(betweenwords[i]+" ");
                            // }
                        // }
                        
                        // writer.write("\t");
                        
                    // } else {
                        // writer.write(input[j] + "\t");
                    // }
                    
                    
                // }
                // writer.write("\n");
            }       
        }
        
        writer.close();
    }
}