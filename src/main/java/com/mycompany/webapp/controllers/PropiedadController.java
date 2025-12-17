package com.mycompany.webapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.webapp.models.Foto;
import com.mycompany.webapp.models.Propiedad;
import com.mycompany.webapp.models.Usuario;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 200 // 50 MB
)
@WebServlet(name = "PropiedadController", urlPatterns = { "/propiedades", "/propiedad/*" })
public class PropiedadController extends HttpServlet {

    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(PropiedadController.class.getName());


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath(); // "/propiedades" o "/propiedad"
        String accion = request.getPathInfo(); // "/detalle", "/nuevo", etc.

        if ("/propiedades".equals(path) && (accion == null || accion.equals("/"))) {
            listarPublicas(request, response);
            return;
        }

        if (accion == null)
            accion = "/error";

        switch (accion) {
            case "/api/buscar":
                buscarPropiedadesJson(request, response);
                break;
            case "/admin/lista":
                listarAdmin(request, response);
                break;
            case "/mis-propiedades":
                listarMisPropiedades(request, response);
                break;
            case "/nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "/detalle":
                mostrarDetalle(request, response);
                break;
            case "/editar":
                mostrarFormularioEditar(request, response);
                break;
            case "/eliminar":
                eliminarPropiedad(request, response);
                break;
            default:
                forwardToView(request, response, "/WEB-INF/views/error.jsp");
                break;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getPathInfo();

        if ("/guardar".equals(accion)) {
            guardarPropiedad(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }


    private void listarPublicas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Propiedad> todas = em.createNamedQuery("Propiedad.findAll", Propiedad.class).getResultList();
            request.setAttribute("propiedades", todas);
            forwardToView(request, response, "/WEB-INF/views/propiedad/propiedades.jsp");
        } catch (Exception e) {
            manejarError(request, response, "Error cargando propiedades.");
        }
    }

    private void listarAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!esAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }
        try {
            List<Propiedad> todas = em.createNamedQuery("Propiedad.findAll", Propiedad.class).getResultList();
            request.setAttribute("propiedades", todas);
            forwardToView(request, response, "/WEB-INF/views/propiedad/listaPropiedades.jsp");
        } catch (Exception e) {
            manejarError(request, response, "Error al listar propiedades para admin.");
        }
    }

    private void listarMisPropiedades(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            TypedQuery<Propiedad> q = em.createNamedQuery("Propiedad.findByPropietario", Propiedad.class);
            q.setParameter("propietario", u);
            request.setAttribute("propiedades", q.getResultList());
            forwardToView(request, response, "/WEB-INF/views/propiedad/misPropiedades.jsp");
        } catch (Exception e) {
            manejarError(request, response, "Error al cargar tus propiedades.");
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (getUsuarioSesion(request) == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }
        forwardToView(request, response, "/WEB-INF/views/propiedad/propiedadForm.jsp");
    }

    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Propiedad p = em.find(Propiedad.class, id);

            if (p != null) {
                // Cargar imágenes
                TypedQuery<Foto> q = em.createNamedQuery("Foto.findByPropiedad", Foto.class);
                q.setParameter("propiedad", p);

                request.setAttribute("p", p);
                request.setAttribute("imagenes", q.getResultList());
                forwardToView(request, response, "/WEB-INF/views/propiedad/detalle.jsp");
            } else {
                manejarError(request, response, "La propiedad solicitada no existe.");
            }
        } catch (Exception e) {
            manejarError(request, response, "Error al mostrar detalle.");
        }
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            long id = Long.parseLong(request.getParameter("id"));
            Propiedad p = em.find(Propiedad.class, id);

            // Verificar permisos (dueño o admin)
            if (p != null && (esDueño(p, u) || "ADMIN".equals(u.getRol()))) {
                request.setAttribute("p", p);
                forwardToView(request, response, "/WEB-INF/views/propiedad/propiedadForm.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/propiedades");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/propiedades");
        }
    }

    private void eliminarPropiedad(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            long id = Long.parseLong(request.getParameter("id"));
            utx.begin();

            Propiedad p = em.find(Propiedad.class, id);
            if (p != null && (esDueño(p, u) || "ADMIN".equals(u.getRol()))) {
                em.remove(p);
                utx.commit();
            } else {
                utx.rollback();
            }
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            Log.log(Level.SEVERE, "Error al eliminar propiedad", e);
        }

        response.sendRedirect(request.getContextPath() + "/propiedad/mis-propiedades");
    }


    private void guardarPropiedad(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        String idStr = request.getParameter("id");
        boolean esEdicion = (idStr != null && !idStr.isEmpty());

        try {
            utx.begin();
            Propiedad p;

            if (esEdicion) {
                // EDICIÓN
                p = em.find(Propiedad.class, Long.parseLong(idStr));
                if (p == null || (!esDueño(p, u) && !"ADMIN".equals(u.getRol()))) {
                    throw new Exception("No tienes permiso para editar esta propiedad.");
                }
            } else {
                // CREACIÓN
                p = new Propiedad();
                p.setPropietario(u);
            }

            // Actualizar campos comunes
            actualizarCamposPropiedad(p, request);

            if (esEdicion) {
                em.merge(p);
            } else {
                em.persist(p);
                em.flush(); // Necesario para obtener ID y crear carpetas

                // Actualizar rol a PROP si es la primera vez
                if ("USER".equals(u.getRol())) {
                    u.setRol("PROP");
                    em.merge(u);
                    request.getSession().setAttribute("user", u);
                }
            }

            // Guardar Fotos (común para ambos)
            guardarFotos(p, request.getParts());

            utx.commit();
            response.sendRedirect(request.getContextPath() + "/propiedad/mis-propiedades");

        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            Log.log(Level.SEVERE, "Error guardando propiedad", e);
            manejarError(request, response, "Error al guardar la propiedad: " + e.getMessage());
        }
    }

    private void actualizarCamposPropiedad(Propiedad p, HttpServletRequest request) {
        p.setNombre(request.getParameter("nombre"));
        p.setDescripcion(request.getParameter("descripcion"));
        p.setCalle_numero(request.getParameter("calle_numero"));
        p.setCiudad(request.getParameter("ciudad"));
        p.setCodigo_postal(request.getParameter("codigo_postal"));
        p.setPrecio_habitacion(Double.parseDouble(request.getParameter("precio_habitacion")));
        p.setHabitaciones(Integer.parseInt(request.getParameter("habitaciones")));
        p.setBaños(Integer.parseInt(request.getParameter("banos")));
        p.setLatitud(Double.parseDouble(request.getParameter("latitud")));
        p.setLongitud(Double.parseDouble(request.getParameter("longitud")));
    }

    private void guardarFotos(Propiedad p, Collection<Part> partes) {
        String carpetaRelativa = "static/img/propiedades/" + p.getPropiedad_id() + "/";
        String rutaAbsoluta = getServletContext().getRealPath(carpetaRelativa);

        File directorio = new File(rutaAbsoluta);
        if (!directorio.exists())
            directorio.mkdirs();

        for (Part part : partes) {
            String fileName = part.getSubmittedFileName();
            if (fileName != null && !fileName.trim().isEmpty()) {
                try (InputStream input = part.getInputStream()) {

                    fileName = new File(fileName).getName();
                    File archivoDestino = new File(directorio, fileName);
                    Files.copy(input, archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    Foto foto = new Foto(carpetaRelativa + fileName, p);
                    em.persist(foto);

                    if ("portada".equals(part.getName())) {
                        p.setPortada(foto.getUrl());
                        em.merge(p);
                    }
                } catch (Exception e) {
                    Log.log(Level.WARNING, "Error subiendo foto " + fileName, e);
                }
            }
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

    private Usuario getUsuarioSesion(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("user");
    }

    private boolean esAdmin(HttpServletRequest request) {
        Usuario u = getUsuarioSesion(request);
        return u != null && "ADMIN".equals(u.getRol());
    }

    private boolean esDueño(Propiedad p, Usuario u) {
        return p.getPropietario().getUsuario_id() == u.getUsuario_id();
    }

    private void buscarPropiedadesJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("q");
        List<Propiedad> propiedades;

        try {
            if (query == null || query.trim().isEmpty()) {
                propiedades = em.createNamedQuery("Propiedad.findAll", Propiedad.class).getResultList();
            } else {
                String jpql = "SELECT p FROM Propiedad p WHERE LOWER(p.nombre) LIKE :q OR LOWER(p.ciudad) LIKE :q";
                propiedades = em.createQuery(jpql, Propiedad.class)
                        .setParameter("q", "%" + query.toLowerCase() + "%")
                        .getResultList();
            }

            // Construcción manual del JSON
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < propiedades.size(); i++) {
                Propiedad p = propiedades.get(i);

                String fotoUrl = p.getPortada(); 

                json.append("{")
                        .append("\"id\":").append(p.getPropiedad_id()).append(",")
                        .append("\"nombre\":\"").append(p.getNombre().replace("\"", "\\\"")).append("\",")
                        .append("\"ciudad\":\"").append(p.getCiudad().replace("\"", "\\\"")).append("\",")
                        .append("\"precio\":").append(p.getPrecio_habitacion()).append(",")
                        .append("\"foto\":\"").append(fotoUrl).append("\"")
                        .append("}");

                if (i < propiedades.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("[]"); // En caso de error devolvemos array vacío
        }
    }
}