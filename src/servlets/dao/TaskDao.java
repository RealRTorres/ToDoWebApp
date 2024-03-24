package dao;

import entity.Task;
import service.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class TaskDao {

    public TaskDao() {
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

    public void insertTask(Task task) {
        Connection con = null;
        String query = "INSERT INTO task(name) VALUES(?)";
        try {
            con = DBUtils.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, task.getName());
            preparedStatement.executeUpdate();
            con.close();
        } catch (SQLException e) {
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
            throw new RuntimeException(e);
        } catch (Exception e) {
            Utils.logError(e);
        }
        return data;
    }


}
