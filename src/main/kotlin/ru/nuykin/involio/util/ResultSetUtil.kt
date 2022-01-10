package ru.nuykin.involio.util

import java.sql.ResultSet

private fun ResultSet.getIntOrNull(columnLabel: String): Int?{
    val value: Int = this.getInt(columnLabel)
    return if (this.wasNull()) null else value
}