package com.mycompany.webapp.controllers;

import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

@WebServlet(name = "ReservaController", urlPatterns = {"/reserva/*", "/reservas/*"})
public class ReservaController extends HttpServlet{
    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response){
        String accion = "/reservas";
        if(request.getServletPath().equals("/reserva")){
            
        }
    }

}
