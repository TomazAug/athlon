package br.com.athlon.controller;

import br.com.athlon.model.Atividade;
import br.com.athlon.model.Atleta;
import br.com.athlon.repository.AtividadeRepository;
import br.com.athlon.repository.AtletaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/atividade")
public class AtividadeController {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isUser(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    private Atleta getAtletaDoUsuarioLogado(Authentication auth) {
        String username = auth.getName();
        return atletaRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Atleta não encontrado"));
    }

    // LISTAGEM
    @GetMapping
    public String listagem(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Atividade> atividades;

        if (isAdmin(auth)) {
            atividades = atividadeRepository.findAll();
        } else if (isUser(auth)) {
            Atleta atleta = getAtletaDoUsuarioLogado(auth);
            atividades = atividadeRepository.findByAtleta(atleta);
        } else {
            throw new AccessDeniedException("Acesso negado");
        }

        // Flag para controlar botões na view
        String username = auth.getName();
        atividades.forEach(a -> a.setPodeEditar(isAdmin(auth) || a.getAtleta().getUser().getUsername().equals(username)));

        model.addAttribute("atividades", atividades);
        return "admin/atividade/listagem";
    }

    // FORMULÁRIO DE INSERÇÃO (USER)
    @GetMapping("/form-inserir")
    public String formInserir(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isUser(auth)) {
            return "redirect:/atividade?mensagem=Somente atletas podem inserir atividades";
        }
        model.addAttribute("atividade", new Atividade());
        return "admin/atividade/inserir";
    }

    // SALVAR (USER)
    @PostMapping("/salvar")
    public String salvar(@Valid Atividade atividade, BindingResult result, RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isUser(auth)) {
            attributes.addFlashAttribute("mensagem", "Somente atletas podem salvar atividades");
            return "redirect:/atividade";
        }

        if (result.hasErrors()) {
            if (atividade.getId() != null) {
                return "admin/atividade/alterar";
            }
            return "admin/atividade/inserir";
        }

        Atleta atleta = getAtletaDoUsuarioLogado(auth);
        atividade.setAtleta(atleta);
        atividade.setDataHora(LocalDateTime.now());

        atividadeRepository.save(atividade);
        attributes.addFlashAttribute("mensagem", "Atividade salva com sucesso!");
        return "redirect:/atividade";
    }

    // EXCLUIR
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        if (isAdmin(auth) || (isUser(auth) && atividade.getAtleta().getUser().getUsername().equals(auth.getName()))) {
            atividadeRepository.delete(atividade);
            attributes.addFlashAttribute("mensagem", "Atividade excluída com sucesso!");
            return "redirect:/atividade";
        }

        attributes.addFlashAttribute("mensagem", "Você não tem permissão para excluir esta atividade");
        return "redirect:/atividade";
    }

    // ALTERAR
    @GetMapping("/alterar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        if (isAdmin(auth) || (isUser(auth) && atividade.getAtleta().getUser().getUsername().equals(auth.getName()))) {
            model.addAttribute("atividade", atividade);
            return "admin/atividade/alterar";
        }

        attributes.addFlashAttribute("mensagem", "Você não tem permissão para alterar esta atividade");
        return "redirect:/atividade";
    }

    // BUSCAR POR MODALIDADE
    @PostMapping("/buscar")
    public String buscarPorModalidade(@RequestParam("modalidade") String modalidade, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Atividade> atividades;

        if (isAdmin(auth)) {
            if (modalidade == null || modalidade.trim().isEmpty()) {
                atividades = atividadeRepository.findAll();
            } else {
                atividades = atividadeRepository.findByModalidadeContainingIgnoreCase(modalidade);
            }
        } else if (isUser(auth)) {
            Atleta atleta = getAtletaDoUsuarioLogado(auth);
            if (modalidade == null || modalidade.trim().isEmpty()) {
                atividades = atividadeRepository.findByAtleta(atleta);
            } else {
                atividades = atividadeRepository.findByModalidadeContainingIgnoreCase(modalidade);
                atividades.removeIf(a -> !a.getAtleta().equals(atleta));
            }
        } else {
            throw new AccessDeniedException("Acesso negado");
        }

        // Define a flag para a view
        String username = auth.getName();
        atividades.forEach(a -> a.setPodeEditar(isAdmin(auth) || a.getAtleta().getUser().getUsername().equals(username)));

        model.addAttribute("atividades", atividades);
        return "admin/atividade/listagem";
    }
}
