package com.mycompany.webapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "usuarios")
@NamedQueries({
        @NamedQuery(name = "Usuario.findByCredentials", query = "SELECT u FROM Usuario u WHERE u.email = :email AND u.contra = :pwd"),
        @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u")
})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long usuario_id;
    @NotEmpty(message = "El nombre no puede ser nulo")
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    @NotEmpty(message = "Los apellidos no pueden ser nulos")
    private String apellidos;
    @Column(nullable = false)
    @Email(message = "El email debe tener un formato valido")
    @NotEmpty(message = "El email no puede ser nulo")
    private String email;
    @Column(nullable = false)
    @NotEmpty(message = "La contrasena no puede ser nula")
    private String contra;
    @Column(nullable = false)
    private String rol = "USER";

    public Usuario() {
    }

    public Usuario(@NotEmpty(message = "El nombre no puede ser nulo") String nombre,
            @NotEmpty(message = "Los apellidos no pueden ser nulos") String apellidos,
            @Email(message = "El email debe tener un formato valido") @NotEmpty(message = "El email no puede ser nulo") String email,
            @NotEmpty(message = "La contrasena no puede ser nula") String contra) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contra = contra;
    }

    public long getUsuario_id() {
        return usuario_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getContra() {
        return contra;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
