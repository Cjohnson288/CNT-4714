import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.sql.*;

public class DisplayGUI extends JFrame
{
	// JLabel
	private JLabel jlbDriver;
	private JLabel jlbDataBaseURL;
	private JLabel jlbUsername;
	private JLabel jlbPassword;
	private JLabel jlbConnectionStatus;
	// JComboBox
	private JComboBox driverList;
	private JComboBox dataBaseURLList;
	// text fields
	private JTextField jtfUsername;
	private JPasswordField jpfPassword;
	// jtextarea
	private JTextArea jtaSqlCommand;
	// jbuttons
	private JButton jbtConnectToDB;
	private JButton jbtClearSQLCommand;
	private JButton jbtExecuteSQLCommand;
	private JButton jbtClearResultWindow;
	//table
	private ResultSetTableModel tableModel = null;
	private JTable table;
	
	//
	private Connection connection;
	// keep track of database connection status
	private boolean connectedToDatabase = false;

	public DisplayGUI() throws ClassNotFoundException, SQLException, IOException 
	{
		// get all gui components setup
		this.createInstanceGUIComponents();
		
		
		//action for connect to DB button
		this.jbtConnectToDB.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				
				//load jdbc driver from the driverlist combobox. Use string class to protect against null selections
				try
				{
					Class.forName(String.valueOf(driverList.getSelectedItem()));
				} catch (ClassNotFoundException e) 
				{	
					//update connection status
					jlbConnectionStatus.setText("No Connection Now");
					jlbConnectionStatus.setForeground(Color.RED);
					e.printStackTrace();
					//clear table
					 table.setModel(new DefaultTableModel());
					 tableModel = null;
				}
				
				//establish connection to DB
				try 
				{
					//if connected disconnect
					if(connectedToDatabase == true)
					{
						connection.close();
						//change connection status and color
						jlbConnectionStatus.setText("No Connection Now");
						jlbConnectionStatus.setForeground(Color.RED);
						//update connection status
						connectedToDatabase = false;
						//clear table
						table.setModel(new DefaultTableModel());
						tableModel = null;
					}
						
					connection = DriverManager.getConnection(String.valueOf(dataBaseURLList.getSelectedItem()), jtfUsername.getText(), jpfPassword.getText());
					//change connection status and color
					jlbConnectionStatus.setText("Connected to " + String.valueOf(dataBaseURLList.getSelectedItem()));
					jlbConnectionStatus.setForeground(Color.GREEN);
					//update connection status
					connectedToDatabase = true;
				} catch (SQLException e) 
				{
					//update connection status
					jlbConnectionStatus.setText("No Connection Now");
					jlbConnectionStatus.setForeground(Color.RED);
					//clear table
					table.setModel(new DefaultTableModel());
					tableModel = null;
					e.printStackTrace();
				}
				
			}
			
		});
		
		
		//clear sql text area
		this.jbtClearSQLCommand.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				jtaSqlCommand.setText("");
			}
			
		});
		
		//execute sql command and update table
		this.jbtExecuteSQLCommand.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				
				//create an instance of abstract table model is we never had one before
				if(connectedToDatabase == true && tableModel == null)
				{
					try 
					{
						//send query string in lowercase for check
						tableModel = new ResultSetTableModel(jtaSqlCommand.getText(), connection);
						table.setModel(tableModel);
					} catch (ClassNotFoundException | SQLException | IOException e)
					{
						//clear table
						 table.setModel(new DefaultTableModel());
						 tableModel = null;
						//display warning
						 JOptionPane.showMessageDialog( null, 
			                        e.getMessage(), "Database error", 
			                        JOptionPane.ERROR_MESSAGE );
						e.printStackTrace();
					}
				}
				else
				//have abstract table model has been create execute string
				if(connectedToDatabase == true && tableModel != null)
				{
					//determine if we are executing a an update or query
					String query = jtaSqlCommand.getText(); 
					if(query.contains("select") || query.contains("SELECT"))
					{
						try 
						{
							tableModel.setQuery(query);
						} catch (IllegalStateException | SQLException e) 
						{
							//clear table
							 table.setModel(new DefaultTableModel());
							 tableModel = null;
							//display warning
							 JOptionPane.showMessageDialog( null, 
				                        e.getMessage(), "Database error", 
				                        JOptionPane.ERROR_MESSAGE );
						
							e.printStackTrace();
						}
					}
					else
					{
						try 
						{
							tableModel.setUpdate(query);
							//clear table
							 table.setModel(new DefaultTableModel());
							 tableModel = null;
						} catch (IllegalStateException | SQLException e) 
						{
							//clear table
							 table.setModel(new DefaultTableModel());
							 tableModel = null;
							//display warning
							 JOptionPane.showMessageDialog( null, 
				                        e.getMessage(), "Database error", 
				                        JOptionPane.ERROR_MESSAGE );
							 
							e.printStackTrace();
						}
					}
				}
			
				
				
				
				
			}
			
		});
		
		//clear table 
		this.jbtClearResultWindow.addActionListener(new ActionListener() 
		{

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//update number of rows to zero
				//tableModel.setNumberOfRows(0);
				//tableModel.fireTableDataChanged();
				table.setModel(new DefaultTableModel());
				tableModel = null;
				
			}
			
		});

		// panel for the buttons
		JPanel buttons = new JPanel(new GridLayout(1, 4));
		buttons.add(this.jlbConnectionStatus);
		buttons.add(this.jbtConnectToDB);
		buttons.add(this.jbtClearSQLCommand);
		buttons.add(this.jbtExecuteSQLCommand);

		// panels for textfields and tables
		JPanel labelsAndTextFields = new JPanel(new GridLayout(4, 2));
		labelsAndTextFields.add(this.jlbDriver);
		labelsAndTextFields.add(this.driverList);
		labelsAndTextFields.add(this.jlbDataBaseURL);
		labelsAndTextFields.add(this.dataBaseURLList);
		labelsAndTextFields.add(this.jlbUsername);
		labelsAndTextFields.add(this.jtfUsername);
		labelsAndTextFields.add(this.jlbPassword);
		labelsAndTextFields.add(this.jpfPassword);

		
		// panel for the top of the gui (jlb/jtf and jta)
		JPanel top = new JPanel(new GridLayout(1, 2));
		top.add(labelsAndTextFields);
		top.add(this.jtaSqlCommand);
		
		//panel for table and buttton
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout(20,0));
		south.add(new JScrollPane(this.table), BorderLayout.NORTH);
		south.add(this.jbtClearResultWindow, BorderLayout.SOUTH);
		
		// add panels to frame
		add(top, BorderLayout.NORTH);
		add(buttons, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
		
		
		  // dispose of window when user quits application (this overrides
	      // the default of HIDE_ON_CLOSE)
	      setDefaultCloseOperation( DISPOSE_ON_CLOSE );
	      
	      // ensure database connection is closed when user quits application
	      addWindowListener(new WindowAdapter() 
	         {
	            // disconnect from database and exit when window has closed
	            public void windowClosed( WindowEvent event )
	            {
	            	try 
	            	{
	            		//close connection on frame exit
						if(!connection.isClosed())
							connection.close();
					} catch (SQLException e) 
	            	{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	               System.exit( 0 );
	            } // end method windowClosed
	         });

	}// end constructor

	public void createInstanceGUIComponents() throws ClassNotFoundException, SQLException, IOException 
	{
		String[] driverString = { "com.mysql.jdbc.Driver", "" };
		String[] dataBaseURLString = { "jdbc:mysql://localhost:3312/project3", "" };

		// handle JLabel
		this.jlbDriver = new JLabel("JDBC Driver");
		this.jlbDataBaseURL = new JLabel("Database URL");
		this.jlbUsername = new JLabel("Username");
		this.jlbPassword = new JLabel("Password");
		this.jlbConnectionStatus = new JLabel("No Connection Now");
		this.jlbConnectionStatus.setForeground(Color.RED);

		// handle combo boxes
		this.driverList = new JComboBox(driverString);
		this.driverList.setSelectedIndex(0);
		this.dataBaseURLList = new JComboBox(dataBaseURLString);

		// handle text fields
		this.jtfUsername = new JTextField();
		this.jpfPassword = new JPasswordField();

		// handle jtextarea
		this.jtaSqlCommand = new JTextArea(3, 75);
		this.jtaSqlCommand.setWrapStyleWord(true);
		this.jtaSqlCommand.setLineWrap(true);

		// handle buttons
		this.jbtConnectToDB = new JButton("Connect to Database");
		this.jbtClearSQLCommand = new JButton("Clear SQL Command");
		this.jbtExecuteSQLCommand = new JButton("Execute SQL Command");
		this.jbtClearResultWindow = new JButton("Clear Result Window");
		
		//handle table
		this.table = new JTable();
	}
}// end class
