package br.com.athlon.controller;

import br.com.athlon.model.Atleta;
import br.com.athlon.repository.AtletaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

    // Listagem de atletas
    @GetMapping
    public String listagem(Model model) {
        List<Atleta> atletas = atletaRepository.findAll();
        model.addAttribute("atletas", atletas);
        return "admin/atleta/listagem";
    }

    // Exibição do formulário para adicionar novo atleta
    @GetMapping("/form-inserir")
    public String formInserir(Model model) {
        model.addAttribute("atleta", new Atleta());
        return "admin/atleta/inserir";
    }

    // Salvar um atleta (inserção ou atualização)
    @PostMapping("/salvar")
    public String salvar(@Valid Atleta atleta, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            if (atleta.getId() != null) {
                return "admin/atleta/alterar";
            }
            return "admin/atleta/inserir";
        }

        atletaRepository.save(atleta);
        attributes.addFlashAttribute("mensagem", "Atleta salvo com sucesso!");
        return "redirect:/atleta";
    }

    // Remoção de um atleta
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        atletaRepository.delete(atleta);
        attributes.addFlashAttribute("mensagem", "Atleta excluído com sucesso!");
        return "redirect:/atleta";
    }

    // Edição de um atleta existente
    @GetMapping("/alterar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        Atleta atleta = atletaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("atleta", atleta);
        return "admin/atleta/alterar";
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
