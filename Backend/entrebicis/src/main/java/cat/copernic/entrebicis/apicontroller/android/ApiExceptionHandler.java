/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Classe que gestiona de manera centralitzada totes les excepcions que poden ocórrer
 * durant les peticions a la API REST del backend.
 * 
 * <p>Proporciona respostes HTTP adequades segons el tipus d'error detectat,
 * com errors de validació de dades, recursos no trobats, duplicats o errors inesperats.
 * També registra missatges d'error utilitzant {@code Logger} per facilitar el seguiment i diagnosi.</p>
 * 
 * <p>És detectada automàticament per Spring Boot gràcies a {@link RestControllerAdvice}.</p>
 * @author reyes
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
    
    // Captura errors de validació (ex: @Email, @NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("⚠️ Error de validació: {}", errorMsg);
        return ResponseEntity.badRequest().body(errorMsg);
    }
    
    // Captura errors quan no es troba un usuari
    @ExceptionHandler(NotFoundUsuariException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundUsuariException ex) {
        logger.error("❌ Usuari no trobat: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Captura errors per intents de crear dades duplicades
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleDuplicateException(DuplicateException ex) {
        logger.warn("⚠️ Duplicat detectat: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Captura errors quan es passen arguments invàlids
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("⚠️ Argument il·legal: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Captura qualsevol altra excepció inesperada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        logger.error("❌ Error inesperat: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error intern del servidor");
    }
}
