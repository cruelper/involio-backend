package ru.nuykin.involio.exception

import org.springframework.http.HttpStatus

abstract class BaseException(
        val errorCode:String,
        override val message:String,
        val status: HttpStatus):
        RuntimeException(message)

class StockNotFoundException(ticker: String): BaseException(
        errorCode = "stock.not.found",
        message = "Stock with ticker = $ticker not found",
        status = HttpStatus.NOT_FOUND
)

class RegistrationOrLoginException(errorCode: String, message: String, status: HttpStatus):
        BaseException(
        errorCode = errorCode,
        message = message,
        status = status
)