package com.mycompany.webapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Reserva{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reserva_id;
    @ManyToOne
    private Propiedad propiedad;
    @ManyToOne
    private Usuario usuario;
    @Column(nullable = false)
    private String fecha_inicio;
    @Column(nullable = false)
    private String fecha_fin;


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
    public String getFecha_inicio() {
        return fecha_inicio;
    }
    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }
    public String getFecha_fin() {
        return fecha_fin;
    }
    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
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
    @Column(nullable = false)
    private double precio_total;
    @Column(nullable = false)
    private String estado;

}
