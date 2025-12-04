package com.mycompany.webapp.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.webapp.models.Usuario;
import com.mycompany.webapp.repositories.UsuarioRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UsuarioController", urlPatterns = { "/usuarios", "/usuario/*" })
public class UsuarioController extends HttpServlet {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vista;
        String accion = null;
        if (request.getServletPath().equals("/usuario")) {
            if (request.getPathInfo() != null) {
                accion = request.getPathInfo();
            } else {
                accion = "error";
            }
        }
        switch (accion) {
            case "/nuevo": {
                vista = "usuarioForm";
                break;
            }
            case "/entrar": {
                vista = "usuarioLogin";
                break;
            }
            default: {
                vista = "error";
            }
        }
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/usuario/" + vista + ".jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getPathInfo();
        if (accion.equals("/guardar")) {
            String name = request.getParameter("nombre");
            String apellido = request.getParameter("apellidos");
            String email = request.getParameter("email");
            String contra = request.getParameter("contra");
            try {
                if (name.isEmpty() || email.isEmpty() || contra.isEmpty() || apellido.isEmpty()) {
                    throw new NullPointerException();
                }
                Usuario u = new Usuario(name, apellido, email, contra);
                save(u);
                response.sendRedirect("http://localhost:8080/WebApp/");
            } catch (Exception e) {
                request.setAttribute("msg", "Error: datos no válidos");
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/error.jsp");
                rd.forward(request, response);
            }
        } else if (accion.equals("/validar")) {
            Usuario u = null;
            try {
                u = loginUser(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpSession session = request.getSession();
            String msg;
            String style;
            String newRequest;
            if (u != null) {
                session.setAttribute("user", u);
                session.setAttribute("role", u.getRol());
                newRequest = "/WebApp";
            } else {
                msg = "ERROR: Login incorrecto";
                style = "danger";
                session.removeAttribute("user");
                session.removeAttribute("role");
                newRequest = "/WebApp/usuario/entrar";
                request.setAttribute("msg", msg);
                request.setAttribute("style", style);
            }
            RequestDispatcher rd = request.getRequestDispatcher(newRequest);
            rd.forward(request, response);
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/error.jsp");
            rd.forward(request, response);
        }
    }

    private Usuario loginUser(HttpServletRequest request) throws Exception {

        Usuario user = null;

        String email = request.getParameter("email");
        String pwd = request.getParameter("pwd");
        if (email.isEmpty() || pwd.isEmpty()) {
                throw new Exception("Datos no válidos");
            }

        try
        {
            user = usuarioRepository.findByCredentials(email, pwd);
        } catch (Exception e) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }

        if(user == null){
            throw new Exception("Usuario no encontrado");
        }
        else{
            return user;
        }
    }

    private void save(Usuario u){
        usuarioRepository.nuevoUsuario(u);
    }
}
