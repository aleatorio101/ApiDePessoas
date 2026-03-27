package com.webpublico.pessoas.service;

import com.webpublico.pessoas.domain.Pessoa;
import com.webpublico.pessoas.domain.PessoaFisica;
import com.webpublico.pessoas.domain.PessoaJuridica;
import com.webpublico.pessoas.dto.request.PessoaFisicaRequest;
import com.webpublico.pessoas.dto.request.PessoaJuridicaRequest;
import com.webpublico.pessoas.dto.response.PessoaFisicaResponse;
import com.webpublico.pessoas.dto.response.PessoaJuridicaResponse;
import com.webpublico.pessoas.dto.response.PessoaSummaryResponse;
import com.webpublico.pessoas.exception.DocumentoDuplicadoException;
import com.webpublico.pessoas.exception.PessoaNaoEncontradaException;
import com.webpublico.pessoas.exception.TipoPessoaIncompativelException;
import com.webpublico.pessoas.mapper.PessoaMapper;
import com.webpublico.pessoas.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;

    @Transactional
    public PessoaFisicaResponse cadastrarPessoaFisica(PessoaFisicaRequest request) {
        log.info("Cadastrando pessoa física com CPF: {}", request.getCpf());

        validarCpfUnico(request.getCpf(), null);
        validarEmailUnico(request.getEmail(), null);

        PessoaFisica pessoa = pessoaMapper.toEntity(request);
        PessoaFisica salva = (PessoaFisica) pessoaRepository.save(pessoa);

        log.info("Pessoa física cadastrada com id: {}", salva.getId());
        return pessoaMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public PessoaFisicaResponse buscarPessoaFisica(Long id) {
        PessoaFisica pessoa = buscarComoFisica(id);
        return pessoaMapper.toResponse(pessoa);
    }

    @Transactional
    public PessoaFisicaResponse atualizarPessoaFisica(Long id, PessoaFisicaRequest request) {
        log.info("Atualizando pessoa física id: {}", id);

        PessoaFisica pessoa = buscarComoFisica(id);

        validarCpfUnico(request.getCpf(), id);
        validarEmailUnico(request.getEmail(), id);

        pessoa.setNome(request.getNome());
        pessoa.setCpf(pessoaMapper.toEntity(request).getCpf());
        pessoa.setEmail(request.getEmail());

        pessoa.getEnderecos().clear();
        if (request.getEnderecos() != null) {
            request.getEnderecos().forEach(endReq ->
                    pessoa.adicionarEndereco(pessoaMapper.toEntity(endReq)));
        }

        return pessoaMapper.toResponse(pessoa);
    }

    @Transactional
    public PessoaJuridicaResponse cadastrarPessoaJuridica(PessoaJuridicaRequest request) {
        log.info("Cadastrando pessoa jurídica com CNPJ: {}", request.getCnpj());

        validarCnpjUnico(request.getCnpj(), null);
        validarEmailUnico(request.getEmail(), null);

        PessoaJuridica pessoa = pessoaMapper.toEntity(request);
        PessoaJuridica salva = (PessoaJuridica) pessoaRepository.save(pessoa);

        log.info("Pessoa jurídica cadastrada com id: {}", salva.getId());
        return pessoaMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public PessoaJuridicaResponse buscarPessoaJuridica(Long id) {
        PessoaJuridica pessoa = buscarComoJuridica(id);
        return pessoaMapper.toResponse(pessoa);
    }

    @Transactional
    public PessoaJuridicaResponse atualizarPessoaJuridica(Long id, PessoaJuridicaRequest request) {
        log.info("Atualizando pessoa jurídica id: {}", id);

        PessoaJuridica pessoa = buscarComoJuridica(id);

        validarCnpjUnico(request.getCnpj(), id);
        validarEmailUnico(request.getEmail(), id);

        pessoa.setRazaoSocial(request.getRazaoSocial());
        pessoa.setCnpj(pessoaMapper.toEntity(request).getCnpj());
        pessoa.setEmail(request.getEmail());

        pessoa.getEnderecos().clear();
        if (request.getEnderecos() != null) {
            request.getEnderecos().forEach(endReq ->
                    pessoa.adicionarEndereco(pessoaMapper.toEntity(endReq)));
        }

        return pessoaMapper.toResponse(pessoa);
    }

    @Transactional(readOnly = true)
    public List<PessoaSummaryResponse> listarTodas() {
        return pessoaRepository.listarTodas()
                .stream()
                .map(pessoaMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public void remover(Long id) {
        log.info("Removendo pessoa id: {}", id);

        if (!pessoaRepository.existsById(id)) {
            throw new PessoaNaoEncontradaException(id);
        }

        pessoaRepository.deleteById(id);
        log.info("Pessoa id {} removida com sucesso.", id);
    }

    private PessoaFisica buscarComoFisica(Long id) {
        Pessoa pessoa = pessoaRepository.buscarPorIdComEnderecos(id)
                .orElseThrow(() -> new PessoaNaoEncontradaException(id));

        if (!(pessoa instanceof PessoaFisica)) {
            throw new TipoPessoaIncompativelException(id, "FISICA");
        }

        return (PessoaFisica) pessoa;
    }

    private PessoaJuridica buscarComoJuridica(Long id) {
        Pessoa pessoa = pessoaRepository.buscarPorIdComEnderecos(id)
                .orElseThrow(() -> new PessoaNaoEncontradaException(id));

        if (!(pessoa instanceof PessoaJuridica)) {
            throw new TipoPessoaIncompativelException(id, "JURIDICA");
        }

        return (PessoaJuridica) pessoa;
    }

    private void validarCpfUnico(String cpf, Long idAtual) {
        String cpfFormatado = cpf.replaceAll("[^\\d]", "");

        if (pessoaRepository.existeCpf(cpf, idAtual) ||
            pessoaRepository.existeCpf(
                String.format("%s.%s.%s-%s",
                        cpfFormatado.substring(0, 3),
                        cpfFormatado.substring(3, 6),
                        cpfFormatado.substring(6, 9),
                        cpfFormatado.substring(9, 11)),
                idAtual)) {
            throw new DocumentoDuplicadoException("CPF", cpf);
        }
    }

    private void validarCnpjUnico(String cnpj, Long idAtual) {
        String cnpjFormatado = cnpj.replaceAll("[^\\d]", "");
        if (pessoaRepository.existeCnpj(cnpj, idAtual) ||
            pessoaRepository.existeCnpj(
                String.format("%s.%s.%s/%s-%s",
                        cnpjFormatado.substring(0, 2),
                        cnpjFormatado.substring(2, 5),
                        cnpjFormatado.substring(5, 8),
                        cnpjFormatado.substring(8, 12),
                        cnpjFormatado.substring(12, 14)),
                idAtual)) {
            throw new DocumentoDuplicadoException("CNPJ", cnpj);
        }
    }

    private void validarEmailUnico(String email, Long idAtual) {
        if (pessoaRepository.existeEmail(email, idAtual)) {
            throw new DocumentoDuplicadoException("E-mail", email);
        }
    }
}
