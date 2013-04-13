import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * 
 */

/**
 * @author damendola
 *
 */
public class MyWriter {
	private BufferedWriter out;
	private boolean useConsole = true;
	private String filename = "out_" + new Date().toString();

	public MyWriter(){
	}

	public MyWriter(String filename, boolean useConsole){
		this.useConsole = useConsole;
		this.filename = filename;
	}
	
	public MyWriter(String filename){
		this.filename = filename;
	}
	
	public MyWriter(boolean useConsole){
		this.useConsole = useConsole;
	}
	
	public void write(String words){
		
		try {
			if(useConsole) System.out.print(words);
			out.write(words);
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeln(String line){
		
		try {
			if(useConsole) System.out.println(line);
			out.write("\n"+ line);
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void openFile(){
		
		try {
			File file = new File(filename + ".txt");
			
			FileWriter fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void closeFile(){
			
			try {
				out.close();
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
	}

}
