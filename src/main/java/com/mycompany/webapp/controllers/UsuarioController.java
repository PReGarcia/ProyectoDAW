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

        String accion = request.getPathInfo();
        if (accion == null)
            accion = "/error";

        switch (accion) {
            case "/admin/usuarios":
                listarUsuariosAdmin(request, response);
                break;
            case "/admin/eliminar":
                eliminarUsuario(request, response);
                break;
            case "/nuevo":
                forwardToView(request, response, "/WEB-INF/views/usuario/usuarioForm.jsp");
                break;
            case "/entrar":
                forwardToView(request, response, "/WEB-INF/views/usuario/usuarioLogin.jsp");
                break;
            case "/salir":
                cerrarSesion(request, response);
                break;
            default:
                forwardToView(request, response, "/WEB-INF/views/error.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getPathInfo();
        if (accion == null)
            accion = "";

        switch (accion) {
            case "/guardar":
                procesarRegistro(request, response);
                break;
            case "/validar":
                procesarLogin(request, response);
                break;
            default:
                forwardToView(request, response, "/WEB-INF/views/error.jsp");
                break;
        }
    }


    private void listarUsuariosAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!esAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            List<Usuario> listaUsuarios = em.createNamedQuery("Usuario.findAll", Usuario.class).getResultList();
            request.setAttribute("usuarios", listaUsuarios);
            forwardToView(request, response, "/WEB-INF/views/usuario/listaUsuarios.jsp");
        } catch (Exception e) {
            manejarError(request, response, "Error al listar usuarios: " + e.getMessage());
        }
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!esAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            long id = Long.parseLong(request.getParameter("id"));
            utx.begin();
            Usuario u = em.find(Usuario.class, id);
            if (u != null) {
                em.remove(u);
            }
            utx.commit();
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error eliminando usuario", e);
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
        }

        response.sendRedirect(request.getContextPath() + "/usuario/admin/usuarios");
    }

    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/propiedades");
    }

    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            utx.begin();

            String nombre = request.getParameter("nombre");
            String apellidos = request.getParameter("apellidos");
            String email = request.getParameter("email");
            String contra = request.getParameter("contra");
            String rol = request.getParameter("rol"); // Por si un admin crea usuarios

            Usuario u = new Usuario(nombre, apellidos, email, encriptPassword(contra));

            if (rol != null && !rol.isEmpty() && esAdmin(request)) {
                u.setRol(rol);
            }

            em.persist(u);
            utx.commit();

            response.sendRedirect(request.getContextPath() + "/usuario/entrar");

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error en registro", e);
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            manejarError(request, response, "Error al registrar usuario. Verifique los datos.");
        }
    }

    private void procesarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String contra = request.getParameter("contra");

        try {
            TypedQuery<Usuario> q = em.createNamedQuery("Usuario.findByCredentials", Usuario.class);
            q.setParameter("email", email);
            q.setParameter("pwd", encriptPassword(contra));

            List<Usuario> resultados = q.getResultList();

            if (!resultados.isEmpty()) {
                Usuario u = resultados.get(0);
                HttpSession session = request.getSession();
                session.setAttribute("user", u);
                session.setAttribute("rol", u.getRol());
                response.sendRedirect(request.getContextPath() + "/propiedades");
            } else {
                request.setAttribute("msg", "Usuario o contraseña incorrectos");
                request.setAttribute("style", "error"); 
                forwardToView(request, response, "/WEB-INF/views/usuario/usuarioLogin.jsp");
            }

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error en login", e);
            manejarError(request, response, "Error interno en el servidor.");
        }
    }

    private void forwardToView(HttpServletRequest request, HttpServletResponse response, String viewPath)
            throws ServletException, IOException {
        request.setAttribute("view", viewPath); 
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
        rd.forward(request, response);
    }

    private void manejarError(HttpServletRequest request, HttpServletResponse response, String msg)
            throws ServletException, IOException {
        request.setAttribute("msg", msg);
        forwardToView(request, response, "/WEB-INF/views/error.jsp");
    }

    private boolean esAdmin(HttpServletRequest request) {
        Usuario u = (Usuario) request.getSession().getAttribute("user");
        return u != null && "ADMIN".equals(u.getRol());
    }

    private String encriptPassword(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pwd.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Log.log(Level.SEVERE, "Error de encriptación", ex);
            return null;
        }
    }
}