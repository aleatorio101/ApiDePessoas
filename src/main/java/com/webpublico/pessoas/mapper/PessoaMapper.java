package com.webpublico.pessoas.mapper;

import com.webpublico.pessoas.domain.Endereco;
import com.webpublico.pessoas.domain.Pessoa;
import com.webpublico.pessoas.domain.PessoaFisica;
import com.webpublico.pessoas.domain.PessoaJuridica;
import com.webpublico.pessoas.dto.request.EnderecoRequest;
import com.webpublico.pessoas.dto.request.PessoaFisicaRequest;
import com.webpublico.pessoas.dto.request.PessoaJuridicaRequest;
import com.webpublico.pessoas.dto.response.EnderecoResponse;
import com.webpublico.pessoas.dto.response.PessoaFisicaResponse;
import com.webpublico.pessoas.dto.response.PessoaJuridicaResponse;
import com.webpublico.pessoas.dto.response.PessoaSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class PessoaMapper {


    public PessoaFisica toEntity(PessoaFisicaRequest request) {
        PessoaFisica pessoa = new PessoaFisica();
        pessoa.setNome(request.getNome());
        pessoa.setCpf(formatarCpf(request.getCpf()));
        pessoa.setEmail(request.getEmail());

        if (request.getEnderecos() != null) {
            request.getEnderecos().forEach(endReq ->
                    pessoa.adicionarEndereco(toEntity(endReq))
            );
        }

        return pessoa;
    }

    public PessoaJuridica toEntity(PessoaJuridicaRequest request) {
        PessoaJuridica pessoa = new PessoaJuridica();
        pessoa.setRazaoSocial(request.getRazaoSocial());
        pessoa.setCnpj(formatarCnpj(request.getCnpj()));
        pessoa.setEmail(request.getEmail());

        if (request.getEnderecos() != null) {
            request.getEnderecos().forEach(endReq ->
                    pessoa.adicionarEndereco(toEntity(endReq))
            );
        }

        return pessoa;
    }

    public Endereco toEntity(EnderecoRequest request) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(request.getLogradouro());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado().toUpperCase());
        endereco.setCep(request.getCep());
        return endereco;
    }

    public PessoaFisicaResponse toResponse(PessoaFisica pessoa) {
        return PessoaFisicaResponse.builder()
                .id(pessoa.getId())
                .tipo("FISICA")
                .nome(pessoa.getNome())
                .cpf(pessoa.getCpf())
                .email(pessoa.getEmail())
                .enderecos(toEnderecoResponseList(pessoa.getEnderecos()))
                .criadoEm(pessoa.getCriadoEm())
                .atualizadoEm(pessoa.getAtualizadoEm())
                .build();
    }

    public PessoaJuridicaResponse toResponse(PessoaJuridica pessoa) {
        return PessoaJuridicaResponse.builder()
                .id(pessoa.getId())
                .tipo("JURIDICA")
                .razaoSocial(pessoa.getRazaoSocial())
                .cnpj(pessoa.getCnpj())
                .email(pessoa.getEmail())
                .enderecos(toEnderecoResponseList(pessoa.getEnderecos()))
                .criadoEm(pessoa.getCriadoEm())
                .atualizadoEm(pessoa.getAtualizadoEm())
                .build();
    }

    public PessoaSummaryResponse toSummary(Pessoa pessoa) {
        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            return PessoaSummaryResponse.builder()
                    .id(pf.getId())
                    .tipo("FISICA")
                    .nomeOuRazaoSocial(pf.getNome())
                    .cpfOuCnpj(pf.getCpf())
                    .email(pf.getEmail())
                    .build();
        }

        PessoaJuridica pj = (PessoaJuridica) pessoa;
        return PessoaSummaryResponse.builder()
                .id(pj.getId())
                .tipo("JURIDICA")
                .nomeOuRazaoSocial(pj.getRazaoSocial())
                .cpfOuCnpj(pj.getCnpj())
                .email(pj.getEmail())
                .build();
    }

    private EnderecoResponse toResponse(Endereco endereco) {
        return EnderecoResponse.builder()
                .id(endereco.getId())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .cep(endereco.getCep())
                .build();
    }

    private List<EnderecoResponse> toEnderecoResponseList(List<Endereco> enderecos) {
        return enderecos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private String formatarCpf(String cpf) {
        String digits = cpf.replaceAll("[^\\d]", "");
        if (digits.length() != 11) {
            return cpf;
        }
        return String.format("%s.%s.%s-%s",
                digits.substring(0, 3),
                digits.substring(3, 6),
                digits.substring(6, 9),
                digits.substring(9, 11)
        );
    }

    private String formatarCnpj(String cnpj) {
        String digits = cnpj.replaceAll("[^\\d]", "");
        if (digits.length() != 14) {
            return cnpj;
        }
        return String.format("%s.%s.%s/%s-%s",
                digits.substring(0, 2),
                digits.substring(2, 5),
                digits.substring(5, 8),
                digits.substring(8, 12),
                digits.substring(12, 14)
        );
    }
}
