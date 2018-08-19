import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Driver 
{
	static String fileName = "config.txt";
	static int numOfStations;
	static int[] workLoads; //each index represents a station
	static SynchronizedConveyers[] conveyers;
	static Station[] stations;
	public static void main(String[] args) throws FileNotFoundException 
	{
		//read in file using scanner
		File file = new File(fileName);
		Scanner read = new Scanner(file);
		
		//get input from config.txt
		//place workloads in array
		numOfStations = read.nextInt();
		workLoads = new int[numOfStations];
		
		//create conveyers
		conveyers = new SynchronizedConveyers[numOfStations];
		
		//create instances of conveyers
		for(int i = 0; i < conveyers.length; i++)
			//create instance of conveyer
			conveyers[i] = new SynchronizedConveyers(i);
			 
		
		//create stations
		stations = new Station[numOfStations];
		
		//create a thread pool with numOfStaions threads
		ExecutorService shippingSim = Executors.newFixedThreadPool(numOfStations);
		
		for(int i = 0; i < workLoads.length && i < conveyers.length && i < stations.length; i++) 
		{
			//set workload
			workLoads[i] = read.nextInt();
			//create instance of stations
			stations[i] = new Station(workLoads[i], i, numOfStations);
			//set input and output conveyers
			stations[i].setInput(conveyers[stations[i].getInputConNum()]);
			stations[i].setOutput(conveyers[stations[i].getOutputConNum()]);
			
			//start up the stations
			try
			{
					shippingSim.execute(stations[i]);
			}
			catch (Exception exc)
			{
				exc.printStackTrace();
			}

		}
				
		//end threadpool
		shippingSim.shutdown();
		
		
	}

}
