package com.spring.bank.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyHasAccountException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyHasAccount(UserAlreadyHasAccountException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.CONFLICT, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPassword(InvalidPasswordException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNAUTHORIZED, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotFound(AccountNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientFunds(InsufficientFundsException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidTransferAccountTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTransferAccountTypeException(InvalidTransferAccountTypeException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.BAD_REQUEST, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountTypeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountTypeNotFoundException(AccountTypeNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidAccountException(InvalidAccountException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CardNotActiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotActiveException(CardNotActiveException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotFoundException(CardNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCvvException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCvvException(InvalidCvvException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ExpiredCardException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredCardException(ExpiredCardException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidCardTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCardTypeException(InvalidCardTypeException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = this.generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ErrorResponseDTO generateErrorResponse(HttpStatus httpStatus, WebRequest request, String message) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }
}
