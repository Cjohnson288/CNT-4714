/* Name: Jonathan Cotto
 * Course: CNT 4714 - Summer 2018
 * Assignment title: Program 1 - Event Driven Programming
 * Date: Tuesday May 29, 2018
 */


import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.*;
import java.util.*;

public class BookStore extends JFrame {
	private String inventoryFile = "inventory.txt";
	private ArrayList<Book> inventory;
	private Order order = new Order();
	
	// create Text Fields
	private JTextField jtfNumItems = new JTextField();
	private JTextField jtfBookID = new JTextField();
	private JTextField jtfQuantity = new JTextField();
	private JTextField jtfItemInfo = new JTextField();
	private JTextField jtfTotalItems = new JTextField();

	// create Buttons
	private JButton jbtProcessItem = new JButton("Process Item #1");// need to update item #


	private JButton jbtConfirmItem = new JButton("Confirm Item #1");// need to update item #
	private JButton jbtViewOrder = new JButton("View Order");
	private JButton jbtFinishOrder = new JButton("Finish Order");
	private JButton jbtNewOrder = new JButton("New Order");
	private JButton jbtExit = new JButton("Exit");

	// create Jlabels
	JLabel jlbSubtotal = new JLabel("Order Subtotal for 0 item(s):");
	JLabel jlbBookID = new JLabel("Enter Book ID for Item #1:");
	JLabel jlbQuantity = new JLabel("Enter Quantitiy for Item #1:");
	JLabel jlbItemInfo = new JLabel("Item #1 Info:");

	// constuctor
	public BookStore() throws FileNotFoundException 
	{
		this.getInventoryFromFile();
		//panel p1 holds textfields and labels
		//p1 will have a gridlayout of 5 rows and 2 columns
		JPanel p1 = new JPanel(new GridLayout(5,2));
		p1.add(new JLabel("Enter number of items in this order:"));
		p1.add(jtfNumItems);
		p1.add(jlbBookID);
		p1.add(jtfBookID);
		p1.add(jlbQuantity);
		p1.add(jtfQuantity);
		p1.add(jlbItemInfo);
		p1.add(jtfItemInfo);
		p1.add(jlbSubtotal);
		p1.add(jtfTotalItems);
		
		
		
		//panel p2 holds the six buttons
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p2.add(jbtProcessItem);
		p2.add(jbtConfirmItem);
		p2.add(jbtViewOrder);
		p2.add(jbtFinishOrder);
		p2.add(jbtNewOrder);
		p2.add(jbtExit);
		
		//update button text
		//this.jbtProcessItem.setText("Process Item #" + totalItems);
		//this.jbtConfirmItem.setText("Confirm Item #" + totalItems);
		
		//deactivate buttons
		this.jbtConfirmItem.setEnabled(false);
		this.jbtViewOrder.setEnabled(false);
		this.jbtFinishOrder.setEnabled(false);
		
		//deactivate textfields
		this.jtfTotalItems.setEnabled(false);
		this.jtfItemInfo.setEnabled(false);
		
		//add the panels to the frame
		add(p1, BorderLayout.NORTH);
		add(p2, BorderLayout.SOUTH);
		
		
		//actionlisteners for all buttons
		jbtProcessItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//get info
				int numOfItemsInOrder = Integer.parseInt(jtfNumItems.getText());
				int bookID = Integer.parseInt(jtfBookID.getText());
				int quantityOfItem = Integer.parseInt(jtfQuantity.getText());
				
				//set maxNumofItems for  new order
				if(order.getMaxNumItems() == -1 && numOfItemsInOrder > 0) {
					order.setMaxNumItems(numOfItemsInOrder);
					jtfNumItems.setEnabled(false);
				}
				//search for book
				int bookIndex = linearSearch(bookID);
				//found book
				if(bookIndex != -1) 
				{
					// give this item info to order to print
					Book foundBook = inventory.get(bookIndex);
					order.setItemInfo(foundBook.getBookID() + "", foundBook.getTitle(), foundBook.getPrice() + "", quantityOfItem + "", order.getDiscountPercentage(quantityOfItem) + "", order.getTotalDiscount(quantityOfItem, foundBook.getPrice()) + "");
					String bookInfo = foundBook.getBookID() + foundBook.getTitle() +  " $" + foundBook.getPrice() + " " + quantityOfItem + " " + order.getDiscountPercentage(quantityOfItem) + "% " + order.getTotalDiscount(quantityOfItem, foundBook.getPrice()); //need to get discound
					jtfItemInfo.setText(bookInfo);
					jbtConfirmItem.setEnabled(true);
					jbtProcessItem.setEnabled(false);
					order.setOrderSubtotal(quantityOfItem, foundBook.getPrice());
					jtfItemInfo.setEnabled(false);
					jtfTotalItems.setEnabled(false);
				}
				//book not found display alert
				else 
				{
					JOptionPane.showMessageDialog(null, "Book ID " + bookID + " not in file.");
				}
			}
			
		});
		
		
		//action on each button
		jbtConfirmItem.addActionListener(new ActionListener(){		
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int numOfItemsInOrder = Integer.parseInt(jtfNumItems.getText());
				int bookID = Integer.parseInt(jtfBookID.getText());
				int quantityOfItem = Integer.parseInt(jtfQuantity.getText());
				
				if(numOfItemsInOrder > order.getMaxNumItems())
					System.out.println("went over qantity");
				
				//increment currentNumofItems
				order.setCurrentNumItems(quantityOfItem);
				//update subtotal items
				order.setTotalItems(order.getTotalItems() + 1);
				
				JOptionPane.showMessageDialog(null, "Item #" + order.getTotalItems() + " accepted");
				
				//prepare transaction.txt line
				order.prepareTransaction();
				
				//add item to viewOrder
				order.addToViewOrder(jtfItemInfo.getText());
				
			
				//enable buttons
				jbtProcessItem.setEnabled(true);
				jbtViewOrder.setEnabled(true);
				jbtFinishOrder.setEnabled(true);
				jbtConfirmItem.setEnabled(false);
				jtfNumItems.setEnabled(false);
				
				//update button text
				jbtProcessItem.setText("Process Item #" + (order.getTotalItems() + 1));
				jbtConfirmItem.setText("Confirm Item #" + (order.getTotalItems() + 1));
				
				//update textFields
				jtfBookID.setText("");
				jtfQuantity.setText("");
				jtfTotalItems.setText("$" +  new DecimalFormat("#0.00").format(order.getOrderSubtotal()));
				
				//update labels
				jlbSubtotal.setText("Order subtotal for " + order.getCurrentNumItems() + " item(s)");
				jlbBookID.setText("Enter Book ID for Item #" + (order.getTotalItems() + 1) + ":");
				jlbQuantity.setText("Enter quantity for Item #" + (order.getTotalItems() + 1) + ":");
				if(order.getCurrentNumItems() < order.getMaxNumItems())
				jlbItemInfo.setText("Item #" + (order.getTotalItems() + 1) + " info:");
				
				//final item order
				if(order.getCurrentNumItems() >= order.getMaxNumItems()) {
					jlbBookID.setVisible(false);
					jlbQuantity.setVisible(false);
					jbtProcessItem.setEnabled(false);
					jbtConfirmItem.setEnabled(false);
				}
			}
		
		
		});
		
		
		jbtViewOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, order.getViewOrder());
			}
		});
		
		
		
		jbtFinishOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//write items to transactions.txt
				try {
					order.printTransactions();
					JOptionPane.showMessageDialog(null, order.getFinishOrder());

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				BookStore.super.dispose(); //dispose frame
			}
		});
		
		
		jbtNewOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				BookStore.super.dispose(); //dispose frame
				//run main
				try {
					BookStore.main(null);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		jbtExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BookStore.super.dispose(); //dispose frame
			}
		});
		
	}


	public int linearSearch(int BookID) {
		for(int i = 0; i < this.inventory.size(); i++) {
			Book currentBook = inventory.get(i);
			if(currentBook.getBookID() == BookID)
				return i;
		}
		return -1;
	}
	
	public void getInventoryFromFile() throws FileNotFoundException {
		// create load all books in an arraylist
		this.inventory = new ArrayList<Book>();
		File file = new File("inventory.txt");
		Scanner textFile = new Scanner(file);

		// scan in inventory into arraylist
		while (textFile.hasNextLine()) {
			// grab next inventory line and parse into 3 strings
			String book = textFile.nextLine();
			String[] bookInfo = book.split(",");

			// create a book and set fields
			Book currentBook = new Book();
			currentBook.setBookID(Integer.parseInt(bookInfo[0]));

			currentBook.setTitle(bookInfo[1]);

			currentBook.setPrice(Double.parseDouble(bookInfo[2]));

			// add book to inventory arraylist
			inventory.add(currentBook);
		}
		
		//close stream
		textFile.close();
		// testing
		for (int i = 0; i < inventory.size(); i++) {
			Book current = inventory.get(i);
			System.out.println(current.getBookID() + ", " + current.getTitle() + ", " + current.getPrice());
		}
	}

	
	public ArrayList<Book> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<Book> inventory) {
		this.inventory = inventory;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		BookStore frame = new BookStore();
		frame.pack(); // fit windows for screen
		frame.setTitle("Book Store");
		frame.setLocationRelativeTo(null); // center windows
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // close window
		frame.setVisible(true); // display window
	}
}
