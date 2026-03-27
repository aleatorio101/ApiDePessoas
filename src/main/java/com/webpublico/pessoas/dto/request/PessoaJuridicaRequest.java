package com.webpublico.pessoas.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class PessoaJuridicaRequest {

    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 255, message = "Razão social deve ter no máximo 255 caracteres")
    private String razaoSocial;

    @NotBlank(message = "CNPJ é obrigatório")
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    private String email;

    @Valid
    private List<EnderecoRequest> enderecos = new ArrayList<>();
}
