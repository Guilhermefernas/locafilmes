package com.locafilmes.controller;

import com.locafilmes.dto.filme.FilmeRequestDTO;
import com.locafilmes.dto.filme.FilmeResponseDTO;
import com.locafilmes.service.FilmeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filmes")
@RequiredArgsConstructor
@Tag(name = "Filmes")
public class FilmeController {

    private final FilmeService filmeService;

    @PostMapping
    public ResponseEntity<FilmeResponseDTO> criar(@Valid @RequestBody FilmeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmeService.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(filmeService.buscarPorId(id));
    }

    // RF06: listar filmes por categoria. Ex.: GET /api/filmes?categoriaId=2
    @GetMapping
    public ResponseEntity<List<FilmeResponseDTO>> listar(@RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(filmeService.listarPorCategoria(categoriaId));
        }
        return ResponseEntity.ok(filmeService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FilmeRequestDTO dto) {
        return ResponseEntity.ok(filmeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        filmeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
