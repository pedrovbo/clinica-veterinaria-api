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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Endereco;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.exception.ClinicaException;
import com.gft.clinica.repositories.AtendimentoRepository;
import com.gft.clinica.repositories.PerfilRepository;
import com.gft.clinica.repositories.VeterinarioRepository;

@SpringBootTest
class VeterinarioServiceTest {

    public static final Long ID = 1L;
    public static final String VETERINARIO_NAO_ENCONTRADO = "Veterinário não encontrado";
    public static final String NOME_VETERINARIO = "Tripa Seca";
    public static final String EMAIL_VETERINARIO = "quase.nada@email.com";
    public static final String CRMV = "123879";
    public static final String TELEFONE = "4535-8677";
    public static final String SENHA = "Botina";
    public static final String EMAIL_ALTERADO = "alma.negra@email.com";
    public static final String OBRIGATORIO_TER_PERFIL_VETERINARIO = "Obrigatorio ter perfil veterinario";
    public static final String EMAIL_JA_CADASTRADO = "Email já cadastrado";
    public static final String TITULO_CONSULTA = "Consulta";
    public static final String DESCRICAO_ATENDIMENTO = "Cachorro dancando";
    public static final String DIAGNOSTICO = "Euforico";
    public static final String COMENTARIOS = "Muito nervoso";
    public static final String NOME_CLIENTE = "Poucas Trancas";
    public static final String EMAIL_CLIENTE = "poucas.trancas@email.com";
    public static final String CEP = "12345-678";
    public static final String COMPLEMENTO = "Apto. 101";
    public static final String LOGRADOURO = "Rua dos Bobos";
    public static final String NUMERO = "123A";
    public static final String NOME_CACHORRO = "Simpato";
    public static final String RACA_CACHORRO = "Amasaki";
    public static final double TAMANHO_CACHORRO = 15d;
    public static final String PORTE_CACHORRO = "Miniatura";
    public static final double PESO_CACHORRO = 2.0;
    public static final String MACHO = "Macho";

    @InjectMocks
    private VeterinarioService veterinarioService;

    @Mock
    private ValidationService validationService;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private AtendimentoRepository atendimentoRepository;
    @Mock
    private BCryptPasswordEncoder encoder;

    private Veterinario veterinario;
    private Veterinario veterinarioAtualizado;
    private Optional<Veterinario> veterinarioOptional;

    private Endereco endereco;
    private Cliente cliente;
    private Cachorro cachorro;
    private Atendimento atendimento;

    private Pageable pageable;

    private Page<Veterinario> veterinarioPage;

    private Page<Atendimento> atendimentoPage;

    private Perfil perfil1;
    private Perfil perfil2;
    private Perfil perfil3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        iniciarVeterinario();
    }

    @Test
    void quandoSalvarVeterinarioDeveRetornarSucesso() {

        when(veterinarioRepository.save(any())).thenReturn(veterinario);
        when(validationService.validacaoEmail(anyString())).thenReturn(veterinario.getEmail());
        when(validationService.validacaoPerfilVeterinario(any(), anyList())).thenReturn(veterinario);
        when(encoder.encode(anyString())).thenReturn(veterinario.getSenha());

        Veterinario resposta = veterinarioService.salvarVeterinario(veterinario);

        verify(validationService, times(1)).validacaoEmail(EMAIL_VETERINARIO);
        verify(validationService, times(1)).validacaoPerfilVeterinario(any(), anyList());
        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(Veterinario.class, resposta.getClass()),
                () -> assertEquals(ID, resposta.getId()),
                () -> assertEquals(NOME_VETERINARIO, resposta.getNome()),
                () -> assertEquals(EMAIL_VETERINARIO, resposta.getEmail()),
                () -> assertEquals(TELEFONE, resposta.getTelefone()),
                () -> assertEquals(CRMV, resposta.getCrmv()),
                () -> assertEquals(SENHA, resposta.getSenha()),
                () -> assertEquals(List.of(perfil1, perfil3).getClass(), resposta.getPerfis().getClass()));
    }

    @Test
    void quandoSalvarVeterinarioComEmailCadastradoDeveRetornarClinicaException() {

        when(veterinarioRepository.save(any())).thenReturn(veterinario);
        when(validationService.validacaoEmail(anyString()))
                .thenThrow(new ClinicaException(EMAIL_JA_CADASTRADO, HttpStatus.CONFLICT));

        try {
            veterinario.setEmail(EMAIL_VETERINARIO);
            veterinarioService.salvarVeterinario(veterinario);
        } catch (Exception e) {
            verify(validationService, times(1)).validacaoEmail(EMAIL_VETERINARIO);
            assertEquals(ClinicaException.class, e.getClass());
            assertEquals(EMAIL_JA_CADASTRADO, e.getMessage());
        }
    }

    @Test
    void quandoSalvarVeterinarioComPerfisIguaisDeveRetornarClinicaException() {

        when(validationService.validacaoPerfilVeterinario(any(), anyList()))
                .thenThrow(new ClinicaException(OBRIGATORIO_TER_PERFIL_VETERINARIO, HttpStatus.CONFLICT));
        when(veterinarioRepository.save(any())).thenReturn(veterinario);

        try {
            veterinario.setPerfis(List.of(perfil3, perfil1));
            veterinarioService.salvarVeterinario(veterinario);
        } catch (Exception e) {
            verify(validationService, times(1)).validacaoPerfilVeterinario(any(), anyList());
            assertEquals(ClinicaException.class, e.getClass());
            assertEquals(OBRIGATORIO_TER_PERFIL_VETERINARIO, e.getMessage());
        }
    }

    @Test
    void quandoBuscarVeterinarioPeloIdDeveRetornarUmaInstanciaDeVeterinario() {

        when(veterinarioRepository.findById(anyLong())).thenReturn(veterinarioOptional);

        Veterinario resposta = veterinarioService.buscarVeterinario(ID);

        assertEquals(Veterinario.class, resposta.getClass());
    }

    @Test
    void quandoBuscarVeterinarioPeloIdDeveRetornarEntityNotFoundException() {

        assertThrows(EntityNotFoundException.class, () -> veterinarioService.buscarVeterinario(10L));

        try {
            veterinarioService.buscarVeterinario(10L);
        } catch (Exception e) {

            assertEquals(VETERINARIO_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void quandoListarTodosOsVeterinariosDeveRetornarUmaPaginaDeVeterinarios() {

        when(veterinarioRepository.findAll(pageable)).thenReturn(veterinarioPage);
        when(encoder.encode(anyString())).thenReturn(veterinario.getSenha());

        Page<Veterinario> resposta = veterinarioService.listarTodosOsVeterinarios(pageable);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(1, resposta.getTotalElements()),
                () -> assertEquals(Veterinario.class, resposta.iterator().next().getClass()),
                () -> assertEquals(ID, resposta.getContent().get(0).getId()),
                () -> assertEquals(CRMV, resposta.getContent().get(0).getCrmv()),
                () -> assertEquals(NOME_VETERINARIO, resposta.getContent().get(0).getNome()),
                () -> assertEquals(EMAIL_VETERINARIO, resposta.getContent().get(0).getEmail()),
                () -> assertEquals(TELEFONE, resposta.getContent().get(0).getTelefone()),
                () -> assertEquals(CRMV, resposta.getContent().get(0).getCrmv()),
                () -> assertEquals(SENHA, resposta.getContent().get(0).getSenha()),
                () -> assertEquals(List.of(perfil1, perfil3).getClass(),
                        resposta.getContent().get(0).getPerfis().getClass()));
    }

    @Test
    void quandoAtualizarEmailDoVeterinarioPeloIdDoVeterinarioDeveRetornarSucesso() {

        when(veterinarioRepository.save(any())).thenReturn(veterinario);
        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.ofNullable(veterinario));
        when(encoder.encode(anyString())).thenReturn(veterinario.getSenha());

        Veterinario resposta = veterinarioService.atualizarVeterinario(veterinarioAtualizado, ID);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(Veterinario.class, resposta.getClass()),
                () -> assertEquals(ID, resposta.getId()),
                () -> assertEquals(NOME_VETERINARIO, resposta.getNome()),
                () -> assertEquals(EMAIL_ALTERADO, resposta.getEmail()),
                () -> assertEquals(TELEFONE, resposta.getTelefone()),
                () -> assertEquals(CRMV, resposta.getCrmv()),
                () -> assertEquals(SENHA, resposta.getSenha()),
                () -> assertEquals(List.of(perfil1, perfil3).getClass(), resposta.getPerfis().getClass()));
    }

    @Test
    void quandoAtualizarEmailDoVeterinarioPeloIdErradoDeveRetornarEntityNotFoundException() {

        when(veterinarioRepository.save(any())).thenReturn(veterinario);
        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.ofNullable(veterinario));

        try {
            veterinarioService.atualizarVeterinario(veterinarioAtualizado, 2L);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
            assertEquals(VETERINARIO_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void excluirVeterinarioComSucesso() {
        when(veterinarioRepository.findById(anyLong())).thenReturn(Optional.ofNullable(veterinario));
        doNothing().when(veterinarioRepository).delete(veterinario);

        veterinarioService.excluirVeterinario(ID);

        verify(veterinarioRepository, times(1)).delete(veterinario);
    }

    @Test
    void excluirVeterinarioDeveRetornarEntityNotFoundException() {
        when(veterinarioRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException(VETERINARIO_NAO_ENCONTRADO));

        try {
            veterinarioService.excluirVeterinario(ID);
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
            assertEquals(VETERINARIO_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void quandoBuscarVeterinarioPorEmailDeveRetornarUmaInstanciaDeVeterinario() {
        when(veterinarioRepository.findByEmail(anyString())).thenReturn(veterinarioOptional);

        Veterinario resposta = veterinarioService.buscarVeterinarioPorEmail(EMAIL_VETERINARIO);

        assertEquals(Veterinario.class, resposta.getClass());
    }

    @Test
    void quandoBuscarVeterinarioPorEmailDeveRetornarUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class,
                () -> veterinarioService.buscarVeterinarioPorEmail(EMAIL_CLIENTE));

        try {
            veterinarioService.buscarVeterinarioPorEmail(EMAIL_CLIENTE);
        } catch (Exception e) {

            assertEquals(VETERINARIO_NAO_ENCONTRADO, e.getMessage());
        }
    }

    @Test
    void listarTodosOsAtendimentosDoCliente() {
        when(atendimentoRepository.findAllByVeterinarioId(anyLong(), any())).thenReturn(atendimentoPage);

        Page<Atendimento> resposta = veterinarioService.listarTodosOsAtendimentosDoVeterinario(ID, pageable);

        assertAll(

                () -> assertNotNull(resposta),
                () -> assertEquals(1, resposta.getTotalElements()),
                () -> assertEquals(Atendimento.class, resposta.iterator().next().getClass()),
                () -> assertEquals(ID, resposta.getContent().get(0).getId()),
                () -> assertEquals(TITULO_CONSULTA, resposta.getContent().get(0).getTitulo()),
                () -> assertEquals(DESCRICAO_ATENDIMENTO,
                        resposta.getContent().get(0).getDescricaoAtendimento()),
                () -> assertEquals(DIAGNOSTICO, resposta.getContent().get(0).getDiagnostico()),
                () -> assertEquals(COMENTARIOS, resposta.getContent().get(0).getComentarios()),
                () -> assertEquals(Cliente.class, resposta.getContent().get(0).getCliente().getClass()),
                () -> assertEquals(Cachorro.class, resposta.getContent().get(0).getCachorro().getClass()),
                () -> assertEquals(Veterinario.class, resposta.getContent().get(0).getVeterinario().getClass()));
    }

    private void iniciarVeterinario() {
        perfil1 = new Perfil(1L, "ADMIN");
        perfil2 = new Perfil(2L, "CLIENTE");
        perfil3 = new Perfil(3L, "VETERINARIO");
        // perfilRepository.saveAll(Arrays.asList(perfil1, perfil2, perfil3));

        veterinario = Veterinario.builder()
                .id(ID)
                .nome(NOME_VETERINARIO)
                .email(EMAIL_VETERINARIO)
                .telefone(TELEFONE)
                .crmv(CRMV)
                .atendimentos(null)
                .senha(SENHA)
                .perfis(List.of(perfil3, perfil1))
                .build();

        veterinarioAtualizado = Veterinario.builder()
                .id(ID)
                .nome(NOME_VETERINARIO)
                .email(EMAIL_ALTERADO)
                .telefone(TELEFONE)
                .crmv(CRMV)
                .atendimentos(null)
                .senha(SENHA)
                .perfis(List.of(perfil3, perfil1))
                .build();

        veterinarioOptional = Optional.of(Veterinario.builder()
                .id(ID)
                .nome(NOME_VETERINARIO)
                .email(EMAIL_VETERINARIO)
                .telefone(TELEFONE)
                .crmv(CRMV)
                .atendimentos(null)
                .senha(SENHA)
                .perfis(List.of(perfil3, perfil1))
                .build());

        endereco = Endereco.builder()
                .cep(CEP)
                .complemento(COMPLEMENTO)
                .logradouro(LOGRADOURO)
                .numero(NUMERO)
                .build();

        cliente = Cliente.builder()
                .nome(NOME_CLIENTE)
                .email(EMAIL_CLIENTE)
                .senha(encoder.encode(SENHA))
                .telefone(TELEFONE)
                .endereco(endereco)
                .perfis(List.of(perfil2))
                .build();

        cachorro = Cachorro.builder()
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

        pageable = PageRequest.of(0, 10);

        veterinarioPage = new PageImpl<>(List.of(veterinario));

        atendimentoPage = new PageImpl<>(List.of(atendimento));
    }
}