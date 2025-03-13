package itmo.blps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserAlreadyExistException(UserAlreadyExistException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOrderNotFoundException(OrderNotFoundException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProductNotFoundException(ProductNotFoundException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAddressNotFoundException(AddressNotFoundException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAddressNotProvidedException(AddressNotProvidedException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getClass().getCanonicalName(),
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format(
                        "Ошибка валидации поля '%s': %s (текущее значение: '%s')",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue() == null ? "пусто" : error.getRejectedValue()))
                .collect(Collectors.toList());

        return new ErrorResponse(
                "Ошибка валидации",
                String.join("\n", errors));
    }
}
