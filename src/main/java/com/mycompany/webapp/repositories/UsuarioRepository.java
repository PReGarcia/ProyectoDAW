package com.mycompany.webapp.repositories;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.webapp.controllers.UsuarioController;
import com.mycompany.webapp.models.Usuario;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.UserTransaction;

public class UsuarioRepository {
    @PersistenceContext(unitName = "WebAppPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private static final Logger Log = Logger.getLogger(UsuarioController.class.getName());

    public void nuevoUsuario(Usuario u) {
        Long id = u.getUsuario_id();
        try {
            utx.begin();
            em.persist(u);
            Log.log(Level.INFO, "New User saved");
            utx.commit();
        } catch (Exception e) {
            Log.log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

    public Usuario findByCredentials(String email, String pwd) {
        Usuario user = null;
        try {
            List<Usuario> users;
            TypedQuery<Usuario> q1 = em.createNamedQuery("Users.findByCredentials", Usuario.class);
            q1.setParameter("email", email);
            q1.setParameter("pwd", pwd);
            users = q1.getResultList();

            if (!users.isEmpty()) {
                user = users.get(0);
            }
        } catch (Exception e) {
            Log.log(Level.WARNING, "EXCEPTION: ", e);
        }

        return user;
    }

}
