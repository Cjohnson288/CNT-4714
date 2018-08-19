import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;



public class SynchronizedConveyers 
{
	private int conveyorNum;
	
	//lock to control synchronization with the conveyer
	public ReentrantLock accessLock = new ReentrantLock();

	private boolean busy = false; //whether conveyer is occupied
	
	
	public SynchronizedConveyers(int conveyerNum)
	{
		this.conveyorNum = conveyerNum;
	}
	
	
	
	public void inputConnection(int stationNum) 
	{	
		
		System.out.println("Station " + stationNum + ": successfully moves packages on conveyer " + this.conveyorNum);

		
	}
	
	
	
	public void outputConnection(int stationNum) 
	{	
		System.out.println("Station " + stationNum + ": successfully moves packages on conveyer " + this.conveyorNum);
	}
	
}
