package com.gft.clinica.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.DadosCachorroDia;
import com.gft.clinica.entities.Endereco;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.exception.ClinicaException;
import com.gft.clinica.repositories.AtendimentoRepository;
import com.gft.clinica.repositories.ClienteRepository;

class ClienteServiceTest {

    private static final long ID_INEXISTENTE = 10L;
    public static final String OBRIGATORIO_TER_PERFIL_CLIENTE = "Obrigatorio ter perfil veterinario";
    public static final String EMAIL_JA_CADASTRADO = "Email já cadastrado";
    public static final String EMAIL_ALTERADO = "alma.negra@email.com";
    public static final String TITULO_CONSULTA = "Consulta";
    public static final String DESCRICAO_ATENDIMENTO = "Cachorro dancando";
    public static final String DIAGNOSTICO = "Euforico";
    public static final String COMENTARIOS = "Muito nervoso";
    public static final long ID = 1L;
    public static final String NOME = "João";
    public static final String EMAIL = "joaograndao@email.com";
    public static final String SENHA = "123";
    public static final String TELEFONE = "123456";
    public static final String CLIENTE_NAO_ENCONTRADO = "Cliente não encontrado";

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteService clienteServiceMock;

    @Mock
    private Cliente clienteMock;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private ValidationService validationService;

    @Mock
    private BCryptPasswordEncoder encoder;

    private Cliente cliente;
    private Cliente clienteAtualizado;
    private Optional<Cliente> optionalCliente;
    private Pageable pageable;
    private Page<Cliente> pageCliente;
    private Perfil perfil1;
    private Perfil perfil2;
    private Atendimento atendimento;
    private Veterinario veterinario;
    private DadosCachorroDia dadosCachorroDia;
    private Cachorro cachorro;
    private Endereco endereco1;
    private Endereco endereco2;
    private Perfil perfil3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inicializandoCliente();
    }

    @Test
    void quandoSalvarClienteDeveRetornarSucesso() {

        when(clienteRepository.save(any())).thenReturn(cliente);
        when(validationService.validacaoEmail(anyString())).thenReturn(cliente.getEmail());
        when(validationService.validacaoPerfilCliente(any(), anyList())).thenReturn(cliente);
        when(encoder.encode(anyString())).thenReturn(cliente.getSenha());

        Cliente resposta = clienteService.salvarCliente(cliente);

        verify(validationService, times(1)).validacaoEmail(EMAIL);
        verify(validationService, times(1)).validacaoPerfilCliente(any(), anyList());
        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(Cliente.class, resposta.getClass()),
                () -> assertEquals(ID, resposta.getId()),
                () -> assertEquals(NOME, resposta.getNome()),
                () -> assertEquals(EMAIL, resposta.getEmail()),
                () -> assertEquals(TELEFONE, resposta.getTelefone()),
                () -> assertEquals(SENHA, resposta.getSenha()),
                () -> assertEquals(endereco1, resposta.getEndereco()),
                () -> assertEquals(List.of(perfil2), resposta.getPerfis()));
    }

    @Test
    void quandoSalvarClienteComEmailCadastradoDeveRetornarClinicaException() {

        when(clienteRepository.save(any())).thenReturn(cliente);
        when(validationService.validacaoEmail(anyString()))
                .thenThrow(new ClinicaException(EMAIL_JA_CADASTRADO, HttpStatus.CONFLICT));

        try {
            cliente.setEmail(EMAIL);
            clienteService.salvarCliente(cliente);
        } catch (Exception e) {
            verify(validationService, times(1)).validacaoEmail(EMAIL);
            assertEquals(ClinicaException.class, e.getClass());
            assertEquals(EMAIL_JA_CADASTRADO, e.getMessage());
        }
    }

    @Test
    void quandoSalvarClienteComPerfisIguaisDeveRetornarClinicaException() {

        when(validationService.validacaoPerfilVeterinario(any(), anyList()))
                .thenThrow(new ClinicaException(OBRIGATORIO_TER_PERFIL_CLIENTE, HttpStatus.CONFLICT));
        when(clienteRepository.save(any())).thenReturn(cliente);

        try {
            cliente.setPerfis(List.of(perfil1, perfil2));
            clienteService.salvarCliente(cliente);
        } catch (Exception e) {
            verify(validationService, times(1)).validacaoPerfilVeterinario(any(), anyList());
            assertEquals(ClinicaException.class, e.getClass());
            assertEquals(OBRIGATORIO_TER_PERFIL_CLIENTE, e.getMessage());
        }
    }

    @Test
    void quandoBuscarClientePeloIdDeveRetornarUmaInstanciaDeCliente() {

        when(clienteRepository.findById(anyLong())).thenReturn(optionalCliente);

        Cliente resposta = clienteService.buscarCliente(ID);

        assertEquals(Cliente.class, resposta.getClass());
    }

    @Test
    void quandoBuscarVeterinarioPeloIdDeveRetornarEntityNotFoundException() {

        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarCliente(ID_INEXISTENTE));

        try {
            clienteService.buscarCliente(ID_INEXISTENTE);
        } catch (Exception e) {

            assertEquals(CLIENTE_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void quandoListarTodosOsClientesDeveRetornarUmaPaginaDeClientes() {

        when(clienteRepository.findAll(pageable)).thenReturn(pageCliente);

        Page<Cliente> resposta = clienteService.listarTodosOsClientes(pageable);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(1, resposta.getTotalElements()),
                () -> assertEquals(Cliente.class, resposta.iterator().next().getClass()),
                () -> assertEquals(ID, resposta.getContent().get(0).getId()),
                () -> assertEquals(NOME, resposta.getContent().get(0).getNome()),
                () -> assertEquals(EMAIL,
                        resposta.getContent().get(0).getEmail()),
                () -> assertEquals(TELEFONE, resposta.getContent().get(0).getTelefone()),
                () -> assertEquals(SENHA, resposta.getContent().get(0).getSenha()),
                () -> assertEquals(List.of(perfil1).getClass(),
                        resposta.getContent().get(0).getPerfis().getClass()));
    }

    @Test
    void quandoAtualizarEmailDoClientePeloIdDoVeterinarioDeveRetornarSucesso() {

        when(clienteRepository.save(any())).thenReturn(cliente);
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.ofNullable(cliente));

        Cliente resposta = clienteService.atualizarCliente(clienteAtualizado, ID);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(Cliente.class, resposta.getClass()),
                () -> assertEquals(ID, resposta.getId()),
                () -> assertEquals(NOME, resposta.getNome()),
                () -> assertEquals(EMAIL_ALTERADO, resposta.getEmail()),
                () -> assertEquals(TELEFONE, resposta.getTelefone()),
                () -> assertEquals(SENHA, resposta.getSenha()),
                () -> assertEquals(List.of(perfil2).getClass(),
                        resposta.getPerfis().getClass()));
    }

    @Test
    void quandoAtualizarEmailDoClientePeloIdErradoDeveRetornarEntityNotFoundException() {

        when(clienteRepository.save(any())).thenReturn(cliente);
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.ofNullable(cliente));

        try {
            clienteService.atualizarCliente(clienteAtualizado, 2L);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
            assertEquals(CLIENTE_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void excluirClienteComSucesso() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.ofNullable(cliente));
        doNothing().when(clienteRepository).delete(cliente);

        clienteService.excluirCliente(ID);

        verify(clienteRepository, times(1)).delete(cliente);
    }

    @Test
    void excluirClienteDeveRetornarEntityNotFoundException() {
        when(clienteRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(CLIENTE_NAO_ENCONTRADO));

        try {
            clienteService.excluirCliente(ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
            assertEquals(CLIENTE_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void quandoBuscarClientePorEmailDeveRetornarUmaInstanciaDeCLiente() {
        when(clienteRepository.findByEmail(anyString())).thenReturn(optionalCliente);

        Cliente resposta = clienteService.buscarClientePorEmail(EMAIL);

        assertEquals(Cliente.class, resposta.getClass());
    }

    @Test
    void quandoBuscarClienteoPorEmailDeveRetornarUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class,
                () -> clienteService.buscarClientePorEmail(EMAIL));

        try {
            clienteService.buscarClientePorEmail(EMAIL);
        } catch (Exception e) {

            assertEquals(CLIENTE_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void listarTodosOsAtendimentosDoCliente() {
        when(atendimentoRepository.findAllByClienteId(anyLong(),
                any())).thenReturn(new PageImpl<>(List.of(atendimento)));

        Page<Atendimento> resposta = clienteService.listarTodosOsAtendimentosDoCliente(ID, pageable);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(1, resposta.getTotalElements()),
                () -> assertEquals(Atendimento.class, resposta.iterator().next().getClass()),
                () -> assertEquals(ID, resposta.getContent().get(0).getId()),
                () -> assertEquals(TITULO_CONSULTA,
                        resposta.getContent().get(0).getTitulo()),
                () -> assertEquals(DESCRICAO_ATENDIMENTO,
                        resposta.getContent().get(0).getDescricaoAtendimento()),
                () -> assertEquals(DIAGNOSTICO,
                        resposta.getContent().get(0).getDiagnostico()),
                () -> assertEquals(COMENTARIOS,
                        resposta.getContent().get(0).getComentarios()),
                () -> assertEquals(Cliente.class,
                        resposta.getContent().get(0).getCliente().getClass()),
                () -> assertEquals(Cachorro.class,
                        resposta.getContent().get(0).getCachorro().getClass()),
                () -> assertEquals(Veterinario.class,
                        resposta.getContent().get(0).getVeterinario().getClass()));
    }

    private void inicializandoCliente() {

        inicializandoPerfil();
        inicializandoEnderecos();

        cliente = Cliente.builder()
                .id(ID)
                .nome(NOME)
                .email(EMAIL)
                .senha(SENHA)
                .telefone(TELEFONE)
                .endereco(endereco1)
                .perfis(List.of(perfil2))
                .build();

        clienteAtualizado = Cliente.builder()
                .id(ID)
                .nome(NOME)
                .email(EMAIL_ALTERADO)
                .senha(SENHA)
                .telefone(TELEFONE)
                .endereco(endereco1)
                .perfis(List.of(perfil2))
                .build();

        optionalCliente = Optional.of(cliente);
        pageCliente = new PageImpl<>(List.of(cliente));

        inicializandoCachorro();
        inicializandoVeterinario();
        inicializandoDadosCachorroDia();
        inicializandoAtendimento();
    }

    private void inicializandoAtendimento() {

        atendimento = Atendimento.builder()
                .id(1L)
                .cliente(cliente)
                .cachorro(cachorro)
                .veterinario(veterinario)
                .dadosCachorroDia(dadosCachorroDia)
                .titulo(TITULO_CONSULTA)
                .descricaoAtendimento(DESCRICAO_ATENDIMENTO)
                .diagnostico(DIAGNOSTICO)
                .comentarios(COMENTARIOS)
                .build();

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
                .crmv("BR123")
                .nome("João")
                .email("joao@email.com")
                .senha("123")
                .telefone("123456")
                .endereco(endereco2)
                .perfis(List.of(perfil3))
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

    private void inicializandoPerfil() {
        perfil1 = new Perfil(1L, "ADMIN");
        perfil2 = new Perfil(2L, "CLIENTE");
        perfil3 = new Perfil(null, "VETERINARIO");
    }

}