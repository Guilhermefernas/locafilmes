package com.locafilmes.controller;

import com.locafilmes.dto.filme.FilmeRequestDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.service.FilmeService;
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
@RequestMapping("/api/filmes")
@RequiredArgsConstructor
@Tag(name = "Filmes", description = "Consulta para CLIENTE e ADMIN. Cadastro, alteração e exclusão somente ADMIN.")
public class FilmeController {

    private final FilmeService filmeService;

    @Operation(summary = "Cadastrar filme")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FilmeResponseDTO> criar(@Valid @RequestBody FilmeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmeService.criar(dto));
    }

    @Operation(summary = "Buscar filme por id")
    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(filmeService.buscarPorId(id));
    }

    @Operation(summary = "Listar filmes", description = "Sem filtro lista todos. Com categoriaId lista os filmes da categoria.")
    @GetMapping
    public ResponseEntity<List<FilmeResponseDTO>> listar(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(filmeService.listarPorCategoria(categoriaId));
        }
        return ResponseEntity.ok(filmeService.listarTodos());
    }

    @Operation(summary = "Atualizar filme")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FilmeRequestDTO dto) {
        return ResponseEntity.ok(filmeService.atualizar(id, dto));
    }

    @Operation(summary = "Excluir filme")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        filmeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
