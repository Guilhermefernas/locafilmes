package com.locafilmes.controller;

import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.security.UsuarioAutenticado;
import com.locafilmes.service.LocacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locacoes")
@RequiredArgsConstructor
@Tag(name = "Locações", description = "ADMIN acessa qualquer locação. CLIENTE só cria, consulta e devolve as próprias.")
public class LocacaoController {

    private final LocacaoService locacaoService;

    @Operation(summary = "Registrar locação")
    @PostMapping
    public ResponseEntity<LocacaoResponseDTO> criar(@Valid @RequestBody LocacaoRequestDTO dto,
                                                    @AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locacaoService.criar(dto, logado));
    }

    @Operation(summary = "Buscar locação por id")
    @GetMapping("/{id}")
    public ResponseEntity<LocacaoResponseDTO> buscarPorId(@PathVariable Long id,
                                                          @AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.ok(locacaoService.buscarPorId(id, logado));
    }

    @Operation(summary = "Listar locações", description = "ADMIN lista todas ou filtra por usuarioId. CLIENTE recebe somente as suas.")
    @GetMapping
    public ResponseEntity<List<LocacaoResponseDTO>> listar(@RequestParam(required = false) Long usuarioId,
                                                           @AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.ok(locacaoService.listar(usuarioId, logado));
    }

    @Operation(summary = "Registrar devolução")
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<LocacaoResponseDTO> registrarDevolucao(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.ok(locacaoService.registrarDevolucao(id, logado));
    }
}
