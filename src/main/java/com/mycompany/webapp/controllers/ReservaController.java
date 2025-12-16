package com.mycompany.webapp.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.webapp.models.Propiedad;
import com.mycompany.webapp.models.Reserva;
import com.mycompany.webapp.models.Usuario;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        if (accion.equals("/nueva")) {
            mostrarFormularioReserva(request, response);
        } else if (accion.equals("/mis-reservas")) { // <--- NUEVA ACCIÓN
            mostrarMisReservas(request, response);
        } else if (accion.equals("/admin/lista")) {
            if (esAdmin(request)) {
                mostrarListaReservasAdmin(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Solo administradores.");
            }
        } else if (accion.equals("/api/fechas-reservadas")) { // <--- NUEVA ACCIÓN
            getReservasJson(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getPathInfo() != null ? request.getPathInfo() : "";

        if (accion.equals("/guardar")) {
            procesarReserva(request, response);
        } else if (accion.equals("/cancelar")) { // <--- NUEVA ACCIÓN DE POST
        cancelarReserva(request, response);
        }else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void mostrarFormularioReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Verificar sesión del usuario
            HttpSession session = request.getSession();
            Usuario usuario = (Usuario) session.getAttribute("user");
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/usuario/entrar");
                return;
            }

            // 2. Obtener el ID de la propiedad de la URL
            String strPropiedadId = request.getParameter("propiedadId");
            if (strPropiedadId == null || strPropiedadId.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de propiedad no proporcionado.");
                return;
            }
            Long propiedadId = Long.parseLong(strPropiedadId);

            // 3. Obtener la Propiedad de la base de datos
            // Nota: Se asume que 'em' está inyectado y es válido.
            Propiedad propiedad = em.find(Propiedad.class, propiedadId);

            if (propiedad == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Propiedad no encontrada.");
                return;
            }
            System.out.println("Encuentra la propiedad");
            // 4. Pasar la propiedad a la vista y redirigir al formulario
            request.setAttribute("p", propiedad);
            request.setAttribute("view", "/WEB-INF/views/reserva/reservaForm.jsp");
            System.out.println("Va a la vista");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error al mostrar formulario de reserva", e);
            request.setAttribute("error", "Error interno al procesar la solicitud.");
            request.setAttribute("view", "/WEB-INF/views/error.jsp");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
        }
    }

    private void procesarReserva(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("user");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        // Obtener datos del formulario
        String strPropiedadId = request.getParameter("propiedadId");
        String strFechaInicio = request.getParameter("fechaInicio");
        String strFechaFin = request.getParameter("fechaFin");

        // Validaciones básicas
        if (strPropiedadId == null || strFechaInicio == null || strFechaFin == null || strFechaInicio.isEmpty()
                || strFechaFin.isEmpty()) {
            request.setAttribute("error", "Debes seleccionar un rango de fechas válido.");
            // Recargar la propiedad para volver a mostrar el formulario
            try {
                Long propiedadId = Long.parseLong(strPropiedadId);
                Propiedad propiedad = em.find(Propiedad.class, propiedadId);
                request.setAttribute("p", propiedad);
                request.setAttribute("view", "/WEB-INF/views/reserva/reservaForm.jsp");
                request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al recargar la propiedad.");
            }
            return;
        }

        try {
            Long propiedadId = Long.parseLong(strPropiedadId);
            LocalDate fechaInicio = LocalDate.parse(strFechaInicio, DATE_FORMAT);
            LocalDate fechaFin = LocalDate.parse(strFechaFin, DATE_FORMAT); // El día de salida (checkout)

            // Validación de fechas
            if (fechaInicio.isBefore(LocalDate.now()) || fechaFin.isBefore(fechaInicio)) {
                request.setAttribute("error",
                        "Las fechas seleccionadas no son válidas. Asegúrate que la fecha de inicio es hoy o posterior y la fecha de fin es posterior a la de inicio.");
                // Volver al formulario con el error
                Propiedad propiedad = em.find(Propiedad.class, propiedadId);
                request.setAttribute("p", propiedad);
                request.setAttribute("view", "/WEB-INF/views/reserva/reservaForm.jsp");
                request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
                return;
            }

            Propiedad propiedad = em.find(Propiedad.class, propiedadId);

            if (propiedad == null) {
                request.setAttribute("error", "Propiedad no encontrada para reservar.");
                response.sendRedirect(request.getContextPath() + "/propiedades");
                return;
            }

            if (!isAvailable(propiedadId, fechaInicio, fechaFin)) {
                request.setAttribute("error",
                        "Las fechas seleccionadas se solapan con una reserva existente y no están disponibles.");
                // Volver al formulario con el error
                request.setAttribute("p", propiedad);
                request.setAttribute("view", "/WEB-INF/views/reserva/reservaForm.jsp");
                request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
                return;
            }

            // *** Lógica de Disponibilidad: Este es un punto clave a implementar ***
            // Aquí deberías realizar una consulta a la BD para verificar que no
            // existen reservas que se solapen con el rango [fechaInicio, fechaFin].
            // Por ahora, asumimos que está disponible.

            // Creación y guardado de la reserva
            utx.begin();

            Reserva nuevaReserva = new Reserva();
            nuevaReserva.setPropiedad(propiedad);
            nuevaReserva.setUsuario(usuario);
            nuevaReserva.setFecha_fin(fechaFin);
            nuevaReserva.setFecha_inicio(fechaInicio);

            // Calcular el número de noches (días entre inicio y fin, el día de fin NO se
            // cuenta como noche)
            long noches = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
            if (noches < 1)
                noches = 1; // Mínimo 1 noche de reserva

            // Calcular el precio total
            double precioTotal = noches * propiedad.getPrecio_habitacion();
            nuevaReserva.setPrecio_total(precioTotal);
            nuevaReserva.setEstado("CONFIRMADA");

            em.persist(nuevaReserva);

            utx.commit();

            // Redirigir a una página de confirmación (o al detalle de la propiedad con un
            // mensaje)
            session.setAttribute("mensaje",
                    "¡Reserva realizada con éxito! Total a pagar: " + precioTotal + "€ por " + noches + " noches.");
            response.sendRedirect(request.getContextPath() + "/propiedad/detalle?id=" + propiedadId);

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error al procesar la reserva", e);
            try {
                if (utx != null)
                    utx.rollback();
            } catch (Exception rbEx) {
                Log.log(Level.SEVERE, "Error durante el rollback", rbEx);
            }
            // Intento de volver al formulario con el error
            request.setAttribute("error", "Error grave al intentar guardar la reserva. Inténtelo de nuevo.");
            try {
                Long propiedadId = Long.parseLong(request.getParameter("propiedadId"));
                Propiedad propiedad = em.find(Propiedad.class, propiedadId);
                request.setAttribute("p", propiedad);
                request.setAttribute("view", "/WEB-INF/views/reserva/reservaForm.jsp");
                request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error interno al procesar la reserva.");
            }
        }
    }

    private void mostrarListaReservasAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Obtener todas las reservas usando la NamedQuery
            List<Reserva> reservas = em.createNamedQuery("Reserva.findAll", Reserva.class).getResultList();

            // 2. Pasar la lista a la vista
            request.setAttribute("reservas", reservas);
            request.setAttribute("view", "/WEB-INF/views/reserva/listaReserva.jsp");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error al listar reservas para admin", e);
            request.setAttribute("error", "Error interno al listar reservas: " + e.getMessage());
            request.setAttribute("view", "/WEB-INF/views/error.jsp");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
        }
    }

    private boolean esAdmin(HttpServletRequest request) {
        Usuario u = (Usuario) request.getSession().getAttribute("user");
        return u != null && "ADMIN".equals(u.getRol());
    }

    private boolean isAvailable(Long propiedadId, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<Reserva> overlappingReservas = em.createNamedQuery("Reserva.findOverlapping", Reserva.class)
                    .setParameter("propiedadId", propiedadId)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFin", fechaFin)
                    .getResultList();

            return overlappingReservas.isEmpty();
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error comprobando disponibilidad", e);
            // En caso de error de sistema, asumimos no disponible por seguridad
            return false;
        }
    }

    // Método utilitario para obtener todas las reservas confirmadas de una
    // propiedad
    private List<Reserva> findReservasForProperty(Long propiedadId) {
        try {
            // Usamos una consulta dinámica ya que estamos añadiendo solo un NamedQuery
            return em.createQuery(
                    "SELECT r FROM Reserva r WHERE r.propiedad.propiedad_id = :propiedadId AND r.estado = 'CONFIRMADA'",
                    Reserva.class)
                    .setParameter("propiedadId", propiedadId)
                    .getResultList();
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error encontrando reservas para propiedad " + propiedadId, e);
            return Collections.emptyList();
        }
    }

    // NUEVO ENDPOINT API: Maneja la solicitud AJAX y devuelve las fechas reservadas
    // en JSON
    private void getReservasJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String strPropiedadId = request.getParameter("propiedadId");
        if (strPropiedadId == null || strPropiedadId.isEmpty()) {
            response.getWriter().write("[]");
            return;
        }

        try {
            Long propiedadId = Long.parseLong(strPropiedadId);
            List<Reserva> reservas = findReservasForProperty(propiedadId);

            // Convertir la lista de Reserva a un formato JSON de rangos de fechas
            // (compatible con flatpickr disable)
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Reserva r : reservas) {
                LocalDate inicio = r.getFecha_inicio();
                // El día de check-out (fecha_fin) está disponible para una nueva entrada, por
                // lo que deshabilitamos hasta el día anterior.
                LocalDate fin = r.getFecha_fin().minusDays(1);

                if (inicio.isBefore(fin) || inicio.isEqual(fin)) { // Asegurarse de que hay al menos 1 día para
                                                                   // deshabilitar
                    if (!first) {
                        json.append(",");
                    }
                    json.append("{\"from\":\"").append(DATE_FORMAT.format(inicio)).append("\",");
                    json.append("\"to\":\"").append(DATE_FORMAT.format(fin)).append("\"}");
                    first = false;
                }
            }
            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error generando JSON de reservas", e);
            response.getWriter().write("[]");
        }
    }

    private void mostrarMisReservas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("user") : null;

        if (usuario == null) {
            // Redirigir al login si no hay usuario
            response.sendRedirect(request.getContextPath() + "/usuario/entrar");
            return;
        }

        try {
            // Usar la NamedQuery para obtener las reservas del usuario
            List<Reserva> misReservas = em.createNamedQuery("Reserva.findByUsuario", Reserva.class)
                    .setParameter("usuarioId", usuario.getUsuario_id())
                    .getResultList();

            // Pasar la lista a la vista
            request.setAttribute("misReservas", misReservas);
            request.setAttribute("view", "/WEB-INF/views/reserva/misReservas.jsp");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);

        } catch (Exception e) {
            Log.log(Level.SEVERE, "Error cargando mis reservas", e);
            request.setAttribute("error", "Error interno al cargar tus reservas.");
            request.setAttribute("view", "/WEB-INF/views/error.jsp");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
        }
    }

    private void cancelarReserva(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    HttpSession session = request.getSession(false);
    Usuario usuario = (session != null) ? (Usuario) session.getAttribute("user") : null;
    
    // 1. Verificar autenticación
    if (usuario == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Debe iniciar sesión para cancelar una reserva.");
        return;
    }

    String reservaIdStr = request.getParameter("id");
    if (reservaIdStr == null || reservaIdStr.isEmpty()) {
        request.setAttribute("error", "ID de reserva no proporcionado.");
        request.setAttribute("view", "/WEB-INF/views/error.jsp");
        request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
        return;
    }

    Long reservaId = Long.parseLong(reservaIdStr);
    
    try {
        em.getTransaction().begin();
        
        // 2. Buscar la reserva
        Reserva reserva = em.find(Reserva.class, reservaId);

        if (reserva == null) {
            em.getTransaction().rollback();
            request.setAttribute("error", "La reserva no existe.");
            request.setAttribute("view", "/WEB-INF/views/error.jsp");
            request.getRequestDispatcher("/WEB-INF/views/template.jsp").forward(request, response);
            return;
        }
        
        // 3. Verificar que el usuario es el dueño de la reserva (Seguridad)
        if (reserva.getUsuario().getUsuario_id() != usuario.getUsuario_id()) {
            em.getTransaction().rollback();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permiso para cancelar esta reserva.");
            return;
        }

        // 4. Verificar estado (Opcional: solo permitir cancelar si está confirmada o pendiente, no si ya terminó)
        if (!reserva.getEstado().equals("CONFIRMADA")) {
             em.getTransaction().rollback();
             session.setAttribute("warning", "Solo se pueden cancelar reservas CONFIRMADAS.");
             response.sendRedirect(request.getContextPath() + "/reserva/mis-reservas");
             return;
        }
        
        // 5. Borrar la reserva
        em.remove(reserva);
        em.getTransaction().commit();

        // 6. Redirigir con mensaje de éxito
        session.setAttribute("success", "Reserva para " + reserva.getPropiedad().getNombre() + " cancelada correctamente.");
        response.sendRedirect(request.getContextPath() + "/reserva/mis-reservas");
        
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        Log.log(Level.SEVERE, "Error al cancelar la reserva: " + reservaId, e);
        session.setAttribute("error", "Error interno al procesar la cancelación.");
        response.sendRedirect(request.getContextPath() + "/reserva/mis-reservas");
    }
}
}
