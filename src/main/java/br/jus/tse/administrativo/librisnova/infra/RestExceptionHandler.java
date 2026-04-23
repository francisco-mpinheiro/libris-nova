package br.jus.tse.administrativo.librisnova.infra;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of("mensagem", "Não é possível excluir este livro pois ele está em uso")
        );
    }
}
