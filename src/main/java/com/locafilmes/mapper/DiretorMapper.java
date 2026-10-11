package com.locafilmes.mapper;

import com.locafilmes.dto.diretor.DiretorResponseDTO;
import com.locafilmes.entity.Diretor;
import org.springframework.stereotype.Component;

@Component
public class DiretorMapper {

    public DiretorResponseDTO toResponse(Diretor diretor) {
        return new DiretorResponseDTO(diretor.getId(), diretor.getNome(), diretor.getNacionalidade());
    }
}
