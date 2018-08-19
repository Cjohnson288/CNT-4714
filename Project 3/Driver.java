import java.awt.BorderLayout;
import java.io.IOException;
import java.sql.SQLException;

public class Driver
{
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException 
	{
		DisplayGUI myFrame = new DisplayGUI();
		myFrame.setVisible(true);
		myFrame.pack();
		myFrame.setLayout(new BorderLayout(2,0));
	}
}
