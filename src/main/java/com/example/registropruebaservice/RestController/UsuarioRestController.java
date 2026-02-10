package com.example.registropruebaservice.RestController;

import com.example.registropruebaservice.DAO.IUsuarioRepositoryDAO;
import com.example.registropruebaservice.DAO.IVerificationTokenRepositoryDAO;
import com.example.registropruebaservice.DAO.UsuarioJPADAO;
import com.example.registropruebaservice.JPA.Result;
import com.example.registropruebaservice.JPA.UsuarioJPA;
import com.example.registropruebaservice.JPA.VerificationTokenJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuario")
//@CrossOrigin(origins = "*")
public class UsuarioRestController {

    @Autowired
    private UsuarioJPADAO usuarioJPADAO;

    @Autowired
    private IVerificationTokenRepositoryDAO iVerificationTokenRepositoryDAO;

    @Autowired
    private IUsuarioRepositoryDAO iUsuarioRepositoryDAO;

    @PostMapping("/registrar")
    public ResponseEntity<Result> Registrar(@RequestBody UsuarioJPA usuario) {
        Result result = usuarioJPADAO.RegistrarUsuario(usuario);
        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("/verificar")
    public ResponseEntity<Result> Verificar(@RequestParam String token) {
        Result result = new Result();
        try {
            VerificationTokenJPA verificationToken = iVerificationTokenRepositoryDAO.findByToken(token);

            if (verificationToken == null) {
                result.correct = false;
                result.errorMessage = "Token inválido";
                result.status = 400;
                return ResponseEntity.status(400).body(result);
            }

            if (verificationToken.getExpiracion().isBefore(java.time.LocalDateTime.now())) {
                result.correct = false;
                result.errorMessage = "Token expirado.";
                result.status = 400;
                return ResponseEntity.status(400).body(result);
            }

            UsuarioJPA usuario = verificationToken.getUsuario();

            if (usuario.getIsVerified() == 1) {
                result.correct = false;
                result.errorMessage = "Este usuario ya ha sido verificado";
                result.status = 400;
                return ResponseEntity.status(400).body(result);
            }

            usuario.setIsVerified(1);
            iUsuarioRepositoryDAO.save(usuario);

            iVerificationTokenRepositoryDAO.delete(verificationToken);

            result.correct = true;
            result.status = 200;
            result.object = "Usuario verificado.";

        } catch (Exception ex) {
            ex.printStackTrace();
            result.correct = false;
            result.errorMessage = "Error al verificar usuario: " + ex.getMessage();
            result.status = 500;
            result.ex = ex;
        }

        return ResponseEntity.status(result.status).body(result);
    }

    @PostMapping("/reenviarVerificacion")
    public ResponseEntity<Result> ReenviarVerificacion(@RequestParam String correo) {
        Result result = usuarioJPADAO.ReenviarVerificacion(correo);
        return ResponseEntity.status(result.status).body(result);
    }
}
