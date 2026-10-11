package com.locafilmes.controller;

import com.locafilmes.dto.diretor.DiretorRequestDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.service.DiretorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diretores")
@RequiredArgsConstructor
@Tag(name = "Diretores", description = "Consulta para CLIENTE e ADMIN. Escrita somente ADMIN.")
public class DiretorController {

    private final DiretorService diretorService;

    @Operation(summary = "Cadastrar diretor")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DiretorResponseDTO> criar(@Valid @RequestBody DiretorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diretorService.criar(dto));
    }

    @Operation(summary = "Buscar diretor por id")
    @GetMapping("/{id}")
    public ResponseEntity<DiretorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diretorService.buscarPorId(id));
    }

    @Operation(summary = "Listar diretores")
    @GetMapping
    public ResponseEntity<List<DiretorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(diretorService.listarTodos());
    }

    @Operation(summary = "Atualizar diretor")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DiretorResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DiretorRequestDTO dto) {
        return ResponseEntity.ok(diretorService.atualizar(id, dto));
    }

    @Operation(summary = "Excluir diretor")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        diretorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
