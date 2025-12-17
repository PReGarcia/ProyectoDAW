package com.mycompany.webapp.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.webapp.models.Propiedad;
import com.mycompany.webapp.models.Reserva;
import com.mycompany.webapp.models.Usuario;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

@WebServlet(name = "ReservaController", urlPatterns = { "/reserva/*", "/reservas/*" })
public class ReservaController extends HttpServlet {

    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(ReservaController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getPathInfo();
        if (accion == null)
            accion = "/error";

        switch (accion) {
            case "/nueva":
                mostrarFormularioReserva(request, response);
                break;
            case "/mis-reservas":
                mostrarMisReservas(request, response);
                break;
            case "/admin/lista":
                listarReservasAdmin(request, response);
                break;
            case "/api/fechas-reservadas":
                devolverFechasReservadasJson(request, response);
                break;
            case "/cancelar":
                cancelarReserva(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getPathInfo();

        if ("/guardar".equals(accion)) {
            crearReserva(request, response);
        } else if ("/cancelar".equals(accion)) {
            cancelarReserva(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }


    private void mostrarFormularioReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (getUsuarioSesion(request) == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            Long propiedadId = Long.parseLong(request.getParameter("propiedadId"));
            Propiedad p = em.find(Propiedad.class, propiedadId);

            if (p != null) {
                request.setAttribute("p", p);
                forwardToView(request, response, "/WEB-INF/views/reserva/reservaForm.jsp");
            } else {
                manejarError(request, response, "La propiedad no existe.");
            }
        } catch (Exception e) {
            manejarError(request, response, "Error al cargar el formulario de reserva.");
        }
    }

    private void mostrarMisReservas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            // Consulta para mis reservas
            List<Reserva> misReservas = em.createNamedQuery("Reserva.findByUsuario", Reserva.class)
                    .setParameter("usuarioId", u.getUsuario_id())
                    .getResultList();

            request.setAttribute("misReservas", misReservas);
            forwardToView(request, response, "/WEB-INF/views/reserva/misReservas.jsp");
        } catch (Exception e) {
            manejarError(request, response, "Error al cargar tus reservas.");
        }
    }

    private void listarReservasAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!esAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            List<Reserva> reservas = em.createNamedQuery("Reserva.findAll", Reserva.class).getResultList();
            request.setAttribute("reservas", reservas);
            forwardToView(request, response, "/WEB-INF/views/reserva/listaReserva.jsp");
        } catch (Exception e) {
            manejarError(request, response, "Error al listar reservas.");
        }
    }


    private void devolverFechasReservadasJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Long propiedadId = Long.parseLong(request.getParameter("propiedadId"));

            // Buscar reservas confirmadas
            List<Reserva> reservas = em.createQuery(
                    "SELECT r FROM Reserva r WHERE r.propiedad.propiedad_id = :propiedadId AND r.estado = 'CONFIRMADA'",
                    Reserva.class)
                    .setParameter("propiedadId", propiedadId)
                    .getResultList();

            // Construir JSON manualmente
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (Reserva r : reservas) {
                LocalDate inicio = r.getFecha_inicio();
                LocalDate fin = r.getFecha_fin().minusDays(1);

                if (!inicio.isAfter(fin)) { // Solo si el rango es válido
                    if (!first)
                        json.append(",");
                    json.append(String.format("{\"from\":\"%s\",\"to\":\"%s\"}",
                            DATE_FORMAT.format(inicio), DATE_FORMAT.format(fin)));
                    first = false;
                }
            }
            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            Log.log(Level.WARNING, "Error generando JSON de reservas", e);
            response.getWriter().write("[]");
        }
    }


    private void crearReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        String strPropiedadId = request.getParameter("propiedadId");

        try {
            Long propiedadId = Long.parseLong(strPropiedadId);
            LocalDate inicio = LocalDate.parse(request.getParameter("fechaInicio"), DATE_FORMAT);
            LocalDate fin = LocalDate.parse(request.getParameter("fechaFin"), DATE_FORMAT);

            if (inicio.isBefore(LocalDate.now()) || !fin.isAfter(inicio)) {
                throw new Exception("Fechas inválidas. Verifica que la fecha de inicio no sea pasada.");
            }

            if (!isAvailable(propiedadId, inicio, fin)) {
                request.setAttribute("error", "Las fechas seleccionadas ya no están disponibles.");
                // Recargar formulario
                Propiedad p = em.find(Propiedad.class, propiedadId);
                request.setAttribute("p", p);
                forwardToView(request, response, "/WEB-INF/views/reserva/reservaForm.jsp");
                return;
            }

            utx.begin();

            Propiedad p = em.find(Propiedad.class, propiedadId);
            if (p == null)
                throw new Exception("Propiedad no encontrada.");

            Reserva r = new Reserva();
            r.setPropiedad(p);
            r.setUsuario(u);
            r.setFecha_inicio(inicio);
            r.setFecha_fin(fin);
            r.setEstado("CONFIRMADA");

            // Cálculo de precio
            long noches = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin);
            if (noches < 1)
                noches = 1;
            r.setPrecio_total(noches * p.getPrecio_habitacion());

            em.persist(r);
            utx.commit();

            request.getSession().setAttribute("msg", "¡Reserva realizada con éxito!"); // Puedes usar esto en la vista
            response.sendRedirect(request.getContextPath() + "/reserva/mis-reservas");

        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            Log.log(Level.SEVERE, "Error creando reserva", e);

            // Volver al formulario con error
            try {
                if (strPropiedadId != null) {
                    Propiedad p = em.find(Propiedad.class, Long.parseLong(strPropiedadId));
                    request.setAttribute("p", p);
                }
            } catch (Exception ex) {
            }

            request.setAttribute("error", "No se pudo realizar la reserva: " + e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/reserva/reservaForm.jsp");
        }
    }

    private void cancelarReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario u = getUsuarioSesion(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            Long id = Long.parseLong(request.getParameter("id"));
            utx.begin();

            Reserva r = em.find(Reserva.class, id);

            if (r != null && (r.getUsuario().getUsuario_id() == u.getUsuario_id() || "ADMIN".equals(u.getRol()))) {
                em.remove(r);
                utx.commit();
            } else {
                utx.rollback();
                throw new Exception("No tienes permiso o la reserva no existe.");
            }
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
            }
            Log.log(Level.SEVERE, "Error cancelando reserva", e);
        }

        // Redirigir siempre a la lista (mis reservas o admin según rol)
        if ("ADMIN".equals(u.getRol())) {
            response.sendRedirect(request.getContextPath() + "/reserva/admin/lista");
        } else {
            response.sendRedirect(request.getContextPath() + "/reserva/mis-reservas");
        }
    }

    private boolean isAvailable(Long propiedadId, LocalDate inicio, LocalDate fin) {
        try {
            List<Reserva> overlapping = em.createNamedQuery("Reserva.findOverlapping", Reserva.class)
                    .setParameter("propiedadId", propiedadId)
                    .setParameter("fechaInicio", inicio)
                    .setParameter("fechaFin", fin)
                    .getResultList();
            return overlapping.isEmpty();
        } catch (Exception e) {
            return false; // Ante la duda, no disponible
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
}