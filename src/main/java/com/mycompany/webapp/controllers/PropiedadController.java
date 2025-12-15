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
                // 1. Iniciar Transacción
                utx.begin();

                // 2. Crear y persistir Propiedad
                Propiedad p = nuevaPropiedad(request);
                em.persist(p);
                
                // 3. Flush para generar el ID de la propiedad (Vital para las fotos)
                em.flush(); 

                // 4. Guardar Fotos (Físico + BD)
                guardarFotos(p, request.getParts());

                // 5. Commit de todo
                utx.commit();

                // Redirigir al éxito (usando contextPath para evitar hardcodear localhost)
                response.sendRedirect(request.getContextPath() + "/propiedades");
                
            } catch (Exception e) {
                // 6. ¡ROLLBACK IMPRESCINDIBLE!
                // Si falla algo, deshacemos todo para no dejar datos basura
                try {
                    if (utx.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Log.log(Level.SEVERE, "Error haciendo rollback", ex);
                }
                
                Log.log(Level.SEVERE, "Error al guardar propiedad", e);
                e.printStackTrace(); // Para que lo veas en la consola
                
                request.setAttribute("msg", "Error al guardar: " + e.getMessage());
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
        return p;
    }

    private String guardarFotos( Propiedad p, Collection<Part> fotosForm){
        String url_perfil = "";
        String carpetaRelativa = "static/img/propiedades/";
        String rutaAbsoluta = getServletContext().getRealPath(carpetaRelativa);
        rutaAbsoluta += File.separator +  p.getPropiedad_id() + File.separator;
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
                if(part.getName().equals("portada")){
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
}
