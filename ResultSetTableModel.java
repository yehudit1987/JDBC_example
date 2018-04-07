import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;


public class ResultSetTableModel extends AbstractTableModel 
{
	private final Connection connection; 
	private final Statement statement;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;
	
	private boolean connectedToDatabase=false;
	
	public ResultSetTableModel (String url, String userName, String password, String query) throws SQLException
	{
		connection=DriverManager.getConnection(url, userName, password);
		statement=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);//**
		
		connectedToDatabase=true;
		setQuery(query);
	}
	
	@Override 
	public int getColumnCount() throws IllegalStateException
	{
		if(!connectedToDatabase)
			throw new IllegalStateException ("Not Connected to Database");
		
		try
		{
			return metaData.getColumnCount();
		}
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
		
		return 0;
	}
	
	@Override
	public int getRowCount() throws IllegalStateException
	{
		if(!connectedToDatabase)
			throw new IllegalStateException ("Not Connected to Database");
		return numberOfRows;
	}
	
	@Override
	public Object getValueAt ( int row, int column) throws IllegalStateException
	{
		if(!connectedToDatabase)
			throw new IllegalStateException ("Not Connected to Database");
		
		try
		{
			resultSet.absolute(row+1);
			return resultSet.getObject(column+1);
		}
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
		
		return "";
	}
	
	public void setQuery (String query) throws SQLException, IllegalStateException //**explain why throws
	{
		if(!connectedToDatabase)
			throw new IllegalStateException ("Not Connected to Database");
		resultSet=statement.executeQuery(query);
		metaData=resultSet.getMetaData();
		resultSet.last();
		numberOfRows=resultSet.getRow();
		fireTableStructureChanged();
		
	}
	
	  // get name of a particular column in ResultSet
	   public String getColumnName(int column) throws IllegalStateException
	   {    
	      // ensure database connection is available
	      if (!connectedToDatabase) 
	         throw new IllegalStateException("Not Connected to Database");

	      // determine column name
	      try 
	      {
	         return metaData.getColumnName(column + 1);  
	      } 
	      catch (SQLException sqlException) 
	      {
	         sqlException.printStackTrace();
	      } 
	      
	      return ""; // if problems, return empty string for column name
	   } 
	
	 //necessary method
	public void disconnectFromDatabase()
	{
		if (connectedToDatabase)
		{
			try
			{
				resultSet.close();
				statement.close();
				connection.close();
			}
			catch (SQLException sqlException)
			{
				sqlException.printStackTrace();
			}
			finally
			{
				connectedToDatabase=false;
			}
		}
	}
}
