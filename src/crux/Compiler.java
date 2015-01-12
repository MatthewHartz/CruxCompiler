package crux;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static String studentName = "Matthew Hartz";
    public static String studentID = "87401675";
    public static String uciNetID = "hartzm";
    public static ArrayList<String> tokens = null;
	
	public static void main(String[] args)
	{
		Scanner s = null;
		File outHandle = null;
		
		int counter = 1;
		int fileCount = new File("Tests").list().length;
		
		for (int i = 1; i <= fileCount; i++) {
			String inFile = String.format("Tests/test%02d.crx", counter);
			String outFile = String.format("Tests/test%02d.out", counter);
			
			try {
	            s = new Scanner(new FileReader(inFile));
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.err.println("Error accessing the source file: \"" + inFile + "\"");
	            System.exit(-2);
	        }
			
			tokens = new ArrayList<String>();
			
			Token t = s.next();
	        while (!(t.kind.equals(Kind.EOF))) {
	            tokens.add(t.toString());
	            t = s.next();
	        }
	        tokens.add(t.toString());
	        
	        // Now compare the contents of the tokens to the outfile
	        try {
	            outHandle = new File(outFile);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.err.println("Error accessing the source file: \"" + inFile + "\"");
	            System.exit(-2);
	        }
	        
	        List<String> lines = null;
	        
	        try {
				lines = Files.readAllLines(FileSystems.getDefault().getPath(outFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        for (int j = 0; j < lines.size(); j++) {
	        	if (!lines.get(j).equals(tokens.get(j))) {
	        		System.out.println(String.format("Error in file: %s line %d", outFile, counter));       		
	        		System.exit(-2);
	        	}
	        }
		}
		
		System.out.println("SUCCESS EVERYTHING MATCHES!");
    }
}
