package com.gft.clinica.services.dogapi;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gft.clinica.dtos.dogapi.DadosRacaDogApi;
import com.gft.clinica.dtos.dogapi.ImagensRacaDogApi;
import com.gft.clinica.exception.ClinicaException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DogApiService {

    private final WebClient webClient;

    private static final String apiKey = "b5259875-4a6e-4553-8b7e-fd8e5d1efbb0";

    public DogApiService(WebClient.Builder builder) {
        webClient = builder.baseUrl("https://api.thedogapi.com/v1/").defaultHeader("x-api-key", apiKey).build();
    }

    public Flux<DadosRacaDogApi> buscarDadosRaca(String raca) {

        Flux<DadosRacaDogApi> dadosRacaDogApi = webClient
                .get()
                .uri("breeds/search?q=" + raca)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new ClinicaException(
                                "verifique os parâmetros informados ou a URL informada", HttpStatus.BAD_REQUEST)))
                .bodyToFlux(DadosRacaDogApi.class);
        if (dadosRacaDogApi.collectList().block().size() > 1 || dadosRacaDogApi.collectList().block().size() == 0) {
            throw new ClinicaException("Informe o nome específico para raça", HttpStatus.BAD_REQUEST);
        }
        return dadosRacaDogApi;
    }

    public Flux<DadosRacaDogApi> buscarTodasRacas() {

        Flux<DadosRacaDogApi> dadosRacaDogApi = webClient
                .get()
                .uri("breeds")
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new ClinicaException(
                                "verifique os parâmetros informados ou a URL informada", HttpStatus.BAD_REQUEST)))
                .bodyToFlux(DadosRacaDogApi.class);
        return dadosRacaDogApi;
    }

    public Flux<ImagensRacaDogApi> buscarImagensRacas() {

        Flux<ImagensRacaDogApi> dadosRacaDogApi = webClient
                .get()
                .uri("breeds")
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new ClinicaException(
                                "verifique os parâmetros informados ou a URL informada", HttpStatus.BAD_REQUEST)))
                .bodyToFlux(ImagensRacaDogApi.class);
        return dadosRacaDogApi;
    }

    public List<ImagensRacaDogApi> buscarImagemRaca(String raca) {

        Flux<ImagensRacaDogApi> dadosRacaDogApi = webClient
                .get()
                .uri("breeds")
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new ClinicaException(
                                "verifique os parâmetros informados ou a URL informada", HttpStatus.BAD_REQUEST)))
                .bodyToFlux(ImagensRacaDogApi.class);

        List<ImagensRacaDogApi> listaImagens = dadosRacaDogApi.collectList().block();

        for (ImagensRacaDogApi imagemApi : listaImagens) {

            if (imagemApi.getName().equals(raca)) {
                return Flux.just(imagemApi).collectList().block();
            }
        }
        throw new ClinicaException("Informe o nome específico para raça", HttpStatus.BAD_REQUEST);
    }
}
