package com.webpublico.pessoas.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class EnderecoResponse {

    private Long id;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
