package com.mycompany.webapp.models;

import jakarta.inject.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = "Propiedad.findAll", query = "SELECT p FROM Propiedad p"),
})
public class Propiedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long propiedad_id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String calle_numero;
    @Column(nullable = false)
    private String ciudad;
    @Column(nullable = false)
    private String codigo_postal;
    @Column(nullable = false)
    private double precio_habitacion;
    @Column(nullable = false)
    private int habitaciones;
    @Column(nullable = false)
    private int baños; 
    @Column(nullable = false)
    private double latitud;
    @Column(nullable = false)
    private double longitud;
    private String descripcion;
    @ManyToOne
    @JoinColumn(name = "id_propietario", nullable = false)
    private Usuario propietario;

    public Propiedad() {
    }

    public Propiedad(String nombre, String calle_numero, String ciudad, String codigo_postal,
            double precio_habitacion, int habitaciones, int baños, double latitud, double longitud,
            String descripcion, Usuario propietario) {
        this.nombre = nombre;
        this.calle_numero = calle_numero;
        this.ciudad = ciudad;
        this.codigo_postal = codigo_postal;
        this.precio_habitacion = precio_habitacion;
        this.habitaciones = habitaciones;
        this.baños = baños;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
        this.propietario = propietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }


    public double getPrecio_habitacion() {
        return precio_habitacion;
    }

    public void setPrecio_habitacion(double precio_habitacion) {
        this.precio_habitacion = precio_habitacion;
    }

    public int getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(int habitaciones) {
        this.habitaciones = habitaciones;
    }

    public int getBaños() {
        return baños;
    }

    public void setBaños(int baños) {
        this.baños = baños;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public long getPropiedad_id() {
        return propiedad_id;
    }
    public String getCalle_numero() {
        return calle_numero;
    }
    public void setCalle_numero(String calle_numero) {
        this.calle_numero = calle_numero;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getCodigo_postal() {
        return codigo_postal;
    }
    public void setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
    }
}
