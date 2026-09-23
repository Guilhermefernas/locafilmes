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
 * equals/hashCode restritos ao "id" — ver nota em {@link Diretor}. Além disso a "senha"
 * é excluída do toString por segurança (nunca deve aparecer em logs).
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    @ToString.Include
    private String email;

    /** Sempre armazenada já criptografada com BCrypt (feito no service). Nunca incluída no toString. */
    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ToString.Include
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "usuario")
    private Set<Locacao> locacoes = new HashSet<>();
}
