package com.example.registropruebaservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    public void enviarCorreoVerificacion(String destinatario, String token) throws Exception {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject("Verifica tu cuenta");

        String contenidoHtml = construirEmailHtml(token);
        helper.setText(contenidoHtml, true);

        mailSender.send(mensaje);
    }

    private String construirEmailHtml(String token) {
        String urlVerificacion = baseUrl + "/api/usuario/verificar?token=" + token;

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 50px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
                + ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }"
                + ".content { padding: 30px; color: #333333; line-height: 1.6; }"
                + ".button { display: inline-block; padding: 12px 30px; margin: 20px 0; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }"
                + ".button:hover { background-color: #45a049; }"
                + ".footer { text-align: center; padding: 20px; color: #777777; font-size: 12px; }"
                + ".warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 20px 0; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h1>¡Bienvenido!</h1>"
                + "</div>"
                + "<div class='content'>"
                + "<h2>Verifica tu cuenta</h2>"
                + "<p>Gracias por registrarte en nuestro sistema. Para completar tu registro, por favor verifica tu correo electrónico haciendo clic en el botón de abajo:</p>"
                + "<div style='text-align: center;'>"
                + "<a href='" + urlVerificacion + "' class='button'>Verificar mi cuenta</a>"
                + "</div>"
                + "<div class='warning'>"
                + "<strong>Importante:</strong> Este enlace expirará en 24 horas."
                + "</div>"
                + "<p>Si no puedes hacer clic en el botón, copia y pega el siguiente enlace en tu navegador:</p>"
                + "<p style='word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 4px;'>" + urlVerificacion + "</p>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>© 2025 Sistema de Registro. Todos los derechos reservados.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}
