package com.gft.clinica.controllers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gft.clinica.dtos.AtendimentoDTO;
import com.gft.clinica.dtos.ClienteDTO;
import com.gft.clinica.dtos.DadosCachorroDiaDTO;
import com.gft.clinica.dtos.EnderecoDTO;
import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Endereco;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.services.ClienteService;

@SpringBootTest
class ClienteControllerTest {

    // CLIENTE
    public static final Long ID = 1L;
    public static final String NOME_CLIENTE = "Poucas Trancas";
    public static final String EMAIL_CLIENTE = "poucas.trancas@email.com";
    public static final String EMAIL_ALTERADO = "alma.negra@email.com";
    public static final String SENHA = "Botina";
    public static final String TELEFONE = "4535-8677";
    // VETERINARIO
    public static final String NOME_VETERINARIO = "Tripa Seca";
    public static final String EMAIL_VETERINARIO = "quase.nada@email.com";
    public static final String CRMV = "123879";
    // ATENDIMENTO
    public static final String TITULO_CONSULTA = "Consulta";
    public static final String DESCRICAO_ATENDIMENTO = "Cachorro dancando";
    public static final String DIAGNOSTICO = "Euforico";
    public static final String COMENTARIOS = "Muito nervoso";
    // ENDERECO
    public static final String CEP = "12345-678";
    public static final String COMPLEMENTO = "Apto. 101";
    public static final String LOGRADOURO = "Rua dos Bobos";
    public static final String NUMERO = "123A";
    // CACHORRO
    public static final String NOME_CACHORRO = "Simpato";
    public static final String RACA_CACHORRO = "Amasaki";
    public static final double TAMANHO_CACHORRO = 15d;
    public static final String PORTE_CACHORRO = "Miniatura";
    public static final double PESO_CACHORRO = 2.0;
    public static final String MACHO = "Macho";
    public static final int IDADE_CACHORRO = 13;
    // VARIAVEIS
    private Perfil perfil2;
    private Endereco endereco;
    private Cachorro cachorro;
    private Cliente cliente;
    private Veterinario veterinario;
    private Atendimento atendimento;
    // DTOs
    private EnderecoDTO enderecoDTO;
    private ClienteDTO clienteDTO;
    private ClienteDTO clienteAtualizadoDTO;
    private AtendimentoDTO atendimentoDTO;
    private DadosCachorroDiaDTO dadosCachorroDiaDTO;
    // Pages
    private Pageable pageable;
    private Page<Cliente> clientePage;
    private Page<Atendimento> atendimentoPage;
    // Controllers, services e mapper
    @InjectMocks
    private ClienteController clienteController;
    @Mock
    private ClienteService clienteService;
    @Mock
    private ModelMapper mapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        iniciarCliente();
    }

    @Test
    void quandoCriarClienteDeveRetornarSucesso() {

        when(clienteService.salvarCliente(cliente)).thenReturn(cliente);
        when(mapper.map(clienteDTO, Cliente.class)).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

        ResponseEntity<ClienteDTO> resposta = clienteController.criarCliente(clienteDTO);

        assertAllParams(resposta, EMAIL_CLIENTE, ID);
    }

    @Test
    void quandoBuscarClientePeloIdDeveRetornarSucesso() {

        when(clienteService.buscarCliente(anyLong())).thenReturn(cliente);
        when(mapper.map(any(), any())).thenReturn(clienteDTO);

        ResponseEntity<ClienteDTO> resposta = clienteController.buscarCliente(ID);

        assertAllParams(resposta, EMAIL_CLIENTE, ID);
    }

    @Test
    void quandoListarTodosOsClientesDeveRetornarUmaPaginaDeClientes() {

        when(clienteService.listarTodosOsClientes(pageable)).thenReturn(clientePage);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

        ResponseEntity<Page<ClienteDTO>> resposta = clienteController.listarTodosOsClientes(pageable);

        assertAll(

                () -> assertNotNull(resposta), () -> assertNotNull(resposta.getBody()),
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(ResponseEntity.class, resposta.getClass()),
                () -> assertEquals(PageImpl.class, resposta.getBody().getClass()),
                () -> assertEquals(ClienteDTO.class, resposta.getBody().iterator().next().getClass()),
                () -> assertEquals(ID, resposta.getBody().iterator().next().getId()),
                () -> assertEquals(NOME_CLIENTE, resposta.getBody().iterator().next().getNome()),
                () -> assertEquals(EMAIL_CLIENTE, resposta.getBody().iterator().next().getEmail()),
                () -> assertEquals(SENHA, resposta.getBody().iterator().next().getSenha()),
                () -> assertEquals(TELEFONE, resposta.getBody().iterator().next().getTelefone()),
                () -> assertEquals(EnderecoDTO.class, resposta.getBody().iterator().next().getEndereco().getClass()),
                () -> assertEquals(null, resposta.getBody().iterator().next().getCachorros()),
                () -> assertEquals(List.of(perfil2).getClass(),
                        resposta.getBody().iterator().next().getPerfis().getClass()));
    }

    @Test
    void quandoAtualizarEmailDoClienteDeveRetornarSucesso() {
        when(clienteService.atualizarCliente(cliente, ID)).thenReturn(cliente);
        when(mapper.map(clienteAtualizadoDTO, Cliente.class)).thenReturn(cliente);
        when(mapper.map(cliente, ClienteDTO.class)).thenReturn(clienteAtualizadoDTO);

        ResponseEntity<ClienteDTO> resposta = clienteController.atualizarCliente(clienteAtualizadoDTO, ID);

        assertAllParams(resposta, EMAIL_ALTERADO, ID);
    }

    @Test
    void quandoExcluirClienteDeveRetornarSucesso() {
        doNothing().when(clienteService).excluirCliente(anyLong());

        ResponseEntity<ClienteDTO> resposta = clienteController.excluirCliente(ID);

        assertAll(
                () -> assertNotNull(resposta), () -> assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode()),
                () -> assertEquals(ResponseEntity.class, resposta.getClass()),
                () -> verify(clienteService, times(1)).excluirCliente(anyLong()));
    }

    @Test
    void listarTodosOsAtendimentosDoCliente() {

        when(clienteService.listarTodosOsAtendimentosDoCliente(anyLong(), any())).thenReturn(atendimentoPage);
        when(mapper.map(any(), any())).thenReturn(atendimentoDTO);

        ResponseEntity<Page<AtendimentoDTO>> resposta = clienteController.listarTodosOsAtendimentosDoCliente(ID,
                pageable);

        assertAll(

                () -> assertNotNull(resposta), () -> assertNotNull(resposta.getBody()),
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(ResponseEntity.class, resposta.getClass()),
                () -> assertEquals(PageImpl.class, resposta.getBody().getClass()),
                () -> assertEquals(AtendimentoDTO.class, resposta.getBody().iterator().next().getClass()),
                () -> assertEquals(ID, resposta.getBody().iterator().next().getId()),
                () -> assertEquals(TITULO_CONSULTA, resposta.getBody().iterator().next().getTitulo()),
                () -> assertEquals(DESCRICAO_ATENDIMENTO,
                        resposta.getBody().iterator().next().getDescricaoAtendimento()),
                () -> assertEquals(DIAGNOSTICO, resposta.getBody().iterator().next().getDiagnostico()),
                () -> assertEquals(COMENTARIOS, resposta.getBody().iterator().next().getComentarios()),
                () -> assertEquals(NOME_CLIENTE, resposta.getBody().iterator().next().getNomeCliente()),
                () -> assertEquals(NOME_CACHORRO, resposta.getBody().iterator().next().getNomeCachorro()),
                () -> assertEquals(NOME_VETERINARIO, resposta.getBody().iterator().next().getNomeVeterinario()),
                () -> assertEquals(NOME_CACHORRO, resposta.getBody().iterator().next().getDadosCachorroDia().getNome()),
                () -> assertEquals(IDADE_CACHORRO,
                        resposta.getBody().iterator().next().getDadosCachorroDia().getIdade()),
                () -> assertEquals(PESO_CACHORRO, resposta.getBody().iterator().next().getDadosCachorroDia().getPeso()),
                () -> assertEquals(TAMANHO_CACHORRO,
                        resposta.getBody().iterator().next().getDadosCachorroDia().getTamanho())

        );
    }

    private void assertAllParams(ResponseEntity<ClienteDTO> resposta, String email, Long id) {
        assertAll(

                () -> assertNotNull(resposta), () -> assertNotNull(resposta.getBody()),
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(ResponseEntity.class, resposta.getClass()),
                () -> assertEquals(ClienteDTO.class, resposta.getBody().getClass()),
                () -> assertEquals(id, resposta.getBody().getId()),
                () -> assertEquals(NOME_CLIENTE, resposta.getBody().getNome()),
                () -> assertEquals(email, resposta.getBody().getEmail()),
                () -> assertEquals(SENHA, resposta.getBody().getSenha()),
                () -> assertEquals(TELEFONE, resposta.getBody().getTelefone()),
                () -> assertEquals(EnderecoDTO.class, resposta.getBody().getEndereco().getClass()),
                () -> assertEquals(null, resposta.getBody().getCachorros()),
                () -> assertEquals(List.of(perfil2).getClass(), resposta.getBody().getPerfis().getClass()));
    }

    private void iniciarCliente() {

        perfil2 = new Perfil(2L, "CLIENTE");

        endereco = Endereco.builder()
                .cep(CEP)
                .complemento(COMPLEMENTO)
                .logradouro(LOGRADOURO)
                .numero(NUMERO)
                .build();

        enderecoDTO = EnderecoDTO.builder()
                .cep(CEP)
                .complemento(COMPLEMENTO)
                .logradouro(LOGRADOURO)
                .numero(NUMERO)
                .build();

        cliente = Cliente.builder()
                .id(ID)
                .nome(NOME_CLIENTE)
                .email(EMAIL_CLIENTE)
                .senha(SENHA)
                .telefone(TELEFONE)
                .endereco(endereco)
                .perfis(List.of(perfil2))
                .build();

        clienteDTO = ClienteDTO.builder()
                .id(ID)
                .nome(NOME_CLIENTE)
                .email(EMAIL_CLIENTE)
                .senha(SENHA)
                .telefone(TELEFONE)
                .endereco(enderecoDTO)
                .perfis(List.of(perfil2))
                .build();

        clienteAtualizadoDTO = ClienteDTO.builder()
                .id(ID)
                .nome(NOME_CLIENTE)
                .email(EMAIL_ALTERADO)
                .senha(SENHA)
                .telefone(TELEFONE)
                .endereco(enderecoDTO)
                .perfis(List.of(perfil2))
                .build();

        cachorro = Cachorro.builder()
                .id(ID)
                .nome(NOME_CACHORRO)
                .idade(8)
                .raca(RACA_CACHORRO)
                .tamanho(TAMANHO_CACHORRO)
                .porte(PORTE_CACHORRO)
                .peso(PESO_CACHORRO)
                .sexo(MACHO)
                .cliente(cliente)
                .build();

        veterinario = Veterinario.builder()
                .id(ID)
                .nome(NOME_VETERINARIO)
                .email(EMAIL_VETERINARIO)
                .telefone(TELEFONE)
                .crmv(CRMV)
                .atendimentos(null)
                .senha(SENHA)
                .perfis(List.of(perfil2))
                .build();

        atendimento = Atendimento.builder()
                .id(1L)
                .cliente(cliente)
                .cachorro(cachorro)
                .veterinario(veterinario)
                .titulo(TITULO_CONSULTA)
                .descricaoAtendimento(DESCRICAO_ATENDIMENTO)
                .diagnostico(DIAGNOSTICO)
                .comentarios(COMENTARIOS)
                .build();

        dadosCachorroDiaDTO = DadosCachorroDiaDTO.builder()
                .idade(IDADE_CACHORRO)
                .nome(NOME_CACHORRO)
                .peso(PESO_CACHORRO)
                .tamanho(TAMANHO_CACHORRO)
                .build();

        atendimentoDTO = AtendimentoDTO.builder()
                .id(ID)
                .titulo(TITULO_CONSULTA)
                .descricaoAtendimento(DESCRICAO_ATENDIMENTO)
                .diagnostico(DIAGNOSTICO)
                .comentarios(COMENTARIOS)
                .nomeVeterinario(veterinario.getNome())
                .nomeCliente(cliente.getNome())
                .nomeCachorro(cachorro.getNome())
                .dadosCachorroDia(dadosCachorroDiaDTO)
                .build();

        pageable = PageRequest.of(0, 10);

        clientePage = new PageImpl<>(List.of(cliente));

        atendimentoPage = new PageImpl<>(List.of(atendimento));
    }
}