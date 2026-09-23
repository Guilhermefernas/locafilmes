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
import java.util.HashSet;
import java.util.Set;

/** equals/hashCode/toString restritos ao "id" — ver nota em {@link Diretor}. */
@Entity
@Table(name = "filmes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 150)
    @ToString.Include
    private String titulo;

    @Column(name = "ano_lancamento")
    @ToString.Include
    private Integer anoLancamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal valorDiaria;

    @Column(name = "quantidade_disponivel", nullable = false)
    @ToString.Include
    private Integer quantidadeDisponivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diretor_id")
    private Diretor diretor;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "filme_categoria",
            joinColumns = @JoinColumn(name = "filme_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "filmes")
    private Set<Locacao> locacoes = new HashSet<>();

    /** Regra de negócio: só é possível alugar um filme com quantidade disponível > 0. */
    public boolean possuiDisponibilidade() {
        return quantidadeDisponivel != null && quantidadeDisponivel > 0;
    }

    public void decrementarDisponibilidade() {
        this.quantidadeDisponivel = this.quantidadeDisponivel - 1;
    }

    public void incrementarDisponibilidade() {
        this.quantidadeDisponivel = this.quantidadeDisponivel + 1;
    }
}
