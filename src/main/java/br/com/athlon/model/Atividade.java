package br.com.athlon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modalidade;
    private String unidadeMedida;
    private Double distancia;
    private Integer tempoTotal;
    private Integer percepcaoEsforco;
    private String tipoTreino;
    private String notas;
    private LocalDateTime dataHora;

    @ManyToOne(cascade = CascadeType.ALL)
    private Atleta atleta;

    // NOVO CAMPO PARA CONTROLE DE PERMISSÃO NA VIEW
    @Transient
    private boolean podeEditar;
}
