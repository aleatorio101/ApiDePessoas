package com.webpublico.pessoas.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "pessoa_juridica")
@DiscriminatorValue("J")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class PessoaJuridica extends Pessoa {

    @Column(name = "razao_social", nullable = false, length = 255)
    private String razaoSocial;


    @Column(name = "cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    public PessoaJuridica(String razaoSocial, String cnpj, String email) {
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        setEmail(email);
    }
}
