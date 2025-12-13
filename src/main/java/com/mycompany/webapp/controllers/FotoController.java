package com.mycompany.webapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.mycompany.webapp.models.Foto;
import com.mycompany.webapp.models.Propiedad;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;

@WebServlet(name = "FotoController", urlPatterns = {"/foto/*"})
public class FotoController extends HttpServlet{

    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

@Override
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
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

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        String accion = request.getPathInfo();
        switch (accion) {
            case "/guardar":
                PropiedadController pc = new PropiedadController();
                Integer num = 1;
                guardarFotos(pc.getById(num.longValue()) ,request.getParts()); 

                response.sendRedirect("http://localhost:8080/WebApp/");
                break;
        
            default:
                break;
        }
    }

    public void guardarFotos(Propiedad p, Collection<Part> fotosForm) {
        String uploadPath = getServletContext().getRealPath("") + File.separator + "static" + File.separator + "img"
                + File.separator + "propiedades" + File.separator + p.getPropiedad_id();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists())
            uploadDir.mkdirs();

        List<Foto> listaFotos = new ArrayList<>();

        try {
            for (Part part : fotosForm) {
                if (part.getName().equals("imagenes") && part.getSize() > 0) {
                    String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    File file = new File(uploadPath, fileName);

                    try (InputStream input = part.getInputStream()) {
                        Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }

                    String rutaRelativa = "static/img/propiedades/" + p.getPropiedad_id() + File.separator + fileName;
                    Foto foto = new Foto(rutaRelativa, p);
                    listaFotos.add(foto);
                }
            }

            if (!listaFotos.isEmpty()) {
                utx.begin();
                for (Foto f : listaFotos) {
                    em.persist(f);
                }
                utx.commit();
            }
        } catch (Exception e) {
            Log.severe("Error: " + e.getMessage());
        }

    }
    
}
