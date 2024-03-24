package dao;

import entity.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class TaskDao {

    private final static String jdbcURL = "jdbc:mysql://localhost:3306/todolist";
    private final static String jdbcUsername = "root";
    private final static String jdbcPassword = ")8X5i5E%crl%Rl@R,tz)";


    public TaskDao() {
    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public List<Task> selectAllTasks() {

        List<Task> tasks = new ArrayList<>();
        try {
            Connection connection = getConnection();
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
            logError(e);
        }
        return tasks;
    }

    public void insertTask(Task task) {
        Connection con = null;
        String query = "INSERT INTO task(name) VALUES(?)";
        try {
            con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, task.getName());
            preparedStatement.executeUpdate();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean deleteTask(int id) {
        Boolean data = Boolean.FALSE;
        Connection con = getConnection();
        try {
            Statement statement = con.createStatement();
            String sql = "DELETE FROM task " +
                    "WHERE id = " + id;
            statement.executeUpdate(sql);
            data = Boolean.TRUE;

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void logError(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
