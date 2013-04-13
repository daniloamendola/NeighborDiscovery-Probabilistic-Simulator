import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

/**
 * 
 */

/**
 * @author damendola
 *
 */
public class Device{
	
	String device_id = "";
	double alfa;
	int CW;
	int Wsift;
	int arraySlot[];
	int errOutOfBoud = 0;
	LinkedList<Double> siftExtractions = new LinkedList<Double>();
	LinkedList<Double> outOfBoundExtractions = new LinkedList<Double>();
	int last_tx_cicle = -1;
	Random rnd = new Random(device_id.hashCode());
	
	public Device(){ }
	
	public Device(String device_id, int CW, double alfa){
		this.device_id = device_id;
		this.CW = CW;
		this.Wsift = CW;
		this.alfa = alfa;
		arraySlot = new int[Wsift+1];
		errOutOfBoud = 0;
	}
	
	public Device(String device_id, int CW, double alfa, int Wsift){
		this.device_id = device_id;
		this.CW = CW;
		this.Wsift = Wsift;
		this.alfa = alfa;
		arraySlot = new int[Wsift+1];
		errOutOfBoud = 0;
		
	}
	

	/**
	 * @return
	 */
	public double doSlotSiftSelection(){
		return doSlotSiftSelection(Wsift);
	}
	
	
	/**
	 * @return
	 */
	public double doSlotSiftSelection(int CWsift){
		
		double sift = selectSiftSlot(alfa, CWsift);//il -1 nonci sarebbe 
		
		siftExtractions.add(new Double(sift));
		
		if ((sift >= 0.0 && sift < CWsift)){ // così esce al massimo il num 29.9 poichè voglio 30 slot
			arraySlot[((int)sift)]++;
		} else {
			outOfBoundExtractions.add(new Double(sift));
			errOutOfBoud++;
		}
		
		return sift; // DA RIVEDERE
	}
	
	/**
	 * @param probT
	 * @return
	 */
	public int tryToTransmit(double probT, int current_cicle){
		if(last_tx_cicle == current_cicle)
			// ha già trasmesso
			return 0;
		else{			
			if(Math.random() < probT) {//rnd.nextDouble()
				// trasmette
				last_tx_cicle = current_cicle;
				return 1;
			} // else non trasmette
			else return 0;
		}
	}
	
	/*
	 * Select a slot using the Sift distribution
	 */
	public double selectSiftSlot(double alfa, int CW){
		
		double u_rand = Math.random();//rnd.nextDouble();
		double constA = ((1.0-alfa)*Math.pow(alfa, CW))/(1-Math.pow(alfa, CW));
		
		double sift = -log(-Math.log(alfa)*u_rand/constA, alfa) ;//- 0.5;//+0,5 estrae 1 in più..
		// System.out.println("SIIIIIIIIIIIFFFFTTTTT:"+sift + " Urand: " + u_rand);
		return sift;
	}
	
	/*
	 * double log(double num, double base)
	 * Return the log of num using specific base.
	 */
	private double log(double num, double base){
		return Math.log10(num)/Math.log10(base);
	}
	
	/*
	 * Write the results in a file
	 */
	public void writeResults(int num_extrac){
		
		try {
			String filename = "out_" + device_id + "_";
			File file = new File(filename + ".txt");
			if (file.exists()) {
				file = new File(filename + new Date().toString()  + ".txt");
			}
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			int somma = 0; 
			for (int i : arraySlot) {
				System.out.print(i + "\t");
				out.write(i + "\t");
				somma += i;
			}
			System.out.println("\n");
			out.write("\n \n");
			for (int i : arraySlot) {
				System.out.print(((float)i)/somma + "\t");
				out.write(((float)i)/somma + "\t");
			}
			System.out.println();
			System.out.println("\n Extracted: " + somma + " slot, con " + errOutOfBoud + " out of bound; Finito!");
			out.write("\n \n Total number of selected = " + somma);
			
			// Write the total log of the selected value
			String filenamelog = "log_" + device_id + "_";
			File filelog = new File(filenamelog + new Date().toString() + ".txt");
			FileWriter fstreamlog = new FileWriter(filelog);
			BufferedWriter log = new BufferedWriter(fstreamlog);
			for (Double d : siftExtractions) {
				//System.out.print(d + "\t");
				log.write(d + "\t");
			}
			//
			log.write("\n \n Out of bound selection: \n");
			for (Double d : outOfBoundExtractions) {
				//System.out.print(d + "\t");
				log.write(d + "\t");
			}
			
			// Close the output stream
			log.close();
			out.close();
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
