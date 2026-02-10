package com.example.registropruebaservice.Service;

import com.example.registropruebaservice.DAO.IUsuarioRepositoryDAO;
import com.example.registropruebaservice.JPA.UsuarioJPA;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsJPAService implements UserDetailsService {

    private final IUsuarioRepositoryDAO iUsuarioRepositoryDAO;

    public UserDetailsJPAService(IUsuarioRepositoryDAO iUsuarioRepositoryDAO) {
        this.iUsuarioRepositoryDAO = iUsuarioRepositoryDAO;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioJPA usuario = iUsuarioRepositoryDAO.findByUsername(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");

        }

        if (usuario.getIsVerified() == 0) {
            throw new UsernameNotFoundException("Usuario no verificado");
        }

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                
                .build();

    }

}
