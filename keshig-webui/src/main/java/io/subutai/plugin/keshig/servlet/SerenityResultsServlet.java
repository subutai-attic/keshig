package io.subutai.plugin.keshig.servlet;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SerenityResultsServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println(req.getRequestURI());

        String homeDir = System.getProperty("user.home");

        String[] tokens = req.getRequestURI().split("/");
        if (tokens.length == 1) {

        }
        System.out.println("*************************************************SERENITY*************************************************");
        RequestDispatcher view = req.getRequestDispatcher(String.format("%s/serenity/%s/index.html", homeDir, tokens[1]));
        System.out.println("*************************************************SERENITY*************************************************");
        view.forward(req, resp);
    }

}
