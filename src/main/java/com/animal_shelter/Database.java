package com.animal_shelter;

import java.sql.*;
import java.util.ArrayList;


public class Database
{
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    // Constructor
    public Database() {}

    private void connect()
    {
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;" +
                    "trustServerCertificate=true;" +
                    "database=AnimalShelter;" +
                    "user=sa;" +
                    "password=@ne2Three");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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
