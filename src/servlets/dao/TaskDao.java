package dao;

import entity.Task;
import service.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class TaskDao {

    public TaskDao() {
    }

    public Task selecTask(int id) {
        Task task = null;
        // Step 1: Establishing a Connection
        try {
            Connection connection = DBUtils.getConnection();;
             PreparedStatement preparedStatement = connection.prepareStatement("select id,name from task where id =?");
            {
                preparedStatement.setInt(1, id);
                System.out.println(preparedStatement);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("name");
                    task = new Task(id, name);
                }
            }
        } catch (SQLException e) {
            Utils.logSQLError(e);
        }
        return task;
    }

    public List<Task> selectAllTasks() {

        List<Task> tasks = new ArrayList<>();
        try {
            Connection connection = DBUtils.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM task");
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                tasks.add(new Task(id, name));
            }
            connection.close();
        } catch (SQLException e) {
            Utils.logSQLError(e);
        } catch (Exception e) {
            Utils.logError(e);
        }
        return tasks;
    }

    public void insertTask(Task task) throws SQLException {
        Connection con = null;
        //String query = "INSERT INTO task(name) VALUES(?)";
        String query = "INSERT INTO task(name) VALUES('" + task + "')";

        try {
            con = DBUtils.getConnection();
            ResultSet rs = con.createStatement().executeQuery(query);
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, task.getName());
            preparedStatement.executeUpdate();
            con.close();
        } catch (SQLException e) {
           Utils.logSQLError(e);
          throw new RuntimeException(e);
        } catch (Exception e) {
            Utils.logError(e);
        }
    }

    public Boolean deleteTask(int id) {
        Boolean data = Boolean.FALSE;
        Connection con = DBUtils.getConnection();
        try {
            Statement statement = con.createStatement();
            String sql = "DELETE FROM task WHERE id = " + id;
            statement.executeUpdate(sql);
            data = Boolean.TRUE;

            con.close();
        }  catch (SQLException e) {
            Utils.logError(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            Utils.logError(e);
        }
        return data;
    }

    public List<Task> sqlInject(int id, Task task) {
        //String query = "UPDATE task set name = " + task.getName() + " WHERE id = " +id;
        //Example: Smith' or '1'='1
        String query = "SELECT * FROM task WHERE name = '" + task.getName() +  "'" ;
        Connection con = DBUtils.getConnection();
        List<Task> tasks = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            boolean result = statement.execute(query);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                int identfier = resultSet.getInt("id");
                String name = resultSet.getString("name");
                tasks.add(new Task(identfier, name));
            }

            con.close();
        }  catch (SQLException e) {
            Utils.logError(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            Utils.logError(e);
        }
        return tasks;
    }

    public List<Task>  updateTask(int id, Task task) {
        Connection con = DBUtils.getConnection();
        List<Task> tasks = new ArrayList<>();
        try {
            //String query = "UPDATE task set name= ? WHERE id = ?";
            String query = "SELECT * FROM task WHERE name = ?" ;
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, task.getName());
            //preparedStatement.setInt(2, id);
            //preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int identifier = resultSet.getInt("id");
                String name = resultSet.getString("name");
                tasks.add(new Task(identifier, name));
            }
            con.close();
        }  catch (SQLException e) {
            Utils.logError(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
          Utils.logError(e);
        }
        return tasks;
    }
}
