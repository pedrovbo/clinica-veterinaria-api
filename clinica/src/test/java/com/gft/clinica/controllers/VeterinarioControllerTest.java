package com.gft.clinica.controllers;

import com.gft.clinica.dtos.AtendimentoDTO;
import com.gft.clinica.dtos.DadosCachorroDiaDTO;
import com.gft.clinica.dtos.VeterinarioDTO;
import com.gft.clinica.entities.*;
import com.gft.clinica.services.VeterinarioService;
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

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class VeterinarioControllerTest {

    private static final long ID_INEXISTENTE = 10L;

    private static final String VETERINARIO_NAO_ENCONTRADO = "Verifique os parâmetros da requisição";

    private static final Long ID = 1L;

    //VETERINARIO
    private static final String NOME = "Doctor";
    private static final String EMAIL = "teste@test.com";
    private static final String SENHA = "123456";
    private static final String TELEFONE = "123456789";
    private static final String CRMV = "123456789";
    private static final List<Perfil> PERFIS = List.of(new Perfil(3L, "VETERINARIO"));
    private static final List<Perfil> PERFILCLIENTE = List.of(new Perfil(2L, "CLIENTE"));

    // ATENDIMENTO
    public static final String TITULO_CONSULTA = "Consulta";
    public static final String DESCRICAO_ATENDIMENTO = "Cachorro dancando";
    public static final String DIAGNOSTICO = "Euforico";
    public static final String COMENTARIOS = "Muito nervoso";
    // CACHORRO
    public static final String NOME_CACHORRO = "Simpato";
    public static final String RACA_CACHORRO = "Amasaki";
    public static final double TAMANHO_CACHORRO = 15d;
    public static final String PORTE_CACHORRO = "Miniatura";
    public static final double PESO_CACHORRO = 2.0;
    public static final String MACHO = "Macho";
    public static final int IDADE_CACHORRO = 13;

    // CLIENTE
    public static final String NOME_CLIENTE = "Poucas Trancas";
    public static final String EMAIL_CLIENTE = "poucas.trancas@email.com";


    @InjectMocks
    private VeterinarioController veterinarioController;

    @Mock
    private VeterinarioService veterinarioService;

    @Mock
    private ModelMapper modelMapper;


    //VARIAVEIS
    private Veterinario veterinario;

    private DadosCachorroDiaDTO dadosCachorroDiaDTO;

    private Atendimento atendimento;

    private Cliente cliente;

    private Cachorro cachorro;

    //DTO
    private AtendimentoDTO atendimentoDTO;

    private VeterinarioDTO veterinarioDTO;

    //PAGEABLE
    private Page<Veterinario> page;

    private Page<Atendimento> atendimentoPage;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        iniciarVeterinario();
    }

    @Test
    void quandoSalvarVeterinarioDeveRetornarSucesso() {
        when(veterinarioService.salvarVeterinario(any())).thenReturn(veterinario);
        when(modelMapper.map(veterinario, VeterinarioDTO.class)).thenReturn(veterinarioDTO);
        ResponseEntity<VeterinarioDTO> response = veterinarioController.salvarVeterinario(veterinarioDTO);

        assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertTrue(response.getBody().getId().equals(ID)),
                () -> assertTrue(response.getBody().getNome().equals(NOME)),
                () -> assertTrue(response.getBody().getEmail().equals(EMAIL)),
                () -> assertTrue(response.getBody().getSenha().equals(SENHA)),
                () -> assertTrue(response.getBody().getTelefone().equals(TELEFONE)),
                () -> assertTrue(response.getBody().getCrmv().equals(CRMV)),
                () -> assertTrue(response.getBody().getPerfis().get(0).getId().equals(PERFIS.get(0).getId()))
        );
    }

    @Test
    void quandoBuscarVeterinarioDeveRetornarSucesso() {
        when(veterinarioService.buscarVeterinario(anyLong())).thenReturn(veterinario);
        when(modelMapper.map(veterinario, VeterinarioDTO.class)).thenReturn(veterinarioDTO);

        ResponseEntity<VeterinarioDTO> response = veterinarioController.buscarVeterinario(ID);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(VeterinarioDTO.class, response.getBody().getClass());

        assertEquals(ID, response.getBody().getId());
        assertEquals(NOME, response.getBody().getNome());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(SENHA, response.getBody().getSenha());
        assertEquals(TELEFONE, response.getBody().getTelefone());
        assertEquals(CRMV, response.getBody().getCrmv());
        assertEquals(PERFIS, response.getBody().getPerfis());

    }

    @Test
    void quandoBuscarVeterinarioENaoEncontrarDeveRetornarVeterinarioNaoEncontrado() {
        when(veterinarioService.buscarVeterinario(ID_INEXISTENTE))
                .thenThrow(new EntityNotFoundException(VETERINARIO_NAO_ENCONTRADO));

        try {
            veterinarioController.buscarVeterinario(ID_INEXISTENTE);

        } catch (Exception e) {
            assertAll(
                    () -> assertTrue(e instanceof EntityNotFoundException),
                    () -> assertTrue(e.getMessage().equals(VETERINARIO_NAO_ENCONTRADO)),
                    () -> assertTrue(e.getClass().equals(EntityNotFoundException.class)));
        }

    }

    @Test
    void quandListarTodosOsVeterinariosDeveRetornarSucesso() {
        when(veterinarioService.listarTodosOsVeterinarios(pageable)).thenReturn(page);
        when(modelMapper.map(veterinario, VeterinarioDTO.class)).thenReturn(veterinarioDTO);

        ResponseEntity<Page<VeterinarioDTO>> response = veterinarioController.buscarTodosOsVeterinarios(pageable);

        assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody().getContent().size() == 1),
                () -> assertTrue(response.getBody().getContent().get(0).getId() == ID),
                () -> assertTrue(response.getBody().getContent().get(0).getNome().equals(NOME)),
                () -> assertTrue(response.getBody().getContent().get(0).getEmail().equals(EMAIL)),
                () -> assertTrue(response.getBody().getContent().get(0).getSenha().equals(SENHA)),
                () -> assertTrue(response.getBody().getContent().get(0).getTelefone().equals(TELEFONE)),
                () -> assertTrue(response.getBody().getContent().get(0).getCrmv().equals(CRMV)),
                () -> assertTrue(response.getBody().getContent().get(0).getPerfis().equals(PERFIS))
        );

    }

    @Test
    void quandoAtualizarVeterinarioDeveRetornarSucesso() {
        when(veterinarioService.atualizarVeterinario(veterinario, ID))
                .thenReturn(veterinario);
        when(modelMapper.map(veterinarioDTO, Veterinario.class)).thenReturn(veterinario);
        when(modelMapper.map(veterinario, VeterinarioDTO.class)).thenReturn(veterinarioDTO);
        // when(modelMapper.map(any(), any())).thenReturn(atendimentoDTO);

        ResponseEntity<VeterinarioDTO> response = veterinarioController.atualizarVeterinario(veterinarioDTO, ID);

        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());

        assertEquals(ID, response.getBody().getId());
        assertEquals(NOME, response.getBody().getNome());
        assertEquals(EMAIL, response.getBody().getEmail());
        assertEquals(SENHA, response.getBody().getSenha());
        assertEquals(TELEFONE, response.getBody().getTelefone());
        assertEquals(CRMV, response.getBody().getCrmv());

    }

    @Test
    void quandoAtualizarVeterinarioComIdInexistenteRetornarVeterianrioNaoEncontrado() {

        when(veterinarioService.atualizarVeterinario(veterinario, ID_INEXISTENTE))
                .thenThrow(new EntityNotFoundException(VETERINARIO_NAO_ENCONTRADO));
        when(modelMapper.map(veterinarioDTO, Veterinario.class)).thenReturn(veterinario);
        when(modelMapper.map(veterinario, VeterinarioDTO.class)).thenReturn(veterinarioDTO);

        assertThrows(EntityNotFoundException.class,
                () -> veterinarioController.atualizarVeterinario(veterinarioDTO, ID_INEXISTENTE));

    }

    @Test
    void quandoExcluirVeterianarioDeveRetornarNOContent() {
        doNothing().when(veterinarioService).excluirVeterinario(ID);
        when(modelMapper.map(veterinario, VeterinarioDTO.class)).thenReturn(veterinarioDTO);

        ResponseEntity<VeterinarioDTO> response = veterinarioController.excluirVeterianario(ID);

        assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertEquals(ResponseEntity.class, response.getClass()),
                () -> assertTrue(null == response.getBody()),
                () -> assertTrue(response.getStatusCode().equals(HttpStatus.NO_CONTENT)));

    }

    @Test
    void listarTodosOsAtendimentosDoVeterinario() {
        when(veterinarioService.listarTodosOsAtendimentosDoVeterinario(anyLong(), any())).thenReturn(atendimentoPage);
        when(modelMapper.map(any(), any())).thenReturn(atendimentoDTO);

        ResponseEntity<Page<AtendimentoDTO>> resposta = veterinarioController.listarTodosOsAtendimentosDoVeterinario(ID,
                pageable);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertNotNull(resposta.getBody()),
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
                () -> assertEquals(NOME, resposta.getBody().iterator().next().getNomeVeterinario()),
                () -> assertEquals(NOME_CACHORRO, resposta.getBody().iterator().next().getDadosCachorroDia().getNome()),
                () -> assertEquals(IDADE_CACHORRO,
                        resposta.getBody().iterator().next().getDadosCachorroDia().getIdade()),
                () -> assertEquals(PESO_CACHORRO, resposta.getBody().iterator().next().getDadosCachorroDia().getPeso()),
                () -> assertEquals(TAMANHO_CACHORRO,
                        resposta.getBody().iterator().next().getDadosCachorroDia().getTamanho())

        );


    }

    private void iniciarVeterinario() {
        veterinario = Veterinario.builder()
                .id(ID)
                .nome(NOME)
                .email(EMAIL)
                .senha(SENHA)
                .telefone(TELEFONE)
                .crmv(CRMV)
                .perfis(PERFIS).build();

        veterinarioDTO = VeterinarioDTO.builder()
                .id(ID)
                .nome(NOME)
                .email(EMAIL)
                .senha(SENHA)
                .telefone(TELEFONE)
                .crmv(CRMV)
                .perfis(PERFIS).build();


        cliente = Cliente.builder()
                .id(ID)
                .nome(NOME_CLIENTE)
                .email(EMAIL_CLIENTE)
                .senha(SENHA)
                .telefone(TELEFONE)
                .perfis(PERFILCLIENTE).build();


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
                // .cliente(cliente)
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
        page = new PageImpl<>(List.of(veterinario));
        atendimentoPage = new PageImpl<>(List.of(atendimento));

    }
}
