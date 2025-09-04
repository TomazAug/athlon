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

    // Listagem: admin vê lista vazia, user vê suas atividades
    @GetMapping
    public String listagem(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            // Admin vê lista vazia (ou todas atividades, se preferir)
            model.addAttribute("atividades", List.of());
        } else if (isUser(auth)) {
            Atleta atleta = getAtletaDoUsuarioLogado(auth);
            List<Atividade> atividades = atividadeRepository.findByAtleta(atleta);
            model.addAttribute("atividades", atividades);
        } else {
            throw new AccessDeniedException("Acesso negado");
        }

        return "admin/atividade/listagem";
    }

    // Formulário para inserir - só USER pode acessar
    @GetMapping("/form-inserir")
    public String formInserir(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isUser(auth)) {
            return "redirect:/atividade?mensagem=Somente atletas podem inserir atividades";
        }

        model.addAttribute("atividade", new Atividade());
        return "admin/atividade/inserir";
    }

    // Salvar atividade - só USER pode salvar
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

    // Excluir atividade - só USER dono pode excluir
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isUser(auth)) {
            attributes.addFlashAttribute("mensagem", "Somente atletas podem excluir atividades");
            return "redirect:/atividade";
        }

        Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        Atleta atleta = getAtletaDoUsuarioLogado(auth);

        if (!atividade.getAtleta().equals(atleta)) {
            attributes.addFlashAttribute("mensagem", "Você não pode excluir uma atividade que não é sua!");
            return "redirect:/atividade";
        }

        atividadeRepository.delete(atividade);
        attributes.addFlashAttribute("mensagem", "Atividade excluída com sucesso!");
        return "redirect:/atividade";
    }

    // Alterar atividade - só USER dono pode alterar
    @GetMapping("/alterar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!isUser(auth)) {
            attributes.addFlashAttribute("mensagem", "Somente atletas podem alterar atividades");
            return "redirect:/atividade";
        }

        Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        Atleta atleta = getAtletaDoUsuarioLogado(auth);

        if (!atividade.getAtleta().equals(atleta)) {
            attributes.addFlashAttribute("mensagem", "Você não pode alterar uma atividade que não é sua!");
            return "redirect:/atividade";
        }

        model.addAttribute("atividade", atividade);
        return "admin/atividade/alterar";
    }

    // Buscar por modalidade - admin vê lista vazia, user filtra as próprias
    @PostMapping("/buscar")
    public String buscarPorModalidade(@RequestParam("modalidade") String modalidade, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(auth)) {
            // Admin vê lista vazia ou todas (escolha a que preferir)
            model.addAttribute("atividades", List.of());
        } else if (isUser(auth)) {
            Atleta atleta = getAtletaDoUsuarioLogado(auth);

            List<Atividade> atividades;
            if (modalidade == null || modalidade.trim().isEmpty()) {
                atividades = atividadeRepository.findByAtleta(atleta);
            } else {
                atividades = atividadeRepository.findByModalidadeContainingIgnoreCase(modalidade);
                atividades.removeIf(a -> !a.getAtleta().equals(atleta));
            }
            model.addAttribute("atividades", atividades);
        } else {
            throw new AccessDeniedException("Acesso negado");
        }

        return "admin/atividade/listagem";
    }
}
