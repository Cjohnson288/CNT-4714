import java.util.Random;

public class Station implements Runnable 
{
	private static int maxNumStations;
	private int workLoad = 0;
	private int inputConNum;
	private int outputConNum;
	private int stationNum;
	private SynchronizedConveyers input;
	private SynchronizedConveyers  output;
	private static Random generator = new Random();
	
	
	//constructor
	public Station(int workLoad, int stationNum, int maxNumStations)
	{
		this.workLoad = workLoad;
		this.stationNum = stationNum;
		this.maxNumStations = maxNumStations;
		this.setInputConNum();
		this.setOutputConNum();
		
		System.out.println("Station " + stationNum + ": Workload set. Station " + this.stationNum + " has " + this.workLoad + " package groups to move");
	}
	
	
	@Override
	public void run() { 
		
		//run this loop until workload is complete
		while(this.workLoad != 0)
		{
			//get input lock
			if(input.accessLock.tryLock())
			{
				System.out.println("Station " + this.stationNum + ": granted access to conveyer " + this.inputConNum);
				
				//get output lock
				if(output.accessLock.tryLock())
				{
					System.out.println("Station " + this.stationNum + ": granted access to conveyer " + this.outputConNum);
					doWork(); //execute dowork we have both locks
					
				
				
				}
				else
				{	
					//release input lock because we did not get output lock
					System.out.println("Station " + this.stationNum + ": released access to conveyer " + this.inputConNum);
					input.accessLock.unlock();
					
					try 
					{
						//put thread to sleep (simulate a wait)
						Thread.sleep(1500);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
				//release locks
				if(input.accessLock.isHeldByCurrentThread())
				{
					System.out.println("Station " + this.stationNum + ": released access to conveyer " + this.inputConNum);
					input.accessLock.unlock();

				}
				
				if(output.accessLock.isHeldByCurrentThread())
				{
					System.out.println("Station " + this.stationNum + ": released access to conveyer " + this.outputConNum);
					output.accessLock.unlock();
				}
				
				try 
				{
					//put thread to sleep (simulate a wait)
					Thread.sleep(1500);
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}

			}
		}
		
		//done with all workloads
		System.out.println(); 
		System.out.println("* * Station " + stationNum + ": Workload successfully completed * *");
		System.out.println();
		
		
	}
	
	public void doWork() 
	{	
	
				this.input.inputConnection(this.stationNum);
				this.output.outputConnection(this.stationNum);
				this.workLoad--; //decrement workLoad
				System.out.println("Station " + this.stationNum + ": has " + this.workLoad + " package groups left to move");
	}
	
	
	
	public SynchronizedConveyers getInput() 
	{
		return input;
	}




	public void setInput(SynchronizedConveyers input)
	{
		this.input = input;
	}




	public SynchronizedConveyers getOutput()
	{
		return output;
	}




	public void setOutput(SynchronizedConveyers output) 
	{
		this.output = output;
	}
	
	public int getInputConNum() 
	{
		return inputConNum;
	}



	//input conveyer is smallest # conveyor
	//station 'n' needs 'n' & 'n -1' except
	//station '0' needs '0' & maxNumStations - 1
	public void setInputConNum()
	{
		if(stationNum == 0)
		{
			this.inputConNum = 0;
		}
		else
		{
			this.inputConNum = this.stationNum - 1;
		}
		
		
		System.out.println("Station " + stationNum + ": In-Connection set to conveyer " + this.inputConNum);

	}




	public int getOutputConNum() 
	{
		return outputConNum;
	}


	//output conveyer is largest # conveyor
	//station 'n' needs 'n' & 'n -1' except
	//station '0' needs '0' & maxNumStations - 1
	public void setOutputConNum() 
	{
		if(this.stationNum == 0)
		{
			this.outputConNum = this.maxNumStations - 1;
		}
		else
		{
			this.outputConNum = this.stationNum;
		}
		
		System.out.println("Station " + stationNum + ": Out-Connection set to conveyer " + this.outputConNum);

	}
	
}
