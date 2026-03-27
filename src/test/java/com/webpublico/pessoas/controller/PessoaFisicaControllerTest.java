package com.webpublico.pessoas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webpublico.pessoas.dto.request.PessoaFisicaRequest;
import com.webpublico.pessoas.dto.response.PessoaFisicaResponse;
import com.webpublico.pessoas.exception.DocumentoDuplicadoException;
import com.webpublico.pessoas.exception.PessoaNaoEncontradaException;
import com.webpublico.pessoas.service.PessoaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada web para PessoaFisicaController.
 *
 * @WebMvcTest carrega apenas o contexto MVC (controllers, filters, handlers)
 * sem inicializar JPA, datasource ou outros beans — tornando os testes
 * rápidos e focados no comportamento HTTP.
 *
 * MockMvc permite simular requisições HTTP e verificar respostas
 * sem precisar de um servidor HTTP real.
 */
@WebMvcTest(controllers = {PessoaFisicaController.class, PessoaController.class})
@DisplayName("PessoaFisicaController")
class PessoaFisicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PessoaService pessoaService;

    private PessoaFisicaRequest requestValido;
    private PessoaFisicaResponse responseEsperado;

    @BeforeEach
    void setUp() {
        requestValido = new PessoaFisicaRequest();
        requestValido.setNome("João Silva");
        requestValido.setCpf("529.982.247-25");
        requestValido.setEmail("joao@email.com");
        requestValido.setEnderecos(Collections.emptyList());

        responseEsperado = PessoaFisicaResponse.builder()
                .id(1L)
                .tipo("FISICA")
                .nome("João Silva")
                .cpf("529.982.247-25")
                .email("joao@email.com")
                .enderecos(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("POST /pessoas/fisicas")
    class Post {

        @Test
        @DisplayName("deve retornar 201 Created com Location header ao cadastrar com sucesso")
        void deveRetornar201QuandoCadastradoComSucesso() throws Exception {
            when(pessoaService.cadastrarPessoaFisica(any())).thenReturn(responseEsperado);

            mockMvc.perform(post("/pessoas/fisicas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.tipo").value("FISICA"))
                    .andExpect(jsonPath("$.nome").value("João Silva"));
        }

        @Test
        @DisplayName("deve retornar 422 quando nome está em branco")
        void deveRetornar422QuandoNomeEmBranco() throws Exception {
            requestValido.setNome("");

            mockMvc.perform(post("/pessoas/fisicas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.campos[0].campo").value("nome"));
        }

        @Test
        @DisplayName("deve retornar 422 quando CPF é inválido")
        void deveRetornar422QuandoCpfInvalido() throws Exception {
            requestValido.setCpf("111.111.111-11"); // CPF inválido (dígito verificador errado)

            mockMvc.perform(post("/pessoas/fisicas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.campos[0].campo").value("cpf"));
        }

        @Test
        @DisplayName("deve retornar 422 quando e-mail é inválido")
        void deveRetornar422QuandoEmailInvalido() throws Exception {
            requestValido.setEmail("email-invalido");

            mockMvc.perform(post("/pessoas/fisicas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.campos[0].campo").value("email"));
        }

        @Test
        @DisplayName("deve retornar 409 quando CPF já está cadastrado")
        void deveRetornar409QuandoCpfDuplicado() throws Exception {
            when(pessoaService.cadastrarPessoaFisica(any()))
                    .thenThrow(new DocumentoDuplicadoException("CPF", "529.982.247-25"));

            mockMvc.perform(post("/pessoas/fisicas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.mensagem").value("CPF '529.982.247-25' já está cadastrado."));
        }
    }

    @Nested
    @DisplayName("GET /pessoas/fisicas/{id}")
    class Get {

        @Test
        @DisplayName("deve retornar 200 com a pessoa quando encontrada")
        void deveRetornar200QuandoEncontrada() throws Exception {
            when(pessoaService.buscarPessoaFisica(1L)).thenReturn(responseEsperado);

            mockMvc.perform(get("/pessoas/fisicas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nome").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"));
        }

        @Test
        @DisplayName("deve retornar 404 quando pessoa não encontrada")
        void deveRetornar404QuandoNaoEncontrada() throws Exception {
            when(pessoaService.buscarPessoaFisica(99L))
                    .thenThrow(new PessoaNaoEncontradaException(99L));

            mockMvc.perform(get("/pessoas/fisicas/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("DELETE /pessoas/fisicas/{id}")
    class Delete {

        @Test
        @DisplayName("deve retornar 204 No Content ao remover com sucesso")
        void deveRetornar204AoRemoverComSucesso() throws Exception {
            mockMvc.perform(delete("/pessoas/fisicas/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("deve retornar 404 quando pessoa não encontrada")
        void deveRetornar404QuandoNaoEncontrada() throws Exception {
            doThrow(new PessoaNaoEncontradaException(99L))
                    .when(pessoaService).remover(99L);

            mockMvc.perform(delete("/pessoas/fisicas/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /pessoas/fisicas/{id}")
    class Put {

        @Test
        @DisplayName("deve retornar 200 ao atualizar com sucesso")
        void deveRetornar200AoAtualizarComSucesso() throws Exception {
            when(pessoaService.atualizarPessoaFisica(eq(1L), any())).thenReturn(responseEsperado);

            mockMvc.perform(put("/pessoas/fisicas/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));
        }
    }
}
