package com.mycompany.webapp.controllers;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private FotoController fc;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

    public PropiedadController(){
        fc = new FotoController();
    }

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
                request.setAttribute("imagenes", fc.findByPropiedad(em,p)); 
                if (p != null) {
                    request.setAttribute("p", p);
                    vista += "detalle.jsp";
                } else {
                    request.setAttribute("msg", "La propiedad no existe");
                    vista += "error.jsp";
                }
                break;

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
            try {
                Propiedad p = nuevaPropiedad(request);
                System.out.println("LLEga a antes de guardar las fotos");
                fc.guardarFotos(utx, em, p, request.getParts());
                System.out.println("LLEga despues de guardar las fotos");
                response.sendRedirect("http://localhost:8080/WebApp/");
            } catch (Exception e) {
                request.setAttribute("msg", "Error: datos no válidos");
                request.setAttribute("view", "/WEB-INF/views/error.jsp");
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
                rd.forward(request, response);
            }
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
        try {
            utx.begin();
            em.persist(p);
            Log.log(Level.INFO, "New User saved");
            utx.commit();
        } catch (Exception e) {
            Log.severe("Error al guardar la propiedad: " + e.getMessage());
            try {
                utx.rollback();
            } catch (Exception ex) {
                Log.severe("Error al hacer rollback: " + ex.getMessage());
            }
        }

        return p;
    }

    public Propiedad getById(Long id){
        TypedQuery<Propiedad> query = em.createNamedQuery("Propiedad.getById", Propiedad.class);
        query.setParameter("propiedad_id", id);

        return query.getSingleResult();
    }


}
