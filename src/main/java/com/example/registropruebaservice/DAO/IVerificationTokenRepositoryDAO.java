package com.example.registropruebaservice.DAO;

import com.example.registropruebaservice.JPA.VerificationTokenJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVerificationTokenRepositoryDAO extends JpaRepository<VerificationTokenJPA, Integer> {

    VerificationTokenJPA findByToken(String token);
}
