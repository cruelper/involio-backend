package ru.nuykin.involio.dto

import java.util.Date

data class DividendsDto(
    var date_dividends: Date,
    var absolut_size: Double,
    var relative_size: Double,
)
