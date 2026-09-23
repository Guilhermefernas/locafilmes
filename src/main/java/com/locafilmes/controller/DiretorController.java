package com.locafilmes.controller;

import com.locafilmes.dto.diretor.DiretorRequestDTO;
import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.service.DiretorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diretores")
@RequiredArgsConstructor
@Tag(name = "Diretores")
public class DiretorController {

    private final DiretorService diretorService;

    @PostMapping
    public ResponseEntity<DiretorResponseDTO> criar(@Valid @RequestBody DiretorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diretorService.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiretorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diretorService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<DiretorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(diretorService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiretorResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DiretorRequestDTO dto) {
        return ResponseEntity.ok(diretorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        diretorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
