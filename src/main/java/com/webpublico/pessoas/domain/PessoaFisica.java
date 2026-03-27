package com.webpublico.pessoas.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "pessoa_fisica")
@DiscriminatorValue("F")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class PessoaFisica extends Pessoa {

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;


    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    public PessoaFisica(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        setEmail(email);
    }
}
