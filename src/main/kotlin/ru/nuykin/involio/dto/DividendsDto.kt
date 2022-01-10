package ru.nuykin.involio.dto

import java.sql.Date

data class DividendsDto(
    var date_dividends: Date,
    var absolut_size: Double,
    var relative_size: Double,
)
