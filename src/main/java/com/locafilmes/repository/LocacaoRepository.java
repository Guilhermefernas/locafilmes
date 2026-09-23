package com.locafilmes.repository;

import com.locafilmes.entity.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    List<Locacao> findByUsuario_IdOrderByDataLocacaoDesc(Long usuarioId);
}
