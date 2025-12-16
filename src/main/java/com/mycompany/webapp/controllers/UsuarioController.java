package com.mycompany.webapp.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.webapp.models.Usuario;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;

@WebServlet(name = "UsuarioController", urlPatterns = { "/usuarios", "/usuario/*" })
public class UsuarioController extends HttpServlet {

    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vista = "/WEB-INF/views/usuario/";
        String accion = null;
        if (request.getServletPath().equals("/usuario")) {
            if (request.getPathInfo() != null) {
                accion = request.getPathInfo();
            } else {
                accion = "error";
            }
        }
        switch (accion) {
            // Dentro del switch(accion)
            case "/admin/usuarios":
                if (!esAdmin(request)) {
                    response.sendRedirect(request.getContextPath() + "/usuario/entrar");
                    return;
                }

                List<Usuario> listaUsuarios = em.createNamedQuery("Usuario.findAll", Usuario.class).getResultList();

                request.setAttribute("usuarios", listaUsuarios);
                vista += "listaUsuarios.jsp"; // Crearemos esta carpeta y vista
                break;

            case "/admin/editar":
                if (!esAdmin(request)) {
                    response.sendRedirect(request.getContextPath() + "/usuario/entrar");
                    return;
                }
                
                try {
                    long id = Long.parseLong(request.getParameter("id"));
                    Usuario uEdit = em.find(Usuario.class, id);
                    if (uEdit != null) {
                        request.setAttribute("u", uEdit);
                        vista = "admin/usuarioFormAdmin.jsp"; // Crearemos esta vista nueva
                    } else {
                        response.sendRedirect(request.getContextPath() + "/usuario/admin/usuarios");
                        return;
                    }
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/usuario/admin/usuarios");
                    return;
                }
                break;

            case "/admin/eliminar":
                if (!esAdmin(request))
                    return;

                // Lógica para borrar usuario
                try {
                    long id = Long.parseLong(request.getParameter("id"));
                    utx.begin();
                    Usuario u = em.find(Usuario.class, id);
                    if (u != null)
                        em.remove(u);
                    utx.commit();
                } catch (Exception e) {
                    // rollback...
                }
                response.sendRedirect(request.getContextPath() + "/usuario/admin/usuarios");
                return;
            case "/nuevo": {
                vista += "usuarioForm.jsp";
                break;
            }
            case "/entrar": {
                vista += "usuarioLogin.jsp";
                break;
            }
            case "/salir": {
                logout(request);
                response.sendRedirect(request.getContextPath() + "/propiedades");
                return;
            }
            default: {
                vista += "error.jsp";
            }
        }
        request.setAttribute("view", vista);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getPathInfo();
        if (accion.equals("/guardar")) {
            try {
                nuevoUsuario(request);
                response.sendRedirect("http://localhost:8080/WebApp/");
            } catch (Exception e) {
                request.setAttribute("msg", "Error: datos no válidos");
                request.setAttribute("view", "/WEB-INF/views/error.jsp");
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
                rd.forward(request, response);
            }
        } else if (accion.equals("/validar")) {
            Usuario u = null;
            try {
                u = loginUser(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (crearSesionUsuario(request, u)) {
                response.sendRedirect(request.getContextPath() + "/propiedades");
            } else {
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
                rd.forward(request, response);
            }
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/error.jsp");
            rd.forward(request, response);
        }
    }

    private Usuario loginUser(HttpServletRequest request) throws Exception {

        Usuario user = null;

        String email = request.getParameter("email");
        String pwd = request.getParameter("contra");
        if (email.isEmpty() || pwd.isEmpty()) {
            throw new Exception("Datos no válidos");
        }

        try {
            user = findByCredentials(email, encriptPassword(pwd));
        } catch (Exception e) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }

        if (user == null) {
            throw new Exception("Usuario no encontrado");
        } else {
            return user;
        }
    }

    private Usuario nuevoUsuario(HttpServletRequest request) {
        String name = request.getParameter("nombre");
        String apellido = request.getParameter("apellidos");
        String email = request.getParameter("email");
        String contra = request.getParameter("contra");

        String pass_digest = encriptPassword(contra);

        Usuario u = new Usuario(name, apellido, email, pass_digest);
        try {
            utx.begin();
            em.persist(u);
            Log.log(Level.INFO, "New User saved");
            utx.commit();
            return u;
        } catch (Exception e) {
            Log.log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

    private Usuario findByCredentials(String email, String pwd) {
        Usuario user = null;
        try {
            List<Usuario> users;
            TypedQuery<Usuario> q1 = em.createNamedQuery("Usuario.findByCredentials", Usuario.class);
            q1.setParameter("email", email);
            q1.setParameter("pwd", pwd);
            users = q1.getResultList();

            if (!users.isEmpty()) {
                user = users.get(0);
            }
        } catch (Exception e) {
            Log.log(Level.WARNING, "EXCEPTION: ", e);
        }

        return user;
    }

    private String encriptPassword(String pwd) {

        String pass_digest = null;

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(pwd.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            pass_digest = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Log.log(Level.SEVERE, "EXCEPTION: ", ex);
        }

        return pass_digest;

    }

    private void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
    }

    private boolean crearSesionUsuario(HttpServletRequest request, Usuario u) {
        HttpSession session = request.getSession();
        String msg;
        String style;
        if (u != null) {
            session.setAttribute("user", u);
            session.setAttribute("rol", u.getRol());
            return true;
        } else {
            msg = "ERROR: Login incorrecto";
            style = "danger";
            session.removeAttribute("user");
            session.removeAttribute("role");
            request.setAttribute("msg", msg);
            request.setAttribute("style", style);
            request.setAttribute("view", "/WEB-INF/views/usuario/usuarioLogin.jsp");
            return false;
        }
    }

    private boolean esAdmin(HttpServletRequest request) {
        Usuario u = (Usuario) request.getSession().getAttribute("user");
        return u != null && "ADMIN".equals(u.getRol());
    }
}
