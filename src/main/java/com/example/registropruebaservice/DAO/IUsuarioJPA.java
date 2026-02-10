package com.example.registropruebaservice.DAO;

import com.example.registropruebaservice.JPA.Result;
import com.example.registropruebaservice.JPA.UsuarioJPA;

public interface IUsuarioJPA {

    Result RegistrarUsuario(UsuarioJPA usuario);

    Result ReenviarVerificacion(String correo);
}
