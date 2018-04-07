import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.table.*;


public class DisplayQueryResults extends JFrame
{
	
	private static final String DATABASE_URL="jdbc:derby:C:\\Users\\yehudit\\Documents\\מדעי המחשב - האוניברסיטה הפתוחה\\סדנה בתכנות מתקדם בשפת ג'אווה 20503\\JDBC Lecture\\students";
	private static final String USERNAME="kerido";
	private static final String PASSWORD="kerido";
	
	private static final String DEFAULT_QUERY="SELECT * FROM students";
	private static ResultSetTableModel tableModel;
	
	public static void main (String [] args)
	{
		try
		{
			tableModel=new ResultSetTableModel (DATABASE_URL, USERNAME, PASSWORD, DEFAULT_QUERY);
			final JTextArea queryArea=new JTextArea (DEFAULT_QUERY, 3, 100);
			queryArea.setWrapStyleWord(true);//**explain next both
			queryArea.setLineWrap(true);
			JScrollPane scrollpane=new JScrollPane (queryArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JButton submitButton=new JButton("submit Query");
			
			Box boxNorth=Box.createHorizontalBox();
			boxNorth.add(scrollpane);
			boxNorth.add(submitButton);
		
			JTable resultTable=new JTable (tableModel);
			JLabel filterLabel=new JLabel("Filter");
			final JTextField filterText = new JTextField();
        	JButton filterButton = new JButton("Apply Filter");
        	Box boxSouth = Box.createHorizontalBox();
        
        	boxSouth.add(filterLabel);
        	boxSouth.add(filterText);
        	boxSouth.add(filterButton);
        
        	// place GUI components on JFrame's content pane
        	JFrame window = new JFrame("Displaying Query Results");
        	window.add(boxNorth, BorderLayout.NORTH);
        	window.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        	window.add(boxSouth, BorderLayout.SOUTH);
        
        	// create event listener for submitButton
        	submitButton.addActionListener(        
        			new ActionListener() 
        			{
        				public void actionPerformed(ActionEvent event)
        				{
        					// perform a new query
        					try 
        					{
        						tableModel.setQuery(queryArea.getText());
        					}
        					catch (SQLException sqlException) 
        					{
        						JOptionPane.showMessageDialog(null, 
        								sqlException.getMessage(), "Database error", 
        								JOptionPane.ERROR_MESSAGE);
                    
        						// try to recover from invalid user query 
        						// by executing default query
        						try 
        						{
        							tableModel.setQuery(DEFAULT_QUERY);
        							queryArea.setText(DEFAULT_QUERY);
        						} 
        						catch (SQLException sqlException2) 
        						{
        							JOptionPane.showMessageDialog(null, 
        									sqlException2.getMessage(), "Database error", 
        									JOptionPane.ERROR_MESSAGE);
        
        							// ensure database connection is closed
        							tableModel.disconnectFromDatabase();
        
        							System.exit(1); // terminate application
        						}                 
        					} 
        				} 
        			}         
        			); // end call to addActionListener
        
        	final TableRowSorter<TableModel> sorter = 
        			new TableRowSorter<TableModel>(tableModel);
        	resultTable.setRowSorter(sorter);
        
        	// create listener for filterButton
        	filterButton.addActionListener(           
        			new ActionListener() 
        			{
        				// pass filter text to listener
        				public void actionPerformed(ActionEvent e) 
        				{
        					String text = filterText.getText();

        					if (text.length() == 0)
        						sorter.setRowFilter(null);
        					else
        					{
        						try
        						{
        							sorter.setRowFilter(
        									RowFilter.regexFilter(text));
        						} 
        						catch (PatternSyntaxException pse) 
        						{
        							JOptionPane.showMessageDialog(null,
        									"Bad regex pattern", "Bad regex pattern",
        									JOptionPane.ERROR_MESSAGE);
        						}
        					} 
        				} 
        			} 
        			); // end call to addActionLister

        	// dispose of window when user quits application (this overrides
        	// the default of HIDE_ON_CLOSE)
        	window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        	window.setSize(500, 250); 
        	window.setVisible(true); 
        
        	// ensure database is closed when user quits application
        	window.addWindowListener(
        			new WindowAdapter() 
        			{
        				public void windowClosed(WindowEvent event)
        				{
        					tableModel.disconnectFromDatabase();
        					System.exit(0);
        				} 
        			} 
        			); 
		} 
		catch (SQLException sqlException) 
		{
			JOptionPane.showMessageDialog(null, sqlException.getMessage(), 
					"Database error", JOptionPane.ERROR_MESSAGE);
			tableModel.disconnectFromDatabase();
			System.exit(1); // terminate application
		}     
  
	} 
		
	}

