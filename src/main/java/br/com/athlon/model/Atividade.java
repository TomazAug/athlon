package br.com.athlon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String modalidade; // Enum com modalidades como CORRIDA, NATAÇÃO, etc.
    private String unidadeMedida; // Enum para unidade de medida (KM, M)
    private Double distancia; // Distância percorrida
    private Integer tempoTotal; // Tempo total em minutos
    private Integer percepcaoEsforco; // Percepção de esforço (escala de 0 a 10)
    private String tipoTreino; // Tipo de treino (livre, intervalado, etc.)
    private String notas; // Notas (opcionais)
    private LocalDateTime dataHora; // Data e hora da atividade, preenchida automaticamente

    @ManyToOne(cascade = CascadeType.ALL)
    private Atleta atleta;
}
