package com.locafilmes.controller;

import com.locafilmes.dto.locacao.LocacaoRequestDTO;
import com.locafilmes.dto.locacao.LocacaoResponseDTO;
import com.locafilmes.service.LocacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locacoes")
@RequiredArgsConstructor
@Tag(name = "Locações")
public class LocacaoController {

    private final LocacaoService locacaoService;

    // RF07: registrar uma locação com um ou mais filmes.
    @PostMapping
    public ResponseEntity<LocacaoResponseDTO> criar(@Valid @RequestBody LocacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locacaoService.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.buscarPorId(id));
    }

    // RF09: listar as locações de um cliente. Ex.: GET /api/locacoes?usuarioId=1
    @GetMapping
    public ResponseEntity<List<LocacaoResponseDTO>> listar(@RequestParam(required = false) Long usuarioId) {
        if (usuarioId != null) {
            return ResponseEntity.ok(locacaoService.listarPorUsuario(usuarioId));
        }
        return ResponseEntity.ok(locacaoService.listarTodas());
    }

    // RF08: registrar a devolução de uma locação.
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<LocacaoResponseDTO> registrarDevolucao(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.registrarDevolucao(id));
    }
}
