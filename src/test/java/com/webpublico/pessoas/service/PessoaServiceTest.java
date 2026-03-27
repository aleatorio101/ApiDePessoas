package com.webpublico.pessoas.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("PessoaService")
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private PessoaMapper pessoaMapper;

    @InjectMocks
    private PessoaService pessoaService;

    private PessoaFisica pessoaFisica;
    private PessoaFisicaRequest pessoaFisicaRequest;
    private PessoaFisicaResponse pessoaFisicaResponse;

    private PessoaJuridica pessoaJuridica;
    private PessoaJuridicaRequest pessoaJuridicaRequest;
    private PessoaJuridicaResponse pessoaJuridicaResponse;

    @BeforeEach
    void setUp() {
        pessoaFisica = new PessoaFisica("João Silva", "123.456.789-09", "joao@email.com");
        setId(pessoaFisica, 1L);

        pessoaFisicaRequest = new PessoaFisicaRequest();
        pessoaFisicaRequest.setNome("João Silva");
        pessoaFisicaRequest.setCpf("123.456.789-09");
        pessoaFisicaRequest.setEmail("joao@email.com");

        pessoaFisicaResponse = PessoaFisicaResponse.builder()
                .id(1L).tipo("FISICA").nome("João Silva")
                .cpf("123.456.789-09").email("joao@email.com")
                .enderecos(Collections.emptyList()).build();

        pessoaJuridica = new PessoaJuridica("Tech Ltda", "12.345.678/0001-90", "tech@email.com");
        setId(pessoaJuridica, 2L);

        pessoaJuridicaRequest = new PessoaJuridicaRequest();
        pessoaJuridicaRequest.setRazaoSocial("Tech Ltda");
        pessoaJuridicaRequest.setCnpj("12.345.678/0001-90");
        pessoaJuridicaRequest.setEmail("tech@email.com");

        pessoaJuridicaResponse = PessoaJuridicaResponse.builder()
                .id(2L).tipo("JURIDICA").razaoSocial("Tech Ltda")
                .cnpj("12.345.678/0001-90").email("tech@email.com")
                .enderecos(Collections.emptyList()).build();
    }

    @Nested
    @DisplayName("cadastrarPessoaFisica")
    class CadastrarPessoaFisica {

        @Test
        @DisplayName("deve cadastrar com sucesso quando CPF e e-mail são únicos")
        void deveCadastrarComSucesso() {
            when(pessoaRepository.existeCpf(anyString(), isNull())).thenReturn(false);
            when(pessoaRepository.existeEmail(anyString(), isNull())).thenReturn(false);
            when(pessoaMapper.toEntity(pessoaFisicaRequest)).thenReturn(pessoaFisica);
            when(pessoaRepository.save(pessoaFisica)).thenReturn(pessoaFisica);
            when(pessoaMapper.toResponse(pessoaFisica)).thenReturn(pessoaFisicaResponse);

            PessoaFisicaResponse resultado = pessoaService.cadastrarPessoaFisica(pessoaFisicaRequest);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo("João Silva");
            assertThat(resultado.getCpf()).isEqualTo("123.456.789-09");
            verify(pessoaRepository, times(1)).save(pessoaFisica);
        }

        @Test
        @DisplayName("deve lançar DocumentoDuplicadoException quando CPF já existe")
        void deveLancarExcecaoQuandoCpfDuplicado() {
            when(pessoaRepository.existeCpf(anyString(), isNull())).thenReturn(true);

            assertThatThrownBy(() -> pessoaService.cadastrarPessoaFisica(pessoaFisicaRequest))
                    .isInstanceOf(DocumentoDuplicadoException.class)
                    .hasMessageContaining("CPF");

            verify(pessoaRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar DocumentoDuplicadoException quando e-mail já existe")
        void deveLancarExcecaoQuandoEmailDuplicado() {
            when(pessoaRepository.existeCpf(anyString(), isNull())).thenReturn(false);
            when(pessoaRepository.existeEmail(anyString(), isNull())).thenReturn(true);

            assertThatThrownBy(() -> pessoaService.cadastrarPessoaFisica(pessoaFisicaRequest))
                    .isInstanceOf(DocumentoDuplicadoException.class)
                    .hasMessageContaining("E-mail");

            verify(pessoaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPessoaFisica")
    class BuscarPessoaFisica {

        @Test
        @DisplayName("deve retornar a pessoa quando encontrada e for do tipo FISICA")
        void deveRetornarQuandoEncontrada() {
            when(pessoaRepository.buscarPorIdComEnderecos(1L))
                    .thenReturn(Optional.of(pessoaFisica));
            when(pessoaMapper.toResponse(pessoaFisica)).thenReturn(pessoaFisicaResponse);

            PessoaFisicaResponse resultado = pessoaService.buscarPessoaFisica(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar PessoaNaoEncontradaException quando ID não existe")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            when(pessoaRepository.buscarPorIdComEnderecos(99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> pessoaService.buscarPessoaFisica(99L))
                    .isInstanceOf(PessoaNaoEncontradaException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("deve lançar TipoPessoaIncompativelException quando ID é de PessoaJuridica")
        void deveLancarExcecaoQuandoTipoIncompativel() {
            when(pessoaRepository.buscarPorIdComEnderecos(2L))
                    .thenReturn(Optional.of(pessoaJuridica));

            assertThatThrownBy(() -> pessoaService.buscarPessoaFisica(2L))
                    .isInstanceOf(TipoPessoaIncompativelException.class)
                    .hasMessageContaining("FISICA");
        }
    }

    @Nested
    @DisplayName("cadastrarPessoaJuridica")
    class CadastrarPessoaJuridica {

        @Test
        @DisplayName("deve cadastrar com sucesso quando CNPJ e e-mail são únicos")
        void deveCadastrarComSucesso() {
            when(pessoaRepository.existeCnpj(anyString(), isNull())).thenReturn(false);
            when(pessoaRepository.existeEmail(anyString(), isNull())).thenReturn(false);
            when(pessoaMapper.toEntity(pessoaJuridicaRequest)).thenReturn(pessoaJuridica);
            when(pessoaRepository.save(pessoaJuridica)).thenReturn(pessoaJuridica);
            when(pessoaMapper.toResponse(pessoaJuridica)).thenReturn(pessoaJuridicaResponse);

            PessoaJuridicaResponse resultado = pessoaService.cadastrarPessoaJuridica(pessoaJuridicaRequest);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getRazaoSocial()).isEqualTo("Tech Ltda");
            verify(pessoaRepository, times(1)).save(pessoaJuridica);
        }

        @Test
        @DisplayName("deve lançar DocumentoDuplicadoException quando CNPJ já existe")
        void deveLancarExcecaoQuandoCnpjDuplicado() {
            when(pessoaRepository.existeCnpj(anyString(), isNull())).thenReturn(true);

            assertThatThrownBy(() -> pessoaService.cadastrarPessoaJuridica(pessoaJuridicaRequest))
                    .isInstanceOf(DocumentoDuplicadoException.class)
                    .hasMessageContaining("CNPJ");

            verify(pessoaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listarTodas")
    class ListarTodas {

        @Test
        @DisplayName("deve retornar lista com todas as pessoas")
        void deveRetornarListaComTodasAsPessoas() {
            PessoaSummaryResponse summaryFisica = PessoaSummaryResponse.builder()
                    .id(1L).tipo("FISICA").nomeOuRazaoSocial("João Silva").build();
            PessoaSummaryResponse summaryJuridica = PessoaSummaryResponse.builder()
                    .id(2L).tipo("JURIDICA").nomeOuRazaoSocial("Tech Ltda").build();

            when(pessoaRepository.listarTodas())
                    .thenReturn(Arrays.asList(pessoaFisica, pessoaJuridica));
            when(pessoaMapper.toSummary(pessoaFisica)).thenReturn(summaryFisica);
            when(pessoaMapper.toSummary(pessoaJuridica)).thenReturn(summaryJuridica);

            List<PessoaSummaryResponse> resultado = pessoaService.listarTodas();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).getTipo()).isEqualTo("FISICA");
            assertThat(resultado.get(1).getTipo()).isEqualTo("JURIDICA");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há pessoas cadastradas")
        void deveRetornarListaVaziaQuandoNaoHaPessoas() {
            when(pessoaRepository.listarTodas()).thenReturn(Collections.emptyList());

            List<PessoaSummaryResponse> resultado = pessoaService.listarTodas();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("deve remover com sucesso quando pessoa existe")
        void deveRemoverComSucesso() {
            when(pessoaRepository.existsById(1L)).thenReturn(true);
            doNothing().when(pessoaRepository).deleteById(1L);

            pessoaService.remover(1L);

            verify(pessoaRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar PessoaNaoEncontradaException quando ID não existe")
        void deveLancarExcecaoQuandoPessoaNaoExiste() {
            when(pessoaRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> pessoaService.remover(99L))
                    .isInstanceOf(PessoaNaoEncontradaException.class)
                    .hasMessageContaining("99");

            verify(pessoaRepository, never()).deleteById(anyLong());
        }
    }

    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao setar id via reflexão no teste", e);
        }
    }
}
