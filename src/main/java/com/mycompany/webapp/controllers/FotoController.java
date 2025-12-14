package com.mycompany.webapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mycompany.webapp.models.Foto;
import com.mycompany.webapp.models.Propiedad;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
@WebServlet(name = "FotoController", urlPatterns = { "/foto/*" })
public class FotoController extends HttpServlet {

    /*@PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());
    */

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getPathInfo();
        String vista = "/WEB-INF/views/foto/";
        switch (accion) {
            case "/form":
                vista += "fotoForm.jsp";
                break;

            default:
                break;
        }
        request.setAttribute("view", vista);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/template.jsp");
        rd.forward(request, response);
    }

    

    public void guardarFotos( UserTransaction utx, EntityManager em,Propiedad p, Collection<Part> fotosForm)
            throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException,
            RollbackException, HeuristicMixedException, HeuristicRollbackException {

        String carpetaRelativa = "static/img/propiedades/" + p.getPropiedad_id() + "/";
        String rutaAbsoluta = getServletContext().getRealPath(carpetaRelativa);

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
            }
        }

        if (!listaFotos.isEmpty()) {
            utx.begin();
            for (Foto foto : listaFotos) {
                em.persist(foto);
            }
            utx.commit();
        }

    }

    public List<Foto> findByPropiedad(EntityManager em, Propiedad p) {
        TypedQuery<Foto> query = em.createNamedQuery("Foto.findByPropiedad", Foto.class);
        query.setParameter("propiedad", p);

        return query.getResultList();

    }



/*@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String accion = request.getPathInfo();
        switch (accion) {
            case "/guardar":
                Long num = 1L;
                TypedQuery<Propiedad> query = em.createNamedQuery("Propiedad.getById", Propiedad.class);
                query.setParameter("propiedad_id", num);
                try {
                    guardarFotos(query.getSingleResult(), request.getParts());
                } catch (SecurityException | IllegalStateException | IOException | NotSupportedException
                        | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException
                        | ServletException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                response.sendRedirect("http://localhost:8080/WebApp/");
                break;

            default:
                break;
        }
    }*/
}
