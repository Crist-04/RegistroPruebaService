package com.example.registropruebaservice.DAO;

import com.example.registropruebaservice.JPA.UsuarioJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepositoryDAO extends JpaRepository<UsuarioJPA, Integer> {

    UsuarioJPA findByUsername(String username);

}
