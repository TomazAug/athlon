package br.com.athlon.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtletaRegistrationDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private Double altura;
    private Double peso;

    @NotBlank(message = "Sexo é obrigatório")
    private String sexo;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "Username é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres")
    private String password;

}
