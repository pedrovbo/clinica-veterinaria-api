package com.gft.clinica.exception;

import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.gft.clinica.dtos.ApiErrorDTO;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ ClinicaException.class })
	public ResponseEntity<ApiErrorDTO> handleLojaException(ClinicaException ex, WebRequest request) {

		String error = "Erro no sistema";

		ApiErrorDTO apiError = new ApiErrorDTO(ex.getMessage(), error, ex.getStatus());

		return new ResponseEntity<ApiErrorDTO>(apiError, new HttpHeaders(), apiError.getStatus());

	}

	@ExceptionHandler({ EntityNotFoundException.class })
	public ResponseEntity<ApiErrorDTO> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {

		String error = "Recurso não encontrado";

		String message = "Verifique os parâmetros da requisição";

		ApiErrorDTO apiError = new ApiErrorDTO(message, error, HttpStatus.NOT_FOUND);

		return new ResponseEntity<ApiErrorDTO>(apiError, new HttpHeaders(), apiError.getStatus());

	}

	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<ApiErrorDTO> handleEntityNotFoundException(NoSuchElementException ex, WebRequest request) {

		String error = "Impossível realizar a operação";

		String message = "Verifque os parâmetros da requisição";

		ApiErrorDTO apiError = new ApiErrorDTO(message, error, HttpStatus.BAD_REQUEST);

		return new ResponseEntity<ApiErrorDTO>(apiError, new HttpHeaders(), apiError.getStatus());

	}

	@ExceptionHandler({ SQLIntegrityConstraintViolationException.class })
	public ResponseEntity<ApiErrorDTO> handleEntityNotFoundException(SQLIntegrityConstraintViolationException ex,
			WebRequest request) {

		String error = "Possui recursos associados";

		String message = "Não pode ser excluído";

		ApiErrorDTO apiError = new ApiErrorDTO(message, error, HttpStatus.CONFLICT);

		return new ResponseEntity<ApiErrorDTO>(apiError, new HttpHeaders(), apiError.getStatus());

	}

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<ApiErrorDTO> handleEntityNotFoundException(ConstraintViolationException ex,
			WebRequest request) {

		String error = "Possui recursos associados";

		String message = "Não pode ser excluído";

		ApiErrorDTO apiError = new ApiErrorDTO(message, error, HttpStatus.CONFLICT);

		return new ResponseEntity<ApiErrorDTO>(apiError, new HttpHeaders(), apiError.getStatus());

	}

}
