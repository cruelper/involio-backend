package ru.nuykin.involio.dto

data class CurrencyDto(
    val id: String,
    val name: String,
    val sign: Char,
    val idOnYahooApi: String,
)
