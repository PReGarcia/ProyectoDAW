package com.mycompany.webapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
@WebServlet(name = "PropiedadController", urlPatterns = { "/propiedades", "/propiedad/*" })
public class PropiedadController extends HttpServlet {

    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vista = "/WEB-INF/views/propiedad/";
        String accion = "/propiedades";
        if (request.getServletPath().equals("/propiedad")) {
            if (request.getPathInfo() != null) {
                accion = request.getPathInfo();
            } else {
                accion = "/error";
            }
        }
        switch (accion) {

            // En doGet
            case "/admin/lista":
                if (!esAdmin(request)) {
                    response.sendRedirect("...");
                    return;
                }

                List<Propiedad> todas = em.createNamedQuery("Propiedad.findAll", Propiedad.class).getResultList();
                request.setAttribute("propiedades", todas);
                vista += "listaPropiedades.jsp";
                break;
            case "/mis-propiedades":
                Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("user");

                if (usuarioLogueado != null) {
                    request.setAttribute("propiedades", findByPropietario(usuarioLogueado));
                    vista += "misPropiedades.jsp";
                } else {
                    response.sendRedirect(request.getContextPath() + "/usuario/entrar");
                    return;
                }
                break;
            case "/propiedades":
                request.setAttribute("propiedades", findAll());
                vista += "propiedades.jsp";
                break;

            case "/nuevo":
                if (request.getSession().getAttribute("user") != null) {
                    vista += "propiedadForm.jsp";
                } else {
                    response.sendRedirect("/WebApp/usuario/entrar");
                    return;
                }
                break;

            case "/detalle":
                long id = Long.parseLong(request.getParameter("id"));

                Propiedad p = em.find(Propiedad.class, id);
                request.setAttribute("imagenes", findByPropiedad(em, p));
                if (p != null) {
                    request.setAttribute("p", p);
                    vista += "detalle.jsp";
                } else {
                    request.setAttribute("msg", "La propiedad no existe");
                    vista += "error.jsp";
                }
                break;

            case "/editar":
                long idEditar = Long.parseLong(request.getParameter("id"));
                Propiedad pEditar = em.find(Propiedad.class, idEditar);

                Usuario uSesion = (Usuario) request.getSession().getAttribute("user");
                if (pEditar != null && uSesion != null
                        && pEditar.getPropietario().getUsuario_id() == uSesion.getUsuario_id()) {
                    request.setAttribute("p", pEditar); // Pasamos la propiedad para rellenar el formulario
                    vista += "propiedadForm.jsp";
                } else {
                    response.sendRedirect(request.getContextPath() + "/propiedades");
                    return;
                }
                break;

            case "/eliminar":
                try {
                    long idEliminar = Long.parseLong(request.getParameter("id"));
                    utx.begin();
                    Propiedad pEliminar = em.find(Propiedad.class, idEliminar);

                    Usuario uActual = (Usuario) request.getSession().getAttribute("user");
                    boolean esDueño = pEliminar.getPropietario().getUsuario_id() == uActual.getUsuario_id();
                    boolean esAdmin = "ADMIN".equals(uActual.getRol());

                    if (pEliminar != null && (esDueño || esAdmin)) {
                        em.remove(pEliminar);
                        utx.commit();
                    } else {
                        utx.rollback();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        utx.rollback();
                    } catch (Exception ex) {
                    }
                }
                response.sendRedirect(request.getContextPath() + "/propiedad/mis-propiedades");
                return;

            default:
                vista += "error.jsp";
                break;
        }
        request.setAttribute("view", vista);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
        rd.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getPathInfo();
        if (accion.equals("/guardar")) {
            String idStr = request.getParameter("id");
            try {
                if (idStr != null && !idStr.isEmpty()) {
                    // Si hay ID, es una edición
                    procesarEdicion(request, Long.parseLong(idStr));
                } else {
                    // Si no hay ID, es una creación nueva
                    procesarCreacion(request);
                }
                // Si todo sale bien, redirigimos
                response.sendRedirect(request.getContextPath() + "/propiedad/mis-propiedades");

            } catch (Exception e) {
                // Manejo centralizado de errores
                Log.log(Level.SEVERE, "Error al guardar propiedad", e);
                request.setAttribute("msg", "Error al procesar la propiedad: " + e.getMessage());
                request.setAttribute("view", "/WEB-INF/views/error.jsp");
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
                rd.forward(request, response);
            }
        } else {
            // Manejo de otras acciones POST si las hubiera
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

    }

    public List<Propiedad> findAll() {
        TypedQuery<Propiedad> q1 = em.createNamedQuery("Propiedad.findAll", Propiedad.class);
        return q1.getResultList();
    }

    public Propiedad nuevaPropiedad(HttpServletRequest request) {
        String nombre = request.getParameter("nombre");
        String calle_numero = request.getParameter("calle_numero");
        String ciudad = request.getParameter("ciudad");
        String codigo_postal = request.getParameter("codigo_postal");
        double precio_habitacion = Double.parseDouble(request.getParameter("precio_habitacion"));
        int habitaciones = Integer.parseInt(request.getParameter("habitaciones"));
        int baños = Integer.parseInt(request.getParameter("banos"));
        double latitud = Double.parseDouble(request.getParameter("latitud"));
        double longitud = Double.parseDouble(request.getParameter("longitud"));
        String descripcion = request.getParameter("descripcion");
        Usuario propietario = (Usuario) request.getSession().getAttribute("user");

        Propiedad p = new Propiedad(nombre, calle_numero, ciudad, codigo_postal,
                precio_habitacion, habitaciones, baños, latitud, longitud,
                descripcion, propietario);
        return p;
    }

    private String guardarFotos(Propiedad p, Collection<Part> fotosForm) {
        String url_perfil = "";
        String carpetaRelativa = "static/img/propiedades/";
        String rutaAbsoluta = getServletContext().getRealPath(carpetaRelativa);
        rutaAbsoluta += File.separator + p.getPropiedad_id() + File.separator;
        carpetaRelativa += p.getPropiedad_id() + "/";

        // Devuelve la ruta absoluta
        File directorio = new File(rutaAbsoluta);
        // Crea el directorio si no existe
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        List<Foto> listaFotos = new ArrayList<>();

        Foto fotoNueva;

        for (Part part : fotosForm) {
            String fileName = part.getSubmittedFileName();

            if (fileName != null && !fileName.trim().isEmpty()) {

                fileName = new File(fileName).getName();

                try (InputStream input = part.getInputStream()) {
                    File archivoDestino = new File(directorio, fileName);
                    Files.copy(input, archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace(); // Para ver si falla aquí en el log del servidor
                }

                fotoNueva = new Foto(carpetaRelativa + fileName, p);

                listaFotos.add(fotoNueva);
                if (part.getName().equals("portada")) {
                    url_perfil = fotoNueva.getUrl();
                    p.setPortada(url_perfil);
                }
            }
        }

        if (!listaFotos.isEmpty()) {
            for (Foto foto : listaFotos) {
                em.persist(foto);
            }
        }

        return url_perfil;
    }

    public Propiedad getById(Long id) {
        TypedQuery<Propiedad> query = em.createNamedQuery("Propiedad.getById", Propiedad.class);
        query.setParameter("propiedad_id", id);

        return query.getSingleResult();
    }

    public List<Foto> findByPropiedad(EntityManager em, Propiedad p) {
        TypedQuery<Foto> query = em.createNamedQuery("Foto.findByPropiedad", Foto.class);
        query.setParameter("propiedad", p);

        return query.getResultList();

    }

    public List<Propiedad> findByPropietario(Usuario u) {
        TypedQuery<Propiedad> q = em.createNamedQuery("Propiedad.findByPropietario", Propiedad.class);
        q.setParameter("propietario", u);
        return q.getResultList();
    }

    private void procesarCreacion(HttpServletRequest request) throws Exception {
        try {
            utx.begin();

            Propiedad p = nuevaPropiedad(request);

            em.persist(p);
            em.flush();

            guardarFotos(p, request.getParts());
            verificarYActualizarRol(p.getPropietario(), request);

            utx.commit();
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                Log.log(Level.SEVERE, "Rollback failed", ex);
            }
            throw e;
        }
    }

    private void procesarEdicion(HttpServletRequest request, Long id) throws Exception {
        try {
            utx.begin();

            Propiedad p = em.find(Propiedad.class, id);
            if (p == null) {
                throw new Exception("La propiedad a editar no existe.");
            }

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

            em.merge(p);
            guardarFotos(p, request.getParts());

            utx.commit();
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                Log.log(Level.SEVERE, "Rollback failed", ex);
            }
            throw e;
        }
    }

    private void verificarYActualizarRol(Usuario propietario, HttpServletRequest request) {
        if ("USER".equals(propietario.getRol())) {
            propietario.setRol("PROP");
            em.merge(propietario);
            request.getSession().setAttribute("user", propietario);
        }
    }

    private boolean esAdmin(HttpServletRequest request) {
        Usuario u = (Usuario) request.getSession().getAttribute("user");
        return u != null && "ADMIN".equals(u.getRol());
    }
}
