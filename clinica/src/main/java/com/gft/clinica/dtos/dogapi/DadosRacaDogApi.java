package com.gft.clinica.dtos.dogapi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
public class DadosRacaDogApi {

    private String name;
    private String temperament;
    private String bred_for;
    private String breed_group;
    private String life_span;
    private String reference_image_id;
    private Object weight;
    private Object height;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTemperament() {
        return temperament;
    }
    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }
    public String getBred_for() {
        return bred_for;
    }
    public void setBred_for(String bred_for) {
        this.bred_for = bred_for;
    }
    public String getBreed_group() {
        return breed_group;
    }
    public void setBreed_group(String breed_group) {
        this.breed_group = breed_group;
    }
    public String getLife_span() {
        return life_span;
    }
    public void setLife_span(String life_span) {
        this.life_span = life_span;
    }
    public String getReference_image_id() {
        return reference_image_id;
    }
    public void setReference_image_id(String reference_image_id) {
        this.reference_image_id = reference_image_id;
    }
    public Object getWeight() {
        return weight;
    }
    public void setWeight(Object weight) {
        this.weight = weight;
    }
    public Object getHeight() {
        return height;
    }
    public void setHeight(Object height) {
        this.height = height;
    }

    
    
}
