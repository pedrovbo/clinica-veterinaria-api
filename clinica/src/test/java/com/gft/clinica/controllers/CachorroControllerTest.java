package com.gft.clinica.controllers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.persistence.EntityNotFoundException;

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

import com.gft.clinica.dtos.AtendimentoDTO;
import com.gft.clinica.dtos.CachorroDTO;
import com.gft.clinica.dtos.dogapi.DadosRacaDogApi;
import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.services.CachorroService;
import com.gft.clinica.services.dogapi.DogApiService;

import reactor.core.publisher.Flux;

@SpringBootTest
public class CachorroControllerTest {

    private static final Long ID = 1L;

    private static final String SEXO = "macho";

    private static final Double PESO = 2D;

    private static final String PORTE = "miniatura";

    private static final Double TAMANHO = 10D;

    private static final Integer IDADE = 4;

    private static final String RACA = "Miniature Pinscher";

    private static final String NOME = "Rex";

    private static final List<Perfil> PERFIS = List.of(new Perfil(1L, "ADMIN"));

    private static final Long ID_INEXISTENTE = 10L;

    private static final String ENTITY_NOT_FOUND_EXCEPTION = "Verifique os parâmetros da requisição";

    private Cliente cliente;

    private List<AtendimentoDTO> atendimentosDTO;

    private Page<Cachorro> pageCachorros;

    private List<Atendimento> atendimentos;

    @InjectMocks
    private CachorroController cachorroController;

    @Mock
    private CachorroService cachorroService;

    @Mock
    private CachorroDTO cachorroDTO;

    @Mock
    private Page<CachorroDTO> pageCachorrosDTO;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DogApiService dogApiService;

    private Cachorro cachorro;

    private Pageable pageable;

    private Flux<DadosRacaDogApi> fluxDadosDaRaca;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        iniciarCachorro();

        iniciarObjetosAuxiliares();
    }

    @Test
    void quandoSalvarCachorroComSucessoRetornarCachorroDTO() {

        when(cachorroService.salvarCachorro(cachorro, ID)).thenReturn(cachorro);
        when(modelMapper.map(cachorroDTO, Cachorro.class)).thenReturn(cachorro);
        when(modelMapper.map(cachorro, CachorroDTO.class)).thenReturn(cachorroDTO);
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        ResponseEntity<CachorroDTO> resposta = cachorroController.salvarCachorro(ID, cachorroDTO);

        assertAll(
                () -> assertTrue(resposta.getStatusCode().is2xxSuccessful()),
                () -> assertEquals(ResponseEntity.class, resposta.getClass()),
                () -> assertTrue(resposta.getBody().getId().equals(ID)),
                () -> assertTrue(resposta.getBody().getNome().equals(NOME)),
                () -> assertTrue(resposta.getBody().getRaca().equals(RACA)),
                () -> assertTrue(resposta.getBody().getPorte().equals(PORTE)),
                () -> assertTrue(resposta.getBody().getPeso().equals(PESO)),
                () -> assertTrue(resposta.getBody().getTamanho().equals(TAMANHO)),
                () -> assertTrue(resposta.getBody().getIdade().equals(IDADE)),
                () -> assertTrue(resposta.getBody().getSexo().equals(SEXO)));

    }

    @Test
    void quandoTentarSalvarCachorroComIdDoClienteInexistenteRetornarEntityNotFoundException() {

        when(cachorroService.salvarCachorro(cachorro, ID_INEXISTENTE))
                .thenThrow(new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION));
        when(modelMapper.map(cachorroDTO, Cachorro.class)).thenReturn(cachorro);
        when(modelMapper.map(cachorro, CachorroDTO.class)).thenReturn(cachorroDTO);
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        try {
            cachorroController.salvarCachorro(ID_INEXISTENTE, cachorroDTO);

        } catch (Exception e) {
            assertAll(
                    () -> assertTrue(e instanceof EntityNotFoundException),
                    () -> assertTrue(e.getMessage().equals(ENTITY_NOT_FOUND_EXCEPTION)),
                    () -> assertTrue(e.getClass().equals(EntityNotFoundException.class)));
        }

    }

    @Test
    void quandoListarTodosCachorrosComSucessoRetornarPageCachorroDTO() {

        when(cachorroService.listarTodosCachorros(pageable)).thenReturn(pageCachorros);
        when(modelMapper.map(cachorro, CachorroDTO.class)).thenReturn(cachorroDTO);
        pageCachorrosDTO = pageCachorros.map(cachorro -> modelMapper.map(cachorro, CachorroDTO.class));
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        ResponseEntity<Page<CachorroDTO>> resposta = cachorroController.listarTodosCachorros(pageable);

        assertAll(
                () -> assertTrue(resposta.getStatusCode().is2xxSuccessful()),
                () -> assertTrue(resposta.getBody().getClass() == PageImpl.class),
                () -> assertTrue(resposta.getBody().getContent().get(0).getId().equals(ID)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getNome().equals(NOME)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getRaca().equals(RACA)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getPorte().equals(PORTE)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getPeso().equals(PESO)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getTamanho().equals(TAMANHO)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getIdade().equals(IDADE)),
                () -> assertTrue(resposta.getBody().getContent().get(0).getSexo().equals(SEXO)));
    }

    @Test
    void quandoBuscarCachorroComSucessoRetornarCachorroDTO() {

        when(cachorroService.buscarCachorro(ID)).thenReturn(cachorro);
        when(modelMapper.map(any(), any())).thenReturn(cachorroDTO);
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        ResponseEntity<CachorroDTO> resposta = cachorroController.buscarCachorro(ID);

        assertAll(
                () -> assertTrue(resposta.getStatusCode().is2xxSuccessful()),
                () -> assertTrue(resposta.getBody().getId().equals(ID)),
                () -> assertTrue(resposta.getBody().getNome().equals(NOME)),
                () -> assertTrue(resposta.getBody().getRaca().equals(RACA)),
                () -> assertTrue(resposta.getBody().getPorte().equals(PORTE)),
                () -> assertTrue(resposta.getBody().getPeso().equals(PESO)),
                () -> assertTrue(resposta.getBody().getTamanho().equals(TAMANHO)),
                () -> assertTrue(resposta.getBody().getIdade().equals(IDADE)),
                () -> assertTrue(resposta.getBody().getSexo().equals(SEXO)));

    }

    @Test
    void quandoBuscarCachorroInexistenteRetornarEntityNotFoundException() {

        when(cachorroService.buscarCachorro(ID_INEXISTENTE))
                .thenThrow(new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION));

        try {
            cachorroController.buscarCachorro(ID_INEXISTENTE);

        } catch (Exception e) {
            assertAll(
                    () -> assertTrue(e instanceof EntityNotFoundException),
                    () -> assertTrue(e.getMessage().equals(ENTITY_NOT_FOUND_EXCEPTION)),
                    () -> assertTrue(e.getClass().equals(EntityNotFoundException.class)));
        }

    }

    @Test
    void quandoAlterarCachorroComSucessoRetornarCachorroDTO() {

        when(cachorroService.atualizarCachorro(cachorro, ID)).thenReturn(cachorro);
        when(modelMapper.map(cachorroDTO, Cachorro.class)).thenReturn(cachorro);
        when(modelMapper.map(cachorro, CachorroDTO.class)).thenReturn(cachorroDTO);
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        ResponseEntity<CachorroDTO> resposta = cachorroController.alterarCachorro(cachorroDTO, ID);

        assertAll(
                () -> assertTrue(resposta.getStatusCode().is2xxSuccessful()),
                () -> assertTrue(resposta.getBody().getId().equals(ID)),
                () -> assertTrue(resposta.getBody().getNome().equals(NOME)),
                () -> assertTrue(resposta.getBody().getRaca().equals(RACA)),
                () -> assertTrue(resposta.getBody().getPorte().equals(PORTE)),
                () -> assertTrue(resposta.getBody().getPeso().equals(PESO)),
                () -> assertTrue(resposta.getBody().getTamanho().equals(TAMANHO)),
                () -> assertTrue(resposta.getBody().getIdade().equals(IDADE)),
                () -> assertTrue(resposta.getBody().getSexo().equals(SEXO)));

    }

    @Test
    void quandoAlterarCachorroComIdInexistenteRetornarEntityNotFoundException() {

        when(cachorroService.atualizarCachorro(cachorro, ID_INEXISTENTE))
                .thenThrow(new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION));
        when(modelMapper.map(cachorroDTO, Cachorro.class)).thenReturn(cachorro);
        when(modelMapper.map(cachorro, CachorroDTO.class)).thenReturn(cachorroDTO);
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        assertThrows(EntityNotFoundException.class,
                () -> cachorroController.alterarCachorro(cachorroDTO, ID_INEXISTENTE));

    }

    @Test
    void quandoExcluirCachorroComSucessoRetornarNoContent() {

        doNothing().when(cachorroService).excluirCachorro(ID);
        when(modelMapper.map(cachorro, CachorroDTO.class)).thenReturn(cachorroDTO);
        when(dogApiService.buscarDadosRaca(RACA)).thenReturn(fluxDadosDaRaca);

        ResponseEntity<CachorroDTO> resposta = cachorroController.excluirCachorro(ID);

        assertAll(
                () -> assertTrue(resposta.getStatusCode().is2xxSuccessful()),
                () -> assertEquals(ResponseEntity.class, resposta.getClass()),
                () -> assertTrue(null == resposta.getBody()),
                () -> assertTrue(resposta.getStatusCode().equals(HttpStatus.NO_CONTENT)));
    }

    void iniciarCachorro() {

        cachorro = Cachorro.builder().nome(NOME).raca(RACA).idade(IDADE).tamanho(TAMANHO).porte(PORTE).id(ID).peso(PESO)
                .sexo(SEXO).cliente(cliente).atendimentos(atendimentos).build();

        cachorroDTO = CachorroDTO.builder().nome(NOME).raca(RACA).idade(IDADE).tamanho(TAMANHO).porte(PORTE).id(ID)
                .peso(PESO).sexo(SEXO).nomeCliente("Godofredo").atendimentos(atendimentosDTO).build();

        pageCachorros = new PageImpl<>(List.of(cachorro));

    }

    void iniciarObjetosAuxiliares() {

        cliente = Cliente.builder().id(ID).nome("Godofredo").perfis(PERFIS).build();
        fluxDadosDaRaca = Flux.just(DadosRacaDogApi.builder().build());

    }
}
