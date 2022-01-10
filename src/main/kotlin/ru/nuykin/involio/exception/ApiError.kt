package ru.nuykin.involio.exception

data class ApiError(
        val errorCode: String,
        val message: String
)
