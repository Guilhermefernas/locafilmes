package com.locafilmes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/** equals/hashCode/toString restritos ao "id" — ver nota em {@link Diretor}. */
@Entity
@Table(name = "locacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_locacao", nullable = false)
    @ToString.Include
    private LocalDate dataLocacao;

    @Column(name = "data_devolucao_prevista", nullable = false)
    @ToString.Include
    private LocalDate dataDevolucaoPrevista;

    @Column(name = "data_devolucao")
    @ToString.Include
    private LocalDate dataDevolucao;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ToString.Include
    private StatusLocacao status;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "locacao_filme",
            joinColumns = @JoinColumn(name = "locacao_id"),
            inverseJoinColumns = @JoinColumn(name = "filme_id")
    )
    private Set<Filme> filmes = new HashSet<>();
}
