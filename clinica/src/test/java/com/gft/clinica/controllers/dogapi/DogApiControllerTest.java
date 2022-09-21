package com.gft.clinica.controllers.dogapi;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gft.clinica.dtos.dogapi.DadosRacaDogApi;
import com.gft.clinica.dtos.dogapi.ImagensRacaDogApi;
import com.gft.clinica.services.dogapi.DogApiService;

import reactor.core.publisher.Flux;

@SpringBootTest
public class DogApiControllerTest {

    private static final String RACA = "Affenpinscher";

    @InjectMocks
    DogApiController dogApiController;

    @Mock
    DogApiService mockDogApiService;

    private DadosRacaDogApi dadosRacaDogApi;

    private List<ImagensRacaDogApi> listImagensRacaDogApi;

    private Flux<ImagensRacaDogApi> fluxImagensRacaDogApi;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        iniciarObjetosAuxiliares();
    }

    @Test
    void quandoBuscarDadosDaRacaComSucessoRetornarListDadosRacaDogApi() {

        when(mockDogApiService.buscarDadosRaca(RACA)).thenReturn(Flux.just(dadosRacaDogApi));

        ResponseEntity<List<DadosRacaDogApi>> resposta = dogApiController.buscarDadosRaca(RACA);

        assertAll(
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(DadosRacaDogApi.class, resposta.getBody().get(0).getClass()),
                () -> assertEquals(ArrayList.class, resposta.getBody().getClass()),
                () -> assertTrue(resposta.getBody().size() > 0));

    }

    @Test
    void quandoBuscarImagemRacaComSucessoRetornarListImagensRacaDogApi() {

        when(mockDogApiService.buscarImagemRaca(RACA)).thenReturn(listImagensRacaDogApi);

        ResponseEntity<List<ImagensRacaDogApi>> resposta = dogApiController.buscarImagemRaca(RACA);

        assertAll(
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(ImagensRacaDogApi.class, resposta.getBody().get(0).getClass()),
                () -> assertTrue(resposta.getBody().size() == 1));

    }

    @Test
    void quandoBuscarImagensRacasComSucessoRetornarListImagensRacaDogApi() {

        when(mockDogApiService.buscarImagensRacas()).thenReturn(fluxImagensRacaDogApi);

        ResponseEntity<List<ImagensRacaDogApi>> resposta = dogApiController.buscarImagensRacas();

        assertAll(
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(ImagensRacaDogApi.class, resposta.getBody().get(0).getClass()),
                () -> assertTrue(resposta.getBody().size() > 0));
    }

    @Test
    void quandoBuscarTodasRacasComSucessoRetornarListDadosRacaDogApi() {

        when(mockDogApiService.buscarTodasRacas()).thenReturn(Flux.just(dadosRacaDogApi));

        ResponseEntity<List<DadosRacaDogApi>> resposta = dogApiController.buscarTodasRacas();

        assertAll(
                () -> assertEquals(HttpStatus.OK, resposta.getStatusCode()),
                () -> assertEquals(DadosRacaDogApi.class, resposta.getBody().get(0).getClass()),
                () -> assertTrue(resposta.getBody().size() > 0));
    }

    private void iniciarObjetosAuxiliares() {
        dadosRacaDogApi = DadosRacaDogApi.builder()
                .name(RACA)
                .build();

        listImagensRacaDogApi = List.of(ImagensRacaDogApi.builder()
                .image("IMAGEM")
                .name(RACA)
                .reference_image_id(RACA)
                .build());

        fluxImagensRacaDogApi = Flux.just(ImagensRacaDogApi.builder()
                .image("IMAGEM")
                .name(RACA)
                .reference_image_id(RACA)
                .build());
    }

}
