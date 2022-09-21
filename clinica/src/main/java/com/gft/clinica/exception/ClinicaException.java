package com.gft.clinica.exception;


import org.springframework.http.HttpStatus;

import com.gft.clinica.dtos.ApiErrorDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ClinicaException extends RuntimeException {

    private static final long serialVersionUID = 1L;
	
	private String message;
    private HttpStatus status;	

    public ClinicaException(ApiErrorDTO apiErrorDTO) {
        this.message = apiErrorDTO.getMessage();
		this.status = apiErrorDTO.getStatus();
    }

}
