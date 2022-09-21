package com.gft.clinica.dtos.dogapi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
public class ImagensRacaDogApi{

    private String name;
    private String reference_image_id;
    private Object image;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getReference_image_id() {
        return reference_image_id;
    }
    public void setReference_image_id(String reference_image_id) {
        this.reference_image_id = reference_image_id;
    }
    public Object getImage() {
        return image;
    }
    public void setImage(Object image) {
        this.image = image;
    }
    
}
