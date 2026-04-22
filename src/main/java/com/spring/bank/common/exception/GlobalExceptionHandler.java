package com.spring.bank.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ErrorResponseDTO errorResponse = generateErrorResponse(HttpStatus.BAD_REQUEST, request, message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountTypeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountTypeAlreadyExists(AccountTypeAlreadyExistsException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, request, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotFound(AccountNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientFunds(InsufficientFundsException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(InvalidTransferAccountTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTransferAccountType(InvalidTransferAccountTypeException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, request, ex.getMessage());
    }

    @ExceptionHandler(AccountTypeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountTypeNotFound(AccountTypeNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidAccount(InvalidAccountException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(CardNotActiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotActive(CardNotActiveException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardNotFound(CardNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(InvalidCvvException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCvv(InvalidCvvException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(ExpiredCardException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredCard(ExpiredCardException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(InvalidCardTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCardType(InvalidCardTypeException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(TransferNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransferNotFound(TransferNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransactionNotFound(TransactionNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(SavingsBoxNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleSavingsBoxNotFound(SavingsBoxNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(PixKeyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePixKeyNotFound(PixKeyNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(PixContactNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlePixContactNotFound(PixContactNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(PixKeyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handlePixKeyAlreadyExists(PixKeyAlreadyExistsException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, request, ex.getMessage());
    }

    @ExceptionHandler(PixKeyLimitExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handlePixKeyLimitExceeded(PixKeyLimitExceededException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage());
    }

    @ExceptionHandler(CardBillNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCardBillNotFound(CardBillNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, request, ex.getMessage());
    }

    @ExceptionHandler(BillAlreadyPaidException.class)
    public ResponseEntity<ErrorResponseDTO> handleBillAlreadyPaid(BillAlreadyPaidException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, request, ex.getMessage());
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleClientAlreadyExists(ClientAlreadyExistsException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, request, ex.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleClientNotFound(ClientNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, request, ex.getMessage());
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParam(org.springframework.web.bind.MissingServletRequestParameterException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, request, "Missing parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, request, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, WebRequest request, String message) {
        return new ResponseEntity<>(generateErrorResponse(status, request, message), status);
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
