package com.animal_shelter;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;


public class Database
{
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    // Constructor
    public Database() {}

    /**
     * Connects to the database
     */
    private void connect()
    {
        try
        {
            // Load the properties file
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/database.properties"));
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Connect to the database
            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;"+
                    "trustServerCertificate=true;"+
                    "database="+properties.getProperty("database")+";"+
                    "user="+properties.getProperty("username")+";"+
                    "password="+properties.getProperty("password"));
        }
        catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Disconnects from the database
     */
    private void disconnect()
    {
        try
        {
            if (resultSet != null)          resultSet.close();
            if (preparedStatement != null)  preparedStatement.close();
            if (connection != null)         connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns an ArrayList of Strings containing the values of each column in the resultSet
     * @param query SQL query
     * @return ArrayList of Strings
     */
    public ArrayList<String> returns(String query)
    {
        ArrayList<String> list = new ArrayList<>();
        connect();
        try
        {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                ArrayList<String> row = new ArrayList<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                {
                    row.add(String.valueOf(resultSet.getObject(i)));
                }
                list.add(String.join(" ",row));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            disconnect();
        }
        return list;
    }

    /**
     * Executes an SQL query
     * @param query SQL query
     */
    public void executes(String query)
    {
        connect();
        try
        {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            disconnect();
        }
    }

}
