/*
Greg Hamelin
Final Project
CIT-215
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionListener;

public class TestDBAccess extends JFrame
{


    private static final int WIDTH=800;
    private static final int HEIGHT=600;

    // Declare JFrame components:
	private DefaultListModel<String> dlmTables = new DefaultListModel<String>();
	private JList<String> jlstTables = new JList<String>(dlmTables);

	private DefaultListModel<String> dlmColumns = new DefaultListModel<String>();
	private JList<String> jlstColumns = new JList<String>(dlmColumns);

    private JTextArea jtaOutput, jtaInput;

    private JButton jbutExecute, jbutClear, jbutExit;

    private ExecuteButtonHandler executeHandler;
    private ClearButtonHandler clearHandler;
    private ExitButtonHandler exitHandler;

    private JScrollPane scrollingOutput, scrollingInput;

    private JPanel jpnlTop = new JPanel();
    private JPanel jpnlCenter = new JPanel();
    private JPanel jpnlBottom = new JPanel();

    private String sStringOut = "";

    // Constructor
    public TestDBAccess(String sTitle)
    {

		// Set the title and Size:
        setTitle(sTitle);
        setSize(WIDTH, HEIGHT);

		//add the tables names to the JList
		dlmTables.addElement("EMPMST");
		dlmTables.addElement("EMPADR");

		//set the selected index to the first item in the JList
    	jlstTables.setSelectedIndex(0);
    	jlstTables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//this method returns the table metadata for the selected
		//table when the application launches
    	getColumn(jlstTables.getSelectedValue().toString());

    	//add listener to jlstTables
    	jlstTables.addListSelectionListener( new ListBoxListener() );

        // Make the Output and Input JTextAreas scrollable:
		jtaOutput = new JTextArea(10,1);
		jtaOutput.setEditable(false);
		scrollingOutput = new JScrollPane(jtaOutput);
		jtaInput = new JTextArea(10,1);
		scrollingInput = new JScrollPane(jtaInput);

		// Instantiate and register the Circle button for clicks events:
		jbutExecute = new JButton("Execute");
        executeHandler = new ExecuteButtonHandler();
        jbutExecute.addActionListener(executeHandler);

		// Instantiate and register the Clear button for clicks events:
		jbutClear = new JButton("Clear");
        clearHandler = new ClearButtonHandler();
        jbutClear.addActionListener(clearHandler);

		// Instantiate and register the Exit button for clicks events:
		jbutExit = new JButton("Exit");
		exitHandler = new ExitButtonHandler();
        jbutExit.addActionListener(exitHandler);

        // Assemble the JPanels:
        jpnlTop.setLayout(new GridLayout(1, 2));
        jpnlTop.add(new JScrollPane(jlstTables));
        jpnlTop.add(new JScrollPane(jlstColumns));

        jpnlCenter.setLayout(new GridLayout(2, 1));
        jpnlCenter.add(scrollingInput);
        jpnlCenter.add(scrollingOutput);

        jpnlBottom.setLayout(new GridLayout(1, 3));
        jpnlBottom.add(jbutExecute);
        jpnlBottom.add(jbutClear);
        jpnlBottom.add(jbutExit);

        // Start to add the components to the JFrame:
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        pane.add(jpnlTop, BorderLayout.NORTH);
        pane.add(jpnlCenter, BorderLayout.CENTER);
        pane.add(jpnlBottom, BorderLayout.SOUTH);

        // Show the JFrame and set code to respond to the user clicking on the X:
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

} // end constructor


//listener class to determine which table is clicked
class ListBoxListener implements ListSelectionListener
{
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()== false)
	   	{
			getColumn(jlstTables.getSelectedValue().toString());
   	   	}
	}
}


//method to get metadata for the selected table and
//output to the JList
private  void getColumn (String sStringIn)
{
	try
	{
	   	//instantiate DBAccessObjectV2
		DBAccessObjectV2 dbAccess1 = new DBAccessObjectV2("jdbc:odbc:JavaClassDSN");

		//remove current elements from dlmColumns
		dlmColumns.removeAllElements();

		//add the table metadata to dlmColumns
		String selectedTable = sStringIn;
		ResultSetMetaData rsMetaData = dbAccess1.getResultMetaData(selectedTable);
		for (int iCount = 1; iCount <= rsMetaData.getColumnCount(); iCount++)  {
		dlmColumns.addElement(rsMetaData.getColumnLabel(iCount) + "  (" + rsMetaData.getColumnTypeName(iCount) + ")" + "\n");}

		//close down the object
		dbAccess1.closeDown();

	}
		//catch errors
		catch(Exception error1)
		{
			System.out.println("TestDBAccess Error: ");
			System.out.println(error1.toString());
		}
}

  // Execute Button Event Handler
  class ExecuteButtonHandler implements ActionListener
    {
      public void actionPerformed(ActionEvent e)
      {
			try
			{
			    //Instantiate two objects of the DBAccessObjectV2 by invoking
			    //both the constructors:
				DBAccessObjectV2 dbAccess1 = new DBAccessObjectV2("jdbc:odbc:JavaClassDSN");
				DBAccessObjectV2 dbAccess2 = new DBAccessObjectV2();

				ResultSet rsClient = dbAccess1.getResultSet(jtaInput.getText());
				ResultSetMetaData rsMetaData = dbAccess1.getResultMetaData();

				jtaOutput.setText("");

				if (jtaInput.getText().toLowerCase().contains("delete") || jtaInput.getText().toLowerCase().contains("insert")
				|| jtaInput.getText().toLowerCase().contains("update"))
				{
					dbAccess2.executeSQL(jtaInput.getText());

					for (int iCount = 1; iCount <= rsMetaData.getColumnCount(); iCount++)  {
					jtaOutput.append(rsMetaData.getColumnLabel(iCount) + "\t");}

					jtaOutput.append("\n");

					while (rsClient.next())
					jtaOutput.append("\n" + rsClient.getString(1) + "\t" +
					rsClient.getString(2) + "\t" + rsClient.getString(3));

				}

				else
				{
					for (int iCount = 1; iCount <= rsMetaData.getColumnCount(); iCount++)  {
					jtaOutput.append(rsMetaData.getColumnLabel(iCount) + "\t");}
					jtaOutput.append("\n");

					while (rsClient.next())
					jtaOutput.append("\n" + rsClient.getString(1) + "\t" +
					rsClient.getString(2) + "\t" + rsClient.getString(3));

				}

				//close down the sql connections
				dbAccess1.closeDown();
    			dbAccess2.closeDown();
			}

			//catch errors
			catch (Exception error)
			{

				System.out.println("TestDBAccess Error: ");
				System.out.println(error.toString());
			}

      }  // end actionPerformed

    } // end CircleButtonHandler


    // Exit Button Event Handler
	class ExitButtonHandler implements ActionListener
	{
	      public void actionPerformed(ActionEvent e)
	      {
			System.exit(0);
	      }

    } // end ExitButtonHandler

	class ClearButtonHandler implements ActionListener
	{
	      public void actionPerformed(ActionEvent e)
	      {
			  jtaInput.setText("");
			  jtaOutput.setText("");
	      }
    } // end ClearButtonHandler


 	public static void main(String[] args)
    {
		//Instantiate the JFrame:
		TestDBAccess testDBAccess = new TestDBAccess("Greg Hamelin's DataBase Tool");

    }  // end main

}// end class
