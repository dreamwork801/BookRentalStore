/* Name: Zach Colby
 * Course CNT 4717 - Fall 2015
 * Assignment title: Program 1 - Event-driven Programming
 * Date: Wednesday, September 9th, 2015
 */


import javax.swing.*;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimeZone;

public class MyStore
{
   // Global Variables
   String InputFile = "inventory.txt";
   String OutputFile = "transactions.txt";
   int TaxRate = 6;
   int numberOfItems = 0;
   int currentItem = 1;
   int currentBookID = 0;
   int currentQuantity = 0;
   double currentSubTotal = 0;
   ArrayList<String> orderInfo =  new ArrayList<String>();
   DecimalFormat df = new DecimalFormat("#.00");
   
   // GUI Components
   JFrame frame;
   JPanel panel;
   JPanel panelButtons;
   JLabel label1 = new JLabel("Enter number of items in this order:");
   JLabel label2 = new JLabel("Enter Book ID for Item #1:");
   JLabel label3 = new JLabel("Enter quanity for Item #1:");
   JLabel label4 = new JLabel("Item #1 info:");
   JLabel label5 = new JLabel("Order subtotal for 0 item(s):");
   JTextField textField1 = new JTextField(50);
   JTextField textField2 = new JTextField(50);
   JTextField textField3 = new JTextField(50);
   JTextField textField4 = new JTextField(50);
   JTextField textField5 = new JTextField(50);
   JButton buttonProcess = new JButton("Process Item #" + String.valueOf(currentItem));
   JButton buttonConfirm = new JButton("Confirm Item #" + String.valueOf(currentItem));
   JButton buttonView = new JButton("View Order");
   JButton buttonFinish = new JButton("Finish Order");
   JButton buttonNew = new JButton("New Order");
   JButton buttonExit = new JButton("Exit");
   ArrayList<Inventory> myInventory = new ArrayList<Inventory>();

   // Constructor method
   public MyStore()
   {
      buildGUI();
   }
   
   // Method that makes the frame and panels and adds elements to it.
   private void buildGUI() {
	  // Create a panel and add the components to it
      panel = new JPanel();
      panel.setLayout(new GridLayout(5,2));
      panel.add(label1);
      panel.add(textField1);
      panel.add(label2);
      panel.add(textField2);
      panel.add(label3);
      panel.add(textField3);
      panel.add(label4);
      panel.add(textField4);
      panel.add(label5);
      panel.add(textField5);
      
      // Align the labels to the right
      label1.setHorizontalAlignment(JLabel.RIGHT);
      label2.setHorizontalAlignment(JLabel.RIGHT);
      label3.setHorizontalAlignment(JLabel.RIGHT);
      label4.setHorizontalAlignment(JLabel.RIGHT);
      label5.setHorizontalAlignment(JLabel.RIGHT);
      
      panelButtons = new JPanel();
      panelButtons.add(buttonProcess);
      panelButtons.add(buttonConfirm);
      panelButtons.add(buttonView);
      panelButtons.add(buttonFinish);
      panelButtons.add(buttonNew);
      panelButtons.add(buttonExit);
      

      // Create a frame and add the panel to it
      JFrame frame = new JFrame("'Turn the Page' Book Store");
      frame.setLayout(new GridLayout(2,1));
      frame.add(panel);
      frame.add(panelButtons);
      

      // Display the frame on the screen
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack(); // size frame to fit its components
      frame.setLocationRelativeTo(null); // center on screen
      frame.setVisible(true);
      frame.setSize(800, 300);
      
   }
   private void ActivateButtons() {
	   buttonProcess.setActionCommand("Process");
	   buttonConfirm.setActionCommand("Confirm");
	   buttonView.setActionCommand("View");
	   buttonFinish.setActionCommand("Finish");
	   buttonNew.setActionCommand("New");
	   buttonExit.setActionCommand("Exit");
	   
	   buttonProcess.addActionListener(new ButtonListener());
	   buttonConfirm.addActionListener(new ButtonListener());
	   buttonView.addActionListener(new ButtonListener());
	   buttonFinish.addActionListener(new ButtonListener());
	   buttonNew.addActionListener(new ButtonListener());
	   buttonExit.addActionListener(new ButtonListener());
	   
	   // We want these elements buttons disabled initially.
	   buttonConfirm.setEnabled(false);
	   buttonFinish.setEnabled(false);
	   buttonView.setEnabled(false);
	   textField4.setEnabled(false);
	   textField5.setEnabled(false);
   }
   
   private class ButtonListener implements ActionListener{

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		switch(command){
			case "Process":
				ProcessHelper();
				break;
			case "Confirm":
				ConfirmHelper();
				break;
			case "View":
			    ViewHelper();
				break;
			case "Finish":
			    FinishHelper();
				break;
			case "New":
			    NewHelper();
				break;
			case "Exit":
				System.exit(0);
				break;
			default:
				throw new IllegalArgumentException("Invalid Command given");
		}
	}
   }
   
// Process Helper and Helper Methods
   private void ProcessHelper(){
		numberOfItems = Integer.parseInt(textField1.getText());
		currentBookID = Integer.parseInt(textField2.getText());
		if (textField3.getText().isEmpty()){
		    JOptionPane.showMessageDialog(null, "Please inset a valid quantity for the book", "No Quantity", JOptionPane.WARNING_MESSAGE);
		    return;
		}
		currentQuantity = Integer.parseInt(textField3.getText());
		buttonConfirm.setEnabled(true);
		buttonProcess.setEnabled(false);
		textField1.setEnabled(false);
		
		String book = GetBookByID();
		orderInfo.add(book);
		textField4.setText(book);
		textField5.setText("$" + df.format(currentSubTotal));
		label5.setText("Order subtotal for " + currentItem + " item(s):");
   }

// Confirm Helper and Helper Methods
   private void ConfirmHelper(){
	   if (IsBookInInventory()){
		   JOptionPane.showMessageDialog(null, "Item #" + currentItem + " accepted", "Success", JOptionPane.INFORMATION_MESSAGE);
		   textField2.setText("");
		   textField3.setText("");
		   buttonView.setEnabled(true);
		   currentItem++;
		   int previousItem = currentItem-1;
		   if (currentItem <= numberOfItems){
		       buttonProcess.setText("Process Item #" + String.valueOf(currentItem));
		       buttonConfirm.setText("Confirm Item #" + String.valueOf(currentItem));
		       buttonProcess.setEnabled(true);
		       buttonConfirm.setEnabled(false);
		       
		       label2.setText("Enter Book ID for Item #" + currentItem + ":");
		       label3.setText("Enter quanity for Item #" + currentItem + ":");
		       label4.setText("Item #" + previousItem + " info:");
		   }
		   else {
		       buttonProcess.setText("Process Item");
		       buttonConfirm.setText("Confirm Item");
		       buttonProcess.setEnabled(false);
		       buttonConfirm.setEnabled(false);
               textField2.setEnabled(false);
               textField3.setEnabled(false);
		       buttonFinish.setEnabled(true);
               label2.setText("");
               label3.setText("");
               label4.setText("Item #" + previousItem + " info:");
		   }
	   }
	   else{
		   JOptionPane.showMessageDialog(null, "Book ID " + currentBookID + " not on file", "No Record", JOptionPane.WARNING_MESSAGE);
	       buttonConfirm.setEnabled(false);
	       buttonProcess.setEnabled(true);
	       
	   }
   }

// View Helper and Helper Methods
   private void ViewHelper() {
       JOptionPane.showMessageDialog(null, CreateLineItemString(), "View Order", JOptionPane.INFORMATION_MESSAGE);
   }

// Finish Helper and Helper Methods
   private void FinishHelper() {
       LocalDateTime dateTimeNow = LocalDateTime.now();
       WriteTransactionToFile(dateTimeNow);
       JOptionPane.showMessageDialog(null, CreateOrderString(dateTimeNow), "Order Confirmation", JOptionPane.INFORMATION_MESSAGE);
       
       buttonFinish.setEnabled(false);
   }

   private String CreateOrderString(LocalDateTime dateTimeNow){
       String message = "";
       double orderTotal = currentSubTotal * (1+TaxRate/100.0);
       message = message.concat("Date: " + dateTimeNow.format(DateTimeFormatter.ofPattern("MM/dd/yy hh:mm:ss a")) + " " + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + "\n");
       message = message.concat("\nNumber of line items: " + orderInfo.size() + "\n");
       message = message.concat("\nItem # / ID / Title / Price / Qty / Disc % / Subtotal:\n");
       message = message.concat("\n" + CreateLineItemString() + "\n\n");
       message = message.concat("\n Order subtotal: $" + df.format(currentSubTotal) + "\n");
       message = message.concat("\n Tax rate: " + TaxRate + "%\n");
       message = message.concat("\n Order total: $" + df.format(orderTotal) + "\n");
       message = message.concat("\nThanks for shopping at 'Turn the Page' Book Shop!");
       
       return message;
   }
   
   private void WriteTransactionToFile(LocalDateTime dateTimeNow) {
       Writer writer = null;
       String transactionID = dateTimeNow.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
       try {
           writer = new BufferedWriter(new FileWriter(OutputFile, true));
           for (String line : orderInfo ){
               writer.write(transactionID + ", " + line + ", " + dateTimeNow.format(DateTimeFormatter.ofPattern("MM/dd/yy hh:mm:ss a")) + System.lineSeparator());
           }
       } 
       catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
       }
       finally {try {writer.close();} catch (Exception ex) {/*ignore*/}}
   }
   
   // Used by confirm and finish helper
   private String CreateLineItemString(){
       String message = "";
       int i = 1;
       for (String line : orderInfo ){
           message += i + ". " + line + "\n";
           i++;
       }
       return message;
   }
   
// New Helper and Helper Methods
   private void NewHelper() {
       // Reset Global Variables
       numberOfItems = 0;
       currentItem = 1;
       currentBookID = 0;
       currentQuantity = 0;
       currentSubTotal = 0;
       orderInfo =  new ArrayList<String>();
       
       // Reset Text
       label1.setText("Enter number of items in this order:");
       label2.setText("Enter Book ID for Item #1:");
       label3.setText("Enter quanity for Item #1:");
       label4.setText("Item #1 info:");
       label5.setText("Order subtotal for 0 item(s):");
       
       // Reset Textfields
       textField1.setText("");
       textField2.setText("");
       textField3.setText("");
       textField4.setText("");
       textField5.setText("");
       
       // Reset buttons
       buttonProcess.setText("Process Item #" + String.valueOf(currentItem));
       buttonConfirm.setText("Confirm Item #" + String.valueOf(currentItem));
       
       // Reset default disabled/enabled text/buttons
       textField1.setEnabled(true);
       textField2.setEnabled(true);
       textField3.setEnabled(true);
       textField4.setEnabled(false);
       textField5.setEnabled(false);
       buttonProcess.setEnabled(true);
       buttonConfirm.setEnabled(false);
       buttonView.setEnabled(true);
       buttonFinish.setEnabled(false);
       buttonNew.setEnabled(true);
       buttonExit.setEnabled(true);
   }
 
   // Inventory and helper methods below
   private class Inventory {
       int BookID;
	   String Name;
	   double Price;
	   public int getBookID() { return BookID; }
	   public void setBookID(int BookID) { this.BookID = BookID; }
	   public String getName() { return Name; }
	   public void setName(String Name) { this.Name = Name; }
	   public double getPrice() { return Price; }
	   public void setPrice(double Price){ this.Price = Price; }
	   
	   public Inventory(int bookID, String name, double price){
		   BookID = bookID;
		   Name = name;
		   Price = price;
	   }
   }
   
   private void GetInventory() {
       Scanner textfile = null;
       try {
           textfile = new Scanner(new File(InputFile));
       }
       catch (FileNotFoundException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "Inventory Not Found", "Error", JOptionPane.ERROR_MESSAGE);
       }
       
       int bookid = 0;
       String name = null;
       double price = 0;
       while (textfile.hasNext()){
           try{
           // Scan in the entire line and split it by comma and space
           String[] input = textfile.nextLine().split(",\\s");
           bookid = Integer.parseInt(input[0]);
           name = input[1];
           price = Double.parseDouble(input[2]);
           myInventory.add(new Inventory(bookid, name, price));
           }
           catch (ArrayIndexOutOfBoundsException e){
               e.printStackTrace();
               JOptionPane.showMessageDialog(null, "Incorrectly Formatted Inventory", "Error", JOptionPane.ERROR_MESSAGE);
           }
       }
   }
   
   private String GetBookByID() {     
       for (int i = 0; i< myInventory.size(); i++){
           if (myInventory.get(i).getBookID() == currentBookID){
               Inventory book = myInventory.get(i);
               int discount = GetDiscount(book.getPrice());
               String info = book.getBookID() + " " + book.getName() + " $" 
                           + df.format(book.getPrice()) + " " + currentQuantity + " " 
                           + discount + "% $" + df.format(GetSubTotal(discount, book.getPrice()));
               return info;
           }
       }
       return "";
   }
   
   private double GetSubTotal(int discount, double price) {
       double currentBookSubtotal = (price * currentQuantity) - (currentQuantity * price * discount/100.0);
       currentSubTotal += currentBookSubtotal;
       return currentBookSubtotal;
   }

    private int GetDiscount(double price) {
        if (currentQuantity >= 15)
            return 20;
        if (currentQuantity >= 10)
            return 15;
        if (currentQuantity >= 5)
            return 10;
        return 0;
    }

    private boolean IsBookInInventory(){
        for (int i = 0; i< myInventory.size(); i++){
            if (myInventory.get(i).getBookID() == currentBookID){
                return true;
            }
        }
        return false;
   }
   
   public static void main(String[] args) {
		MyStore myStore = new MyStore();
		myStore.GetInventory();
		myStore.ActivateButtons();
	}
}