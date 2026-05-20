package com.agrosys.plot.global;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.agrosys.plot.dto.ErrorResponse;
import com.agrosys.plot.dto.Response;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        /**
         * Maneja errores de validación (@Valid)
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Response> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Error")
                                .message("Error de validación en los campos")
                                .details(errors)
                                .build();
                Response response = Response.builder()
                                .success(false)
                                .message("Error de validación en los campos")
                                .data(error)
                                .build();

                return ResponseEntity.ok(response);
        }

        /**
         * Maneja error de credenciales incorrectas
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Response> handleBadCredentialsException(
                        BadCredentialsException ex) {

                log.error("Intento de login fallido: {}", ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message("Usuario o contraseña incorrectos")
                                .build();

                Response response = Response.builder()
                                .success(false)
                                .message("Usuario o contraseña incorrectos")
                                .data(error)
                                .build();

                return ResponseEntity.ok(response);
        }

        /**
         * Maneja error de usuario no encontrado
         */
        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<Response> handleUsernameNotFoundException(
                        UsernameNotFoundException ex) {

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .build();

                Response response = Response.builder()
                                .success(false)
                                .message("Usuario no encontrado")
                                .data(error)
                                .build();

                return ResponseEntity.ok(response);
        }

        /**
         * Maneja errores de negocio (RuntimeException)
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Response> handleRuntimeException(RuntimeException ex) {

                log.error("Error inesperado: {}", ex.getMessage(), ex);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .build();

                Response response = Response.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(error)
                                .build();

                return ResponseEntity.ok(response);

        }

        /**
         * Maneja cualquier otra excepción no contemplada
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Response> handleGenericException(Exception ex) {

                log.error("Error interno del servidor: {}", ex.getMessage(), ex);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("Ha ocurrido un error interno en el servidor")
                                .build();

                Response response = Response.builder()
                                .success(false)
                                .message("Ha ocurrido un error interno en el servidor")
                                .data(error)
                                .build();

                return ResponseEntity.ok(response);
        }
}