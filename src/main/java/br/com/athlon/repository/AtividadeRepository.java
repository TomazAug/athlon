package br.com.athlon.repository;

import br.com.athlon.model.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    List<Atividade> findByModalidadeContainingIgnoreCase(String modalidade);
}
