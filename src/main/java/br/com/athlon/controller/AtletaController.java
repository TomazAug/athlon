package br.com.athlon.controller;

import br.com.athlon.dto.AtletaRegistrationDto;
import br.com.athlon.model.Atleta;
import br.com.athlon.model.User;
import br.com.athlon.repository.AtletaRepository;
import br.com.athlon.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/atleta")
public class AtletaController {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listagem de atletas
    @GetMapping
    public String listagem(Model model) {
        List<Atleta> atletas = atletaRepository.findAll();
        model.addAttribute("atletas", atletas);
        return "admin/atleta/listagem";
    }

    // Formulário para inserir novo atleta + usuário (com DTO)
    @GetMapping("/form-inserir")
    public String formInserir(Model model) {
        model.addAttribute("atletaDto", new AtletaRegistrationDto());
        return "admin/atleta/inserir";
    }

    // Salvar atleta e criar usuário (com DTO)
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("atletaDto") AtletaRegistrationDto atletaDto,
                         BindingResult result,
                         RedirectAttributes attributes,
                         Model model) {

        // Verificar erros de validação no DTO
        if (result.hasErrors()) {
            return "admin/atleta/inserir";
        }

        // Verificar se username já existe
        if (userRepository.existsByUsername(atletaDto.getUsername())) {
            model.addAttribute("mensagem", "Erro: username já está em uso!");
            return "admin/atleta/inserir";
        }

        // Criar User com senha criptografada
        User user = new User();
        user.setUsername(atletaDto.getUsername());
        user.setPassword(passwordEncoder.encode(atletaDto.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        // Criar Atleta e associar User
        Atleta atleta = new Atleta();
        atleta.setNome(atletaDto.getNome());
        atleta.setAltura(atletaDto.getAltura());
        atleta.setPeso(atletaDto.getPeso());
        atleta.setSexo(atletaDto.getSexo());
        atleta.setDataNascimento(atletaDto.getDataNascimento());
        atleta.setUser(user);

        atletaRepository.save(atleta);

        attributes.addFlashAttribute("mensagem", "Atleta e usuário salvos com sucesso!");
        return "redirect:/atleta";
    }

    // Remover atleta e usuário associado
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        User user = atleta.getUser();
        atletaRepository.delete(atleta);
        if(user != null){
            userRepository.delete(user);
        }
        attributes.addFlashAttribute("mensagem", "Atleta e usuário excluídos com sucesso!");
        return "redirect:/atleta";
    }

    // Formulário para alteração do atleta (sem alterar usuário)
    @GetMapping("/alterar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("atleta", atleta);
        return "admin/atleta/alterar";
    }

    // Atualizar atleta (sem alteração do usuário)
    @PostMapping("/atualizar")
    public String atualizar(@Valid Atleta atleta, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "admin/atleta/alterar";
        }
        atletaRepository.save(atleta);
        attributes.addFlashAttribute("mensagem", "Atleta atualizado com sucesso!");
        return "redirect:/atleta";
    }

    // Busca por nome (parcial)
    @PostMapping("/buscar")
    public String buscar(@Param("nome") String nome, Model model) {
        if (nome == null || nome.trim().isEmpty()) {
            model.addAttribute("atletas", atletaRepository.findAll());
        } else {
            List<Atleta> atletas = atletaRepository.findByNomeContaining(nome);
            model.addAttribute("atletas", atletas);
        }
        return "admin/atleta/listagem";
    }
}
