package com.locafilmes.controller;

import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.security.UsuarioAutenticado;
import com.locafilmes.service.LocacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Autorização das locações:
 *  - ADMIN: acessa e gerencia qualquer locação.
 *  - CLIENTE: só cria e consulta as PRÓPRIAS locações (verificado pelo id do token).
 */
@RestController
@RequestMapping("/api/locacoes")
@RequiredArgsConstructor
@Tag(name = "Locações")
public class LocacaoController {

    private final LocacaoService locacaoService;

    // RF07: registrar uma locação com um ou mais filmes.
    @PostMapping
    public ResponseEntity<LocacaoResponseDTO> criar(@Valid @RequestBody LocacaoRequestDTO dto,
                                                    @AuthenticationPrincipal UsuarioAutenticado logado) {
        garantirAcesso(logado, dto.usuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(locacaoService.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocacaoResponseDTO> buscarPorId(@PathVariable Long id,
                                                          @AuthenticationPrincipal UsuarioAutenticado logado) {
        LocacaoResponseDTO locacao = locacaoService.buscarPorId(id);
        garantirAcesso(logado, locacao.usuario().id());
        return ResponseEntity.ok(locacao);
    }

    // RF09: listar as locações de um cliente. Ex.: GET /api/locacoes?usuarioId=1
    // CLIENTE sempre enxerga apenas as suas; ADMIN pode listar todas ou filtrar por usuário.
    @GetMapping
    public ResponseEntity<List<LocacaoResponseDTO>> listar(@RequestParam(required = false) Long usuarioId,
                                                           @AuthenticationPrincipal UsuarioAutenticado logado) {
        if (!logado.isAdmin()) {
            garantirAcesso(logado, usuarioId == null ? logado.getId() : usuarioId);
            return ResponseEntity.ok(locacaoService.listarPorUsuario(logado.getId()));
        }
        if (usuarioId != null) {
            return ResponseEntity.ok(locacaoService.listarPorUsuario(usuarioId));
        }
        return ResponseEntity.ok(locacaoService.listarTodas());
    }

    // RF08: registrar a devolução de uma locação (dono da locação ou ADMIN).
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<LocacaoResponseDTO> registrarDevolucao(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UsuarioAutenticado logado) {
        garantirAcesso(logado, locacaoService.buscarPorId(id).usuario().id());
        return ResponseEntity.ok(locacaoService.registrarDevolucao(id));
    }

    /** ADMIN passa sempre; CLIENTE só se o recurso pertencer a ele. Caso contrário, 403. */
    private void garantirAcesso(UsuarioAutenticado logado, Long donoId) {
        if (!logado.isAdmin() && !logado.getId().equals(donoId)) {
            throw new AccessDeniedException("Acesso negado à locação de outro usuário");
        }
    }
}
