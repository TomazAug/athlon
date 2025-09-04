package br.com.athlon.repository;

import br.com.athlon.model.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtletaRepository extends JpaRepository<Atleta, Long> {

    List<Atleta> findByNomeContaining(String nome);
    Optional<Atleta> findByUser_Username(String username);
}
