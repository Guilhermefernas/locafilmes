package com.locafilmes.mapper;

import com.locafilmes.dto.categoria.CategoriaResponseDTO;
import com.locafilmes.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponseDTO toResponse(Categoria categoria) {
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome());
    }
}
