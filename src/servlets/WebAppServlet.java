import entity.Task;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.List;

import dao.TaskDao;
import service.Utils;

@WebServlet("/")
public class WebAppServlet extends HttpServlet {

    private TaskDao dao;

    public void init() {
        dao = new TaskDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (request.getParameter("update") != null) {
                updateTask(request, response);
            } else if (request.getParameter("sqlinject") != null) {
               sqlinject(request, response);
            } else if (request.getParameter("add") != null) {
                insertTask(request, response);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            listTask(request, response);
        } catch (SQLException | ServletException | IOException e ) {
           // throw new RuntimeException(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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
                case "/edit":
                    editTask(request, response);
                    break;
                case "/delete":
                    deleteTask(request, response);
                    break;
                case "/update":
                    updateTask(request, response);
                    break;
                default:
                    listTask(request, response);
                    break;
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ServletException | IOException  e) {
            //throw new ServletException(ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }


    private void sqlinject(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        Task task = new Task(id, name);
        List<Task> injected = dao.sqlInject(id,task);
        request.setAttribute("injected", injected);
    }

    private void updateTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        Task task = new Task(id, name);
        List<Task> result = dao.updateTask(id,task);
        request.setAttribute("injected", result);
    }

    private void editTask(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        Task task = dao.selecTask(id);
        request.setAttribute("task", task);
        RequestDispatcher dispatcher = request.getRequestDispatcher("edittask.jsp");
        dispatcher.forward(request, response);
    }

    public void listTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Task> tasklist = dao.selectAllTasks();
        request.setAttribute("tasklist", tasklist);
        RequestDispatcher dispatcher = request.getRequestDispatcher("tasklist.jsp");
        dispatcher.forward(request, response);
    }

    public void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("task-form.jsp");
        dispatcher.forward(request, response);
    }

    public void insertTask(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String name = request.getParameter("name");
        Task newUser = new Task(name);
        dao.insertTask(newUser);
        response.sendRedirect("added");
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Boolean isDeleted = dao.deleteTask(id);
        if (isDeleted) {
            Utils.logInfo("id:" + id + " was deleted.");
        }
        response.sendRedirect("list");
    }
}