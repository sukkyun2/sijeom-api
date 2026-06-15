package com.flownews.api.common.api

import com.flownews.api.common.app.NoDataException
import com.flownews.api.common.app.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class CommonExceptionHandler {
    private val logger = LoggerFactory.getLogger(CommonExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): ApiResponse<Void?> {
        val message =
            ex.bindingResult.fieldErrors.joinToString(", ") {
                it.defaultMessage ?: "유효하지 않은 값입니다"
            }
        return ApiResponse.badRequest(message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ApiResponse<Void?> {
        logger.warn(ex.message)
        return ApiResponse.badRequest(ex.message)
    }

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: ValidationException): ApiResponse<Void?> {
        return ApiResponse.badRequest(ex.message)
    }

    @ExceptionHandler(NoDataException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoDataException(ex: NoDataException): ApiResponse<Void?> {
        logger.warn(ex.message)
        return ApiResponse.nodata()
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noHandlerFoundException(ex: NoHandlerFoundException): ApiResponse<Void?> {
        logger.warn(ex.message)
        return ApiResponse.badRequest(ex.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ApiResponse<Void?> {
        logger.error(ex.message, ex)
        return ApiResponse.error("서버 오류가 발생했습니다: ${ex.message}")
    }
}
