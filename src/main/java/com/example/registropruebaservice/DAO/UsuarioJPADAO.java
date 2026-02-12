package com.example.registropruebaservice.DAO;

import com.example.registropruebaservice.JPA.Result;
import com.example.registropruebaservice.JPA.UsuarioJPA;
import com.example.registropruebaservice.JPA.VerificationTokenJPA;
import com.example.registropruebaservice.Service.EmailService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioJPADAO implements IUsuarioJPA {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private IVerificationTokenRepositoryDAO iVerificationTokenRepositoryDAO;

    @Autowired
    private IUsuarioRepositoryDAO iUsuarioRepositoryDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Result RegistrarUsuario(UsuarioJPA usuario) {
        Result result = new Result();
        try {
            UsuarioJPA usuarioExistente = iUsuarioRepositoryDAO.findByUsername(usuario.getUsername());
            if (usuarioExistente != null) {
                result.correct = false;
                result.errorMessage = "El username ya está registrado";
                result.status = 400;
                return result;
            }

            String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(passwordEncriptada);
            usuario.setIsVerified(0);

            entityManager.persist(usuario);
            entityManager.flush();

            VerificationTokenJPA verificationToken = new VerificationTokenJPA();
            verificationToken.setToken(java.util.UUID.randomUUID().toString());
            verificationToken.setUsuario(usuario);
            verificationToken.setExpiracion(java.time.LocalDateTime.now().plusHours(24));

            entityManager.persist(verificationToken);

            try {
                emailService.enviarCorreoVerificacion(usuario.getCorreo(), verificationToken.getToken());
            } catch (Exception emailEx) {
                System.err.println("Error al enviar correo: " + emailEx.getMessage());
            }

            result.correct = true;
            result.status = 200;
            result.object = "Usuario registrado exitosamente.Verifica tu correo electrónico.";
            result.data = verificationToken.getToken();

        } catch (Exception ex) {
            ex.printStackTrace();
            result.correct = false;
            result.errorMessage = "No se registró el usuario: " + ex.getMessage();
            result.status = 500;
            result.ex = ex;
        }

        return result;
    }

    @Override
    @Transactional
    public Result ReenviarVerificacion(String correo) {
        Result result = new Result();
        try {
            UsuarioJPA usuario = entityManager.createQuery(
                    "SELECT u FROM UsuarioJPA u WHERE u.correo = :correo", UsuarioJPA.class)
                    .setParameter("correo", correo)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                result.correct = false;
                result.errorMessage = "No se encontró un usuario con ese correo";
                result.status = 404;
                return result;
            }

            if (usuario.getIsVerified() == 1) {
                result.correct = false;
                result.errorMessage = "Este usuario ya está verificado";
                result.status = 400;
                return result;
            }

            VerificationTokenJPA tokenExistente = entityManager.createQuery(
                    "SELECT v FROM VerificationTokenJPA v WHERE v.usuario.idUsuario = :idUsuario", VerificationTokenJPA.class)
                    .setParameter("idUsuario", usuario.getIdUsuario())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (tokenExistente != null) {
                tokenExistente.setToken(java.util.UUID.randomUUID().toString());
                tokenExistente.setExpiracion(java.time.LocalDateTime.now().plusHours(24));
                entityManager.merge(tokenExistente);
            } else {
                tokenExistente = new VerificationTokenJPA();
                tokenExistente.setToken(java.util.UUID.randomUUID().toString());
                tokenExistente.setUsuario(usuario);
                tokenExistente.setExpiracion(java.time.LocalDateTime.now().plusHours(24));
                entityManager.persist(tokenExistente);
            }

            emailService.enviarCorreoVerificacion(usuario.getCorreo(), tokenExistente.getToken());

            result.correct = true;
            result.status = 200;
            result.object = "Correo de verificación reenviado exitosamente";
            result.data = tokenExistente.getToken();

        } catch (Exception ex) {
            ex.printStackTrace();
            result.correct = false;
            result.errorMessage = "Error al reenviar verificación: " + ex.getMessage();
            result.status = 500;
            result.ex = ex;
        }

        return result;
    }
}
