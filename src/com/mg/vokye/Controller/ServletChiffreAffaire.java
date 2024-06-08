package com.mg.vokye.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

import com.google.gson.Gson;
import com.mg.vokye.bdd.Connexion;
import com.mg.vokye.function.Function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletChiffreAffaire extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        // Parse the date strings and convert to java.sql.Date
        String dateString1 = request.getParameter("d1");
        String dateString2 = request.getParameter("d2");
        
        // Parse the dates and convert to java.sql.Date
        java.sql.Date d1 = java.sql.Date.valueOf(dateString1);
        java.sql.Date d2 = java.sql.Date.valueOf(dateString2);

        Connexion co = new Connexion();
        double chiffres_affaire = 0;
        try {
            chiffres_affaire = (new Function()).chiffre_affaire(co.getConnection(), d1, d2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        out.println(gson.toJson(chiffres_affaire));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
