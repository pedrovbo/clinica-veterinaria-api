package com.gft.clinica.controllers.dogapi;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gft.clinica.dtos.dogapi.DadosRacaDogApi;
import com.gft.clinica.dtos.dogapi.ImagensRacaDogApi;
import com.gft.clinica.services.dogapi.DogApiService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("v1/dogapi")
@AllArgsConstructor
public class DogApiController {

    DogApiService dogApiService;

    @RequestMapping("/{raca}")
    public ResponseEntity<List<DadosRacaDogApi>> buscarDadosRaca(@PathVariable String raca) {
        return ResponseEntity.ok(dogApiService.buscarDadosRaca(raca).collectList().block());
    }

    @RequestMapping("/listarTodasRacas")
    public ResponseEntity<List<DadosRacaDogApi>> buscarTodasRacas() {
        return ResponseEntity.ok(dogApiService.buscarTodasRacas().collectList().block());
    }

    @RequestMapping("/ImagemTodasRacas")
    public ResponseEntity<List<ImagensRacaDogApi>> buscarImagensRacas() {
        return ResponseEntity.ok(dogApiService.buscarImagensRacas().collectList().block());
    }

    @RequestMapping("/ImagemRaca/{raca}")
    public ResponseEntity<List<ImagensRacaDogApi>> buscarImagemRaca(@PathVariable String raca) {
        return ResponseEntity.ok(dogApiService.buscarImagemRaca(raca));
    }
    
}
