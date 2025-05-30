package br.com.athlon.controller;

import br.com.athlon.model.Atividade;
import br.com.athlon.repository.AtividadeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/atividade")
public class AtividadeController {

    @Autowired
    private AtividadeRepository atividadeRepository;

    // Listagem de atividades
    @GetMapping
    public String listagem(Model model) {
        List<Atividade> atividades = atividadeRepository.findAll();
        model.addAttribute("atividades", atividades);
        return "admin/atividade/listagem";
    }

    // Exibição do formulário para adicionar novo atividade
    @GetMapping("/form-inserir")
    public String formInserir(Model model) {
        model.addAttribute("atividade", new Atividade());
        return "admin/atividade/inserir";
    }

    // Salvar uma atividade (inserção ou atualização)
    @PostMapping("/salvar")
    public String salvar(@Valid Atividade atividade, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            if (atividade.getId() != null) {
                return "admin/atividade/alterar";
            }
            return "admin/atividade/inserir";
        }

        atividadeRepository.save(atividade);
        attributes.addFlashAttribute("mensagem", "Atividade salva com sucesso!");
        return "redirect:/atividade";
    }

    // Remoção de uma atividade
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        atividadeRepository.delete(atividade);
        attributes.addFlashAttribute("mensagem", "Atividade excluída com sucesso!");
        return "redirect:/atividade";
    }

    // Edição de uma atividade existente
    @GetMapping("/alterar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        Atividade atividade = atividadeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("atividade", atividade);
        return "admin/atividade/alterar";
    }

    // Busca por modalidade
    @PostMapping("/buscar")
    public String buscarPorModalidade(@RequestParam("modalidade") String modalidade, Model model) {
        if (modalidade == null || modalidade.trim().isEmpty()) {
            // Se o campo de busca estiver vazio, retorna todas as atividades
            model.addAttribute("atividades", atividadeRepository.findAll());
        } else {
            // Buscar as atividades que contêm a modalidade na descrição
            List<Atividade> atividades = atividadeRepository.findByModalidadeContainingIgnoreCase(modalidade);
            model.addAttribute("atividades", atividades);
        }

        return "admin/atividade/listagem";
    }

}
