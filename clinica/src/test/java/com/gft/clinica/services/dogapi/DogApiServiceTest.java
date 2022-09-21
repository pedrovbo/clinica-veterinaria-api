package com.gft.clinica.services.dogapi;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import com.gft.clinica.dtos.dogapi.DadosRacaDogApi;
import com.gft.clinica.dtos.dogapi.ImagensRacaDogApi;
import com.gft.clinica.exception.ClinicaException;

import reactor.core.publisher.Flux;

@SpringBootTest
public class DogApiServiceTest {

    private static final String RACA = "Affenpinscher";
    private static final String RACA_INEXISTENTE = "Farofino Alado";
    private static final String BAD_REQUEST_NOME_RACA = "Informe o nome específico para raça";

    @Autowired
    private DogApiService dogApiService;

    @Mock
    private DogApiService mockDogApiService;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder mockBuilder;

    private Flux<ImagensRacaDogApi> fluxImagensRacaDogApi;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        iniciarObjetosAuxiliares();

    }

    @Test
    void quandoBuscarDadosRacaComSucessoRetornaFluxDeDadosRacaDogApi() {

        Flux<DadosRacaDogApi> resposta = dogApiService.buscarDadosRaca(RACA);

        assertAll(
                () -> assertEquals(RACA, resposta.collectList().block().get(0).getName()),
                () -> assertTrue(resposta.collectList().block().get(0).getClass().equals(DadosRacaDogApi.class)));

    }

    @Test
    void deveRetornarClinicaExceptionQuandoBuscarDadosRacaComErro() {

        assertThrows(ClinicaException.class, () -> dogApiService.buscarDadosRaca(RACA_INEXISTENTE));

        try {

            dogApiService.buscarDadosRaca(RACA_INEXISTENTE);

        } catch (ClinicaException e) {

            assertAll(

                    () -> assertEquals(e.getMessage(), BAD_REQUEST_NOME_RACA),
                    () -> assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST));

        }

    }

    @Test
    void deveRetornarClinicaExceptionQuandoBuscarDadosDaRacaUrlComErroNosParametrosDaRequisicao() {

        doThrow(new ClinicaException("verifique os parâmetros informados ou a URL informada", HttpStatus.BAD_REQUEST))
                .when(mockDogApiService)
                .buscarDadosRaca(anyString());

        assertThrows(ClinicaException.class, () -> mockDogApiService.buscarDadosRaca(RACA));
    }

    @Test
    void quandoBuscarTodasAsRacasRetornaFluxDeDadosRacaDogApi() {

        Flux<DadosRacaDogApi> resposta = dogApiService.buscarTodasRacas();

        assertAll(
                () -> assertTrue(!resposta.collectList().block().isEmpty()),
                () -> assertTrue(resposta.collectList().block().size() > 1),
                () -> assertEquals(RACA, resposta.collectList().block().get(0).getName()),
                () -> assertTrue(resposta.collectList().block().get(0).getClass().equals(DadosRacaDogApi.class)));

    }

    @Test
    void deveRetornarClinicaExceptionQuandoBuscarTodasAsRAcasUrlComErroNosParametrosDaRequisicao() {

        doThrow(new ClinicaException("verifique os parâmetros informados ou a URL informada", HttpStatus.BAD_REQUEST))
                .when(mockDogApiService)
                .buscarTodasRacas();

        assertThrows(ClinicaException.class, () -> mockDogApiService.buscarTodasRacas());

    }

    @Test
    void quandoBuscarImagemDaRacaDeveRetornarListImagensRacaDogApi() {

        List<ImagensRacaDogApi> resposta = dogApiService.buscarImagemRaca(RACA);

        assertAll(
                () -> assertTrue(!resposta.isEmpty()),
                () -> assertTrue(resposta.size() > 0),
                () -> assertTrue(!resposta.get(0).getReference_image_id().isEmpty()),
                () -> assertTrue(!resposta.get(0).getName().isEmpty()),
                () -> assertTrue(resposta.get(0).getClass().equals(ImagensRacaDogApi.class)),
                () -> assertTrue(resposta.get(0).getClass().equals(ImagensRacaDogApi.class)));

    }

    @Test
    void quandoBuscarImagemDaRacaComNomeInvalidoDeveRetornarClinicaException() {

        assertThrows(ClinicaException.class, () -> dogApiService.buscarImagemRaca(RACA_INEXISTENTE));

        try {

            dogApiService.buscarImagemRaca(RACA_INEXISTENTE);

        } catch (ClinicaException e) {

            assertAll(

                    () -> assertEquals(e.getMessage(), BAD_REQUEST_NOME_RACA),
                    () -> assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST));

        }
    }

    @Test
    void quandoBuscarImagensRacasComSucessoRetornarFluxImagensRacaDogApi() {

        Flux<ImagensRacaDogApi> resposta = dogApiService.buscarImagensRacas();

        assertAll(
                () -> assertTrue(!resposta.collectList().block().isEmpty()),
                () -> assertTrue(!resposta.collectList().block().get(0).getReference_image_id().isEmpty()),
                () -> assertTrue(!resposta.collectList().block().get(0).getName().isEmpty()),
                () -> assertTrue(resposta.collectList().block().size() > 1),
                () -> assertTrue(resposta.collectList().block().get(0).getClass().equals(ImagensRacaDogApi.class)));

    }

    private void iniciarObjetosAuxiliares() {

        fluxImagensRacaDogApi = Flux.just(ImagensRacaDogApi.builder()
                .name("IMAGEM")
                .image(fluxImagensRacaDogApi)
                .reference_image_id("RZ8Z8z")
                .build());

    }
}
