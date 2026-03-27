package com.webpublico.pessoas.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PessoaSummaryResponse {

    private Long id;
    private String tipo;
    private String nomeOuRazaoSocial;
    private String cpfOuCnpj;
    private String email;
}
