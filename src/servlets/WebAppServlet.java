import entity.Task;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.TaskDao;

@WebServlet("/")
public class WebAppServlet extends HttpServlet {

    private TaskDao dao;

    public void init() {
        dao = new TaskDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            insertTask(request, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        try {
            switch (action) {
                case "/new":
                    showNewForm(request, response);
                    break;
                case "/delete":
                    deleteTask(request, response);
                    break;
                default:
                    listTask(request, response);
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private void listTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Task> tasklist = dao.selectAllTasks();
        request.setAttribute("tasklist", tasklist);
        RequestDispatcher dispatcher = request.getRequestDispatcher("tasklist.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("task-form.jsp");
        dispatcher.forward(request, response);
    }

    private void insertTask(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String name = request.getParameter("name");
        Task newUser = new Task(name);
        dao.insertTask(newUser);
        response.sendRedirect("/todowebapp/added");
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        dao.deleteTask(id);
        System.out.println("id=" + id);
        response.sendRedirect("list");
    }
}