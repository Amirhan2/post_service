package faang.school.postservice.util.exceptionhandler;

import faang.school.postservice.dto.response.ErrorResponse;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DataValidationException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MESSAGE = "Error has been occurred: ";

    @ExceptionHandler(CreatePostException.class)
    public ResponseEntity<ErrorResponse> handleException(CreatePostException e) {
        log.error(MESSAGE, e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleException(FeignException e) {
        log.error("Error with Feign has been occurred: ", e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        log.error(MESSAGE, e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(DataValidationException e) {
        log.error(MESSAGE, e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(PublishPostException.class)
    public ResponseEntity<ErrorResponse> handleException(PublishPostException e) {
        log.error(MESSAGE, e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UpdatePostException.class)
    public ResponseEntity<ErrorResponse> handleException(UpdatePostException e) {
        log.error(MESSAGE, e);

        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
    }
}
