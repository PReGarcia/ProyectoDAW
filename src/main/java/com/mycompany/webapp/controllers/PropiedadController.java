package com.mycompany.webapp.controllers;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

@WebServlet(name = "PropiedadController", urlPatterns = { "/propiedades", "/propiedad/*" })
public class PropiedadController extends HttpServlet {

    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException{
        String vista;
        String accion = "/propiedades";
        if (request.getServletPath().equals("/propiedad")) {
            if (request.getPathInfo() != null) {
                accion = request.getPathInfo();
            } else {
                accion = "error";
            }
        }
        switch (accion) {
            case "/propiedades":
                vista = "propiedades";
                break;

            case "/nuevo":
                vista = "propiedadForm";
                break;

            default:
                vista = "error";
                break;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/propiedad/" + vista + ".jsp");
        rd.forward(request, response);
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException{

    }
        
}
