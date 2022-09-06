package io.github.helio.domain.repository;

import io.github.helio.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Optional;

public interface UsuarioReposity extends JpaRepository<Usuario, Integer> {


    Optional<Usuario> findByLogin(String login);
}
