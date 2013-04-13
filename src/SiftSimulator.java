import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JFrame;


public class SiftSimulator {

	private double alfa = 0.9;
	private int CW = 30;
	private int Wsift = 30;
	private int num_nd_cicles = 100;
	
	private int num_devices = 5;
	
	private Device devices[];
	
	private Device d1 = new Device("d1", CW, alfa, Wsift);
	private Device d2 = new Device("d2", CW, alfa, Wsift);
	private Device d3 = new Device("d3", CW, alfa, Wsift);
	private Device d4 = new Device("d4", CW, alfa, Wsift);
	private Device d5 = new Device("d5", CW, alfa, Wsift);
	private Device d6 = new Device("d6", CW, alfa, Wsift);
	
	private int collisions_m[][] = new int[num_nd_cicles][CW];
	private int n_discovered[] = new int[num_nd_cicles];
	private int tot_n_collisions = 0;
	
	MyWriter out;
	
	/**
	 * Default Constructor
	 */
	public SiftSimulator(){
		out = new MyWriter("main_out_alfa" + alfa + "_CW" + CW + "_Wsift" + Wsift, false); 
		out.openFile();
		StartSimulation();
	}
	
	public SiftSimulator(int num_devices, int alfa, int CW){
		
		this.CW = CW;
		this.alfa = alfa;
		
		out = new MyWriter("main_out_dev#" + num_devices + "_alfa" + alfa + "_CW" + CW + "" + Wsift, false);
		out.openFile();
		
		devices = new Device[num_devices];
		int id = 0;
		
		for (Device d : devices) {
			d = new Device("device_" + ++id, CW, alfa, Wsift);
		}
		StartSimulation();
	}
	
	/**
	 * 
	 */
	public void StartSimulation() {
		
		double slotSelected[] , slotD2, slotD3, slotD4, slotD5;
		double probT1, probT2, probT3, probT4, probT5;
		
		
		out.write("Start Sim.: alfa = " + alfa + ", CW = " + CW + "\n\n");
		
		int window = 0; 
		while(window < num_nd_cicles){
			out.write("\nSIFT EXTRACTION:");
			/* Device 1 */
			slotD1 = (int) d1.doSlotSiftSelection();
			out.write("\nslotD1:\t" + slotD1 + "\t");
			probT1 = (((double)Wsift - slotD1)/(double)Wsift);
			out.write("probT1:\t" + probT1 + "\n");
			
			/* Device 2 */
			slotD2 = (int) d2.doSlotSiftSelection();
			out.write("slotD2:\t" + slotD2 + "\t");
			probT2 = ((((double)Wsift) - slotD2)/((double)Wsift));
			out.write("probT2:\t" + probT2 + "\n");

			/* Device 3 */
			slotD3 = (int) d3.doSlotSiftSelection();
			out.write("slotD3:\t" + slotD3 + "\t");
			probT3 = ((((double)Wsift) - slotD3)/((double)Wsift));
			out.write("probT3:\t" + probT3 + "\n");
			
			/* Device 4 */
			slotD4 = (int) d4.doSlotSiftSelection();
			out.write("slotD4:\t" + slotD4 + "\t");
			probT4 = ((((double)Wsift) - slotD4)/((double)Wsift));
			out.write("probT4:\t" + probT4 + "\n");
			
			/* Device 5 */
			slotD5 = (int) d5.doSlotSiftSelection();
			out.write("slotD5:\t" + slotD5 + "\t");
			probT5 = ((((double)Wsift) - slotD5)/((double)Wsift));
			out.write("probT5:\t" + probT5 + "\n");
			
			out.write("\nSTART CONTENTION WINDOW " + window + " *********");
			//System.out.println("START NEW WINDOW ********* ");
			int current_slot = 1;
			int nd_find = 0;
			int w_transmitted = 0;
			double p_correction = 0;
			//START COLLISION WINDOW
			while (current_slot <= CW) {
				p_correction = (1.0 - ((double)CW-current_slot)/CW)/2;
				int s_transmitted = 0;
				out.write("\nAT SLOT " + current_slot + "");
				
				double probT = 3*distributionSift(alfa, CW, current_slot)+0.03 ;
				
				s_transmitted = d1.tryToTransmit(probT, window) //probT1
							+ d2.tryToTransmit(probT, window) //probT2 + p_correction
							+ d3.tryToTransmit(probT, window) 
							+ d4.tryToTransmit(probT, window) 
							+ d5.tryToTransmit(probT, window);
				
				out.write(" Transmitting: " + s_transmitted + "");
				if(s_transmitted > 1){
					collisions_m[window][current_slot-1]++; 
					tot_n_collisions++;
					System.out.println(tot_n_collisions + "COLLISION #: " + s_transmitted + ": IN COLLISION WINDOW " + window + " AT SLOT " + current_slot);
					out.writeln("\tCOLLISION #: " + s_transmitted + ": IN COLLISION WINDOW " + window + " AT SLOT " + current_slot);
				}
				if (s_transmitted == 1) nd_find++;
				w_transmitted += s_transmitted;
				current_slot++;
			}
			
			n_discovered[window] = nd_find;
			out.writeln("IN WINDOW " + window + " #NDiscovered: " + nd_find + " #never transmit " + (num_devices - w_transmitted));
			
			window++;
		}//while cycles
		
		//WRITE THE DEVICE'S LOG
		//d1.writeResults(num_nd_cicles);
		//d2.writeResults(num_nd_cicles);
		//d3.writeResults(num_nd_cicles);
		//d4.writeResults(num_nd_cicles);
		//d5.writeResults(num_nd_cicles);
		
//		// Count the total collisions
//		int n_collision = 0;
//		for( int c[] : collisions_m){
//			for(int n : c) 
//				if(n!=0) n_collision++;
//		}
		
		out.writeln("\n*********************** SUMMARY ***************************\n");
		out.write("\n #N_DISCOVERED: ");
		int n_disc = 0;
		for(int n : n_discovered){
			n_disc+=n;
			out.write(n + "\t");
			
		}
		
		int arrayTotSlotCollision[] = new int[collisions_m[0].length]; 
		for(int a = 0; a < arrayTotSlotCollision.length; a++){
			for(int n : collisions_m[a]){
				arrayTotSlotCollision[a]+=n;
			}
			out.write("\nSlot " + a + " #collisioni: " + arrayTotSlotCollision[a]);
		}
		out.write("\n");
		//for(int x = 1; x<=CW; x++)
		//	out.write("\t" + distributionSift(alfa, CW, x));
		//System.out.println("\n COLLISIONI " + n_collision + " Total ND discovered " + n_disc + " in num windows " + num_nd_cicles );
		out.writeln("\n# COLLISION n: " + tot_n_collisions + " Total ND discovered: " + n_disc + " in num di cicli: " + num_nd_cicles + " %ND: " + ((double)n_disc)/num_nd_cicles);
		//plot(arraySlot);
		out.closeFile();
		
	}
	
	/*
	 * Px: Sift Distribution for the specific x
	 * for x = 1, 2, ..., CW. 
	 */
	public double distributionSift(double alfa, int CW, int x){
		return (x<=CW && x>0)?(1-alfa)*Math.pow(alfa, CW)/(1-Math.pow(alfa, CW))*Math.pow(alfa, -x):-1.0;
	}
	
	/**
	 * deprecated method by DanAme
	 * @param a
	 */
	public void plot(int a[]){
		
        Plot2D test = new Plot2D();
        JFrame f = new JFrame();
        double x[]=new double[a.length];
        double y[]=new double[a.length];
        for (int i=0;i<a.length;i++) {
			x[i]=a[i];
        	y[i]=0;
		}
        test.dataX=x;
        test.dataY=y;
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(test.getContent());
        f.add(test.getUIPanel(), "Last");
        f.setSize(400,400);
        f.setLocation(50,50);
        f.setVisible(true);
		
	}
}