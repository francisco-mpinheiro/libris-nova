    package br.jus.tse.administrativo.librisnova.infra.exceptions;

    import jakarta.servlet.http.HttpServletRequest;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    import java.time.LocalDateTime;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(EmailInvalidoException.class)
        public ResponseEntity<ExceptionResponse> emailInvalidoException(EmailInvalidoException ex, HttpServletRequest request){
            String requestURI = request.getRequestURI();

            LOGGER.warn("Falha e validação na requisição [{}]: {}", requestURI, ex.getMessage());

            ExceptionResponse exceptionResponse = new ExceptionResponse(
                    LocalDateTime.now(),
                    HttpStatus.PRECONDITION_FAILED.value(),
                    "Precodition Faliled",
                    ex.getMessage(),
                    requestURI
            );
            return new ResponseEntity<>(exceptionResponse, HttpStatus.PRECONDITION_FAILED);
        }


    }
