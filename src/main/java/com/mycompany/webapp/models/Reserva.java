package com.mycompany.webapp.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = "Reserva.findAll", query = "SELECT r FROM Reserva r"),
    @NamedQuery(name = "Reserva.findOverlapping", 
        query = "SELECT r FROM Reserva r WHERE r.propiedad.propiedad_id = :propiedadId " +
                "AND r.estado = 'CONFIRMADA' " + 
                "AND r.fecha_inicio < :fechaFin " + 
                "AND r.fecha_fin > :fechaInicio"),
    @NamedQuery(name = "Reserva.findByUsuario", 
        query = "SELECT r FROM Reserva r WHERE r.usuario.usuario_id = :usuarioId ORDER BY r.fecha_inicio DESC")
})
public class Reserva{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reserva_id;
    @ManyToOne
    private Propiedad propiedad;
    @ManyToOne
    private Usuario usuario;
    @Column(nullable = false)
    private LocalDate fecha_inicio;
    @Column(nullable = false)
    private LocalDate fecha_fin;
    @Column(nullable = false)
    private double precio_total;
    @Column(nullable = false)
    private String estado;

    public Reserva() {
    }
    public Propiedad getPropiedad() {
        return propiedad;
    }
    public long getReserva_id() {
        return reserva_id;
    }
    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public LocalDate getFecha_inicio() {
        return fecha_inicio;
    }
    public void setFecha_inicio(LocalDate fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }
    public LocalDate getFecha_fin() {
        return fecha_fin;
    }
    public void setFecha_fin(LocalDate fechaInicio) {
        this.fecha_fin = fechaInicio;
    }
    public double getPrecio_total() {
        return precio_total;
    }
    public void setPrecio_total(double precio_total) {
        this.precio_total = precio_total;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setCreadoEn(LocalDate now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCreadoEn'");
    }
    

}
