package com.locafilmes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Nota sobre equals/hashCode/toString: usamos apenas o campo "id" (e não @Data),
 * pois Diretor possui relacionamento bidirecional 1:N com Filme. Se o Lombok gerasse
 * equals/hashCode/toString incluindo a coleção "filmes", e Filme por sua vez incluísse
 * o "diretor" de volta, teríamos recursão infinita (StackOverflowError) ao colocar
 * objetos em HashSet/HashMap ou ao logar/depurar as entidades.
 */
@Entity
@Table(name = "diretores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Diretor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String nome;

    @Column(length = 60)
    @ToString.Include
    private String nacionalidade;

    @Builder.Default
    @OneToMany(mappedBy = "diretor")
    private Set<Filme> filmes = new HashSet<>();
}
