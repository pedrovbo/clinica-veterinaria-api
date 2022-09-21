package com.gft.clinica.controllers;

import com.gft.clinica.dtos.AtendimentoDTO;
import com.gft.clinica.entities.*;
import com.gft.clinica.services.AtendimentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AtendimentoControllerTest {

    private static final Long ID = 1L;
    private static final Long IDVET = 1L;
    private static final Long IDCLIENTE = 1L;
    private static final Long IDCACHORRO = 1L;
    private static final String TITULO = "Consulta";
    private static final String DESCRICAO = "Cachorro com espirrando";
    private static final String DIAGNOSTICO = "Gripe";
    private static final String COMENTARIOS = "Havia fluidos";
    private static final String NOMEVET = "Doutor cão";
    private static final String NOMECACHORRO = "Galak";
    private static final String NOMECLIENTE = "Alpino";

    private Pageable pageable;
    private Page<Atendimento> atendimentosPage;
    private Cliente cliente;
    private Cachorro cachorro1;
    private Veterinario veterinario1;

    private Endereco endereco1;

    private Perfil perfil2;

    @InjectMocks
    private AtendimentoController atendimentoController;
    @Mock
    private AtendimentoService atendimentoService;
    @Mock
    private AtendimentoDTO atendimentoDTO;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Atendimento atendimento;

    @Mock
    Page<AtendimentoDTO> atendimentoDTOPage;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        iniciarAtendimento();

    }

    @Test
    void whenFindByIdOfAtendimentoThenReturnSucess() {
        when(atendimentoService.buscarAtendimento(ID)).thenReturn(atendimento);
        when(modelMapper.map(any(), any())).thenReturn(atendimentoDTO);

        ResponseEntity<AtendimentoDTO> response = atendimentoController.buscarAtendimento(ID);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(AtendimentoDTO.class, response.getBody().getClass());

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(ResponseEntity.class, response.getClass()),
                () -> assertEquals(AtendimentoDTO.class, response.getBody().getClass()),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(ID, response.getBody().getId()),
                () -> assertEquals(TITULO, response.getBody().getTitulo()),
                () -> assertEquals(DESCRICAO, response.getBody().getDescricaoAtendimento()),
                () -> assertEquals(DIAGNOSTICO, response.getBody().getDiagnostico()),
                () -> assertEquals(COMENTARIOS, response.getBody().getComentarios()),
                () -> assertEquals(NOMEVET, response.getBody().getNomeVeterinario()),
                () -> assertEquals(NOMECACHORRO, response.getBody().getNomeCachorro()),
                () -> assertEquals(NOMECLIENTE, response.getBody().getNomeCliente()));

    }

    @Test
    void whenFindAllAtendimentosThenReturnAPageOfAtendimentosDTO() {
        when(atendimentoService.listarTodosAtendimentos(pageable)).thenReturn(atendimentosPage);
        when(modelMapper.map(any(), any())).thenReturn(atendimentoDTO);

        ResponseEntity<Page<AtendimentoDTO>> response = atendimentoController.buscarTodosAtendimentos(pageable);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(PageImpl.class, response.getBody().getClass());
        assertEquals(AtendimentoDTO.class, response.getBody().getContent().get(0).getClass());

        assertEquals(ID, response.getBody().getContent().get(0).getId());
        assertEquals(NOMEVET, response.getBody().getContent().get(0).getNomeVeterinario());
        assertEquals(NOMECLIENTE, response.getBody().getContent().get(0).getNomeCliente());
        assertEquals(NOMECACHORRO, response.getBody().getContent().get(0).getNomeCachorro());
        assertEquals(TITULO, response.getBody().getContent().get(0).getTitulo());
        assertEquals(DESCRICAO, response.getBody().getContent().get(0).getDescricaoAtendimento());
        assertEquals(DIAGNOSTICO, response.getBody().getContent().get(0).getDiagnostico());
        assertEquals(COMENTARIOS, response.getBody().getContent().get(0).getComentarios());
    }

    @Test
    void whenCreateThenReturnCreated() {
        when(atendimentoService.salvarAtendimento(atendimento, IDVET, IDCLIENTE, IDCACHORRO)).thenReturn(atendimento);
        when(modelMapper.map(atendimentoDTO, Atendimento.class)).thenReturn(atendimento);
        when(modelMapper.map(atendimento, AtendimentoDTO.class)).thenReturn(atendimentoDTO);

        ResponseEntity<AtendimentoDTO> response = atendimentoController.salvarAtendimento(IDVET, IDCLIENTE, IDCACHORRO,
                atendimentoDTO);

        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.OK, response.getStatusCode()); // maybe use the CREATED status code

    }

    @Test
    void whenUpdateThenReturnSucess() {
        when(atendimentoService.atualizarAtendimento(atendimento, ID, IDCLIENTE, IDCACHORRO, IDVET))
                .thenReturn(atendimento);
        when(modelMapper.map(atendimentoDTO, Atendimento.class)).thenReturn(atendimento);
        when(modelMapper.map(atendimento, AtendimentoDTO.class)).thenReturn(atendimentoDTO);
        // when(modelMapper.map(any(), any())).thenReturn(atendimentoDTO);

        ResponseEntity<AtendimentoDTO> response = atendimentoController.alterarAtendimento(atendimentoDTO, ID, IDVET,
                IDCLIENTE, IDCACHORRO);

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());

        assertEquals(ID, response.getBody().getId());
        assertEquals(TITULO, response.getBody().getTitulo());
        assertEquals(DESCRICAO, response.getBody().getDescricaoAtendimento());
        assertEquals(DIAGNOSTICO, response.getBody().getDiagnostico());
        assertEquals(COMENTARIOS, response.getBody().getComentarios());
        assertEquals(NOMEVET, response.getBody().getNomeVeterinario());
        assertEquals(NOMECLIENTE, response.getBody().getNomeCliente());
        assertEquals(NOMECACHORRO, response.getBody().getNomeCachorro());
    }

    @Test
    void whenDeleteThenReturnSuccess() {
        doNothing().when(atendimentoService).excluirAtendimento(ID);

        ResponseEntity<AtendimentoDTO> response = atendimentoController.excluirAtendimento(ID);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        verify(atendimentoService, times(1)).excluirAtendimento(ID);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    void iniciarAtendimento() {

        // Objetos auxiliares

        iniciarPerfis();
        iniciarEnderecos();
        iniciarCliente();
        iniciarVeterinario();
        iniciarCachorro();

        // iniciar atendimento
        iniciarPage();

        atendimento = Atendimento.builder()
                .id(ID)
                .titulo(TITULO)
                .descricaoAtendimento(DESCRICAO)
                .diagnostico(DIAGNOSTICO)
                .comentarios(COMENTARIOS)
                .veterinario(veterinario1)
                .cachorro(cachorro1)
                .cliente(cliente).build();

        atendimentoDTO = AtendimentoDTO.builder()
                .id(ID)
                .titulo(TITULO)
                .descricaoAtendimento(DESCRICAO)
                .diagnostico(DIAGNOSTICO)
                .comentarios(COMENTARIOS)
                .nomeVeterinario(NOMEVET)
                .nomeCachorro(NOMECACHORRO)
                .nomeCliente(NOMECLIENTE).build();

    }

    void iniciarPage() {
        atendimentosPage = new PageImpl<>(List.of(atendimento));
    }

    void iniciarPerfis() {
        perfil2 = new Perfil(2L, "VETERINARIO");
    }

    void iniciarEnderecos() {
        endereco1 = Endereco.builder()
                .logradouro("Rua dos Bobos")
                .numero("1")
                .cep("01234567")
                .build();
    }

    void iniciarCliente() {
        cliente = Cliente.builder()
                .id(IDCLIENTE)
                .nome(NOMECLIENTE)
                .email("gft@com")
                .telefone("123456789")
                .build();
    }

    void iniciarVeterinario() {

        veterinario1 = Veterinario.builder()
                .id(IDVET)
                .nome(NOMEVET)
                .endereco(endereco1)
                .perfis(List.of(perfil2))
                .build();

    }

    void iniciarCachorro() {
        cachorro1 = Cachorro.builder()
                .id(IDCACHORRO)
                .nome(NOMECACHORRO)
                .build();

    }

}