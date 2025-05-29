package br.com.athlon.repository;

import br.com.athlon.model.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtletaRepository extends JpaRepository<Atleta, Long> {

    List<Atleta> findByNomeContaining(String nome);
}
