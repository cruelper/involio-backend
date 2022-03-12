package ru.nuykin.involio.dto

data class CurrencyDto(
    val id: String,
    val name: String,
    val sign: String,
    val idOnYahooApi: String,
)
