package com.gft.clinica.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.DadosCachorroDia;
import com.gft.clinica.entities.Endereco;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.repositories.AtendimentoRepository;
import com.gft.clinica.repositories.CachorroRepository;
import com.gft.clinica.repositories.ClienteRepository;
import com.gft.clinica.repositories.VeterinarioRepository;

@SpringBootTest
class AtendimentoServiceTest {

    public static final String ENTITY_NOT_FOUND_EXCEPTION = "Verifique os parâmetros da requisição";
    @InjectMocks
    private AtendimentoService atendimentoService;

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private CachorroRepository cachorroRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    private Pageable pageable;
    private Page<Atendimento> pageAtendimento;

    private Atendimento atendimento;
    private Veterinario veterinario;
    private DadosCachorroDia dadosCachorroDia;
    private Cachorro cachorro;
    private Cliente cliente;
    private Endereco endereco1;
    private Endereco endereco2;
    private Perfil perfil2;
    private Perfil perfil3;
    private static Optional<Atendimento> optionalAtendimento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inicializandoAtendimento();
    }

    @Test
    void quandoSalvarAtendimentoRetornaSucesso() {

        when(atendimentoRepository.save(any())).thenReturn(atendimento);
        when(atendimentoRepository.findById(anyLong())).thenReturn(optionalAtendimento);

        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.of(veterinario));
        when(cachorroRepository.findById(anyLong())).thenReturn(Optional.of(cachorro));
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));

        Atendimento resposta = atendimentoService.salvarAtendimento(atendimento, 1L, 1L, 1L);

        assertAll(
                () -> assertNotNull(resposta),
                () -> assertEquals(Atendimento.class, resposta.getClass()),
                () -> assertEquals(1L, resposta.getId()),
                () -> assertEquals(veterinario, resposta.getVeterinario()),
                () -> assertEquals(dadosCachorroDia, resposta.getDadosCachorroDia()),
                () -> assertEquals(cachorro, resposta.getCachorro()),
                () -> assertEquals(cliente, resposta.getCliente()));

    }

    @Test
    void deveRetornarEntityNotFoundExceptionCasoAtendimentoSejaSalvoComVeterinarioInexistente() {

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(cachorroRepository.findById(anyLong())).thenReturn(Optional.of(cachorro));

        when(veterinarioRepository.findById(1L))
                .thenThrow(new NoSuchElementException(ENTITY_NOT_FOUND_EXCEPTION));

        assertThrows(NoSuchElementException.class,
                () -> atendimentoService.salvarAtendimento(atendimento, 1L, 1L, 1L));

    }

    @Test
    void deveRetornarEntityNotFoundExceptionCasoAtendimentoSejaSalvoComClienteInexistente() {

        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.of(veterinario));

        when(clienteRepository.findById(10L))
                .thenThrow(new NoSuchElementException(ENTITY_NOT_FOUND_EXCEPTION));

        assertThrows(NoSuchElementException.class,
                () -> atendimentoService.salvarAtendimento(atendimento, 10L, 10L, 10L));

    }

    @Test
    void deveRetornarEntityNotFoundExceptionCasoAtendimentoSejaSalvoComACachorroInexistente() {

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));

        when(cachorroRepository.findById(10L))
                .thenThrow(new NoSuchElementException(ENTITY_NOT_FOUND_EXCEPTION));

        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.of(veterinario));

        assertThrows(NoSuchElementException.class,
                () -> atendimentoService.salvarAtendimento(atendimento, 10L, 10L, 10L));

    }

    @Test
    void quandoBuscarUmAtendimentoDeveRetornarUmaInstanciaDeAtendimento() {
        when(atendimentoRepository.findById(anyLong())).thenReturn(optionalAtendimento);

        Atendimento response = atendimentoService.buscarAtendimento(1L);

        assertNotNull(response);

        assertEquals(Atendimento.class, response.getClass());
        assertEquals(1, response.getId());
        assertEquals(veterinario, response.getVeterinario());
        assertEquals(dadosCachorroDia, response.getDadosCachorroDia());
        assertEquals(cachorro, response.getCachorro());
        assertEquals(cliente, response.getCliente());
    }

    @Test
    void QuandoBuscarUmAtendimentoDeveRetornarEntityNotFoundException() {

        when(atendimentoRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> atendimentoService.buscarAtendimento(1L));

        try {
            atendimentoService.buscarAtendimento(1L);
        } catch (Exception ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
            assertEquals("Atendimento não encontrado", ex.getMessage());
        }
    }

    @Test
    void quandoAtualizarRetornaAtendimentoAtualizadoComSucesso() {

        when(atendimentoRepository.save(any())).thenReturn(atendimento);
        when(atendimentoRepository.findById(anyLong())).thenReturn(optionalAtendimento);

        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.of(veterinario));
        when(cachorroRepository.findById(anyLong())).thenReturn(Optional.of(cachorro));
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));

        Atendimento resposta = atendimentoService.atualizarAtendimento(atendimento, 1L, 1L, 1L, 1L);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(Atendimento.class, resposta.getClass()),
                () -> assertEquals(1L, resposta.getId()),
                () -> assertEquals(veterinario, resposta.getVeterinario()),
                () -> assertEquals(dadosCachorroDia, resposta.getDadosCachorroDia()),
                () -> assertEquals(cachorro, resposta.getCachorro()),
                () -> assertEquals(cliente, resposta.getCliente()));

    }

    @Test
    void quandoAtualizarEOAtendimentoNaoExistirRetornarEntityNotFoundException() {

        when(atendimentoRepository.save(any())).thenReturn(atendimento);
        when(atendimentoRepository.findById(1L)).thenReturn(optionalAtendimento);

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(cachorroRepository.findById(anyLong())).thenReturn(Optional.of(cachorro));
        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.of(veterinario));

        assertThrows(EntityNotFoundException.class,
                () -> atendimentoService.atualizarAtendimento(atendimento, 10L, 1L, 1L, 1L));

    }

    @Test
    void quandoListarTodosAtendimentosRetornarUmaPageDeAtendimentos() {

        when(atendimentoRepository.findAll(pageable)).thenReturn(pageAtendimento);

        Page<Atendimento> resposta = atendimentoService.listarTodosAtendimentos(pageable);

        assertEquals(Atendimento.class, resposta.iterator().next().getClass());

        assertAll(

                () -> assertEquals(1, resposta.getSize()),
                () -> assertNotNull(resposta),
                () -> assertEquals(1, resposta.getSize()),
                () -> assertEquals(Atendimento.class, resposta.iterator().next().getClass()),
                () -> assertEquals(1, resposta.iterator().next().getId()),
                () -> assertEquals(veterinario, resposta.iterator().next().getVeterinario()),
                () -> assertEquals(dadosCachorroDia, resposta.iterator().next().getDadosCachorroDia()),
                () -> assertEquals(cachorro, resposta.iterator().next().getCachorro()),
                () -> assertEquals(cliente, resposta.iterator().next().getCliente()));

    }

    @Test
    void excluindoAtendimentoComSucesso() {

        when(atendimentoRepository.findById(anyLong())).thenReturn(optionalAtendimento);
        doNothing().when(atendimentoRepository).delete(any());

        atendimentoService.excluirAtendimento(1L);

        verify(atendimentoRepository, times(1)).delete(any());

    }

    @Test
    void quandoNaoEncontrarAtendimentoParaExcluirDeveRetornarException() {

        when(atendimentoRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION));
        try {
            atendimentoService.excluirAtendimento(1L);
        } catch (Exception ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
            assertEquals(ENTITY_NOT_FOUND_EXCEPTION, ex.getMessage());
        }
    }

    private void inicializandoAtendimento() {
        inicializandoPerfis();
        inicializandoEnderecos();
        inicializandoCliente();
        inicializandoVeterinario();
        inicializandoCachorro();
        inicializandoDadosCachorroDia();

        atendimento = Atendimento.builder()
                .id(1L)
                .cliente(cliente)
                .cachorro(cachorro)
                .veterinario(veterinario)
                .titulo("Consulta")
                .dadosCachorroDia(dadosCachorroDia)
                .descricaoAtendimento("Cachorro mancando")
                .diagnostico("Fratura pata esquerda")
                .comentarios("Muito nervoso")
                .build();

        optionalAtendimento = Optional.of(Atendimento.builder()
                .id(1L)
                .cliente(cliente)
                .cachorro(cachorro)
                .veterinario(veterinario)
                .titulo("Consulta")
                .dadosCachorroDia(dadosCachorroDia)
                .descricaoAtendimento("Cachorro mancando")
                .diagnostico("Fratura pata esquerda")
                .comentarios("Muito nervoso")
                .build());

        pageAtendimento = new PageImpl<>(List.of(atendimento));
    }

    private void inicializandoDadosCachorroDia() {
        dadosCachorroDia = DadosCachorroDia.builder()
                .nome("Totó")
                .idade(8)
                .tamanho(15d)
                .peso(2.0)
                .build();
    }

    private void inicializandoCachorro() {
        cachorro = Cachorro.builder()
        		.id(1L)
        		.nome("Totó")
                .idade(8)
                .raca("Affenpinscher")
                .tamanho(15d)
                .porte("Miniatura")
                .peso(2.0)
                .sexo("Macho")
                .cliente(cliente)
                .build();
    }

    private void inicializandoVeterinario() {
        veterinario = Veterinario.builder()
        		.id(1L)
                .crmv("BR123")
                .nome("João")
                .email("joao@email.com")
                .senha("123")
                .telefone("123456")
                .endereco(endereco2)
                .perfis(List.of(perfil3))
                .build();
    }

    private void inicializandoCliente() {
        cliente = Cliente.builder()
        		.id(1L)                
                .nome("João")
                .email("joaograndao@email.com")
                .senha("123")
                .telefone("123456")
                .endereco(endereco1)
                .perfis(List.of(perfil2))
                .build();
    }

    private void inicializandoEnderecos() {
        endereco1 = Endereco.builder()
                .cep("12345-678")
                .complemento("Apto. 101")
                .logradouro("Rua dos Bobos")
                .numero("123A")
                .build();
        endereco2 = Endereco.builder()
                .cep("12345-679")
                .complemento("Casa 02")
                .logradouro("Avenida Palmas")
                .numero("123A")
                .build();
     }

    private void inicializandoPerfis() {
        perfil2 = new Perfil(null, "CLIENTE");
        perfil3 = new Perfil(null, "VETERINARIO");
    }
}