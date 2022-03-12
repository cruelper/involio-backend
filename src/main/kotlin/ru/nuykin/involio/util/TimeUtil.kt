package ru.nuykin.involio.util

import java.util.*

fun getDateInRightFormat(date: Date): Date{
    val calendar: Calendar = GregorianCalendar()
    calendar.time = date
    calendar.set(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        0, 0, 0
    )
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun getRightCurDate(): Date{
    return getDateInRightFormat(GregorianCalendar().time)
}

fun getRightSomeTimeWithStepDate(date: Date, interval: Int, step: Int): Date{
    val yearAgoDayInCalendar: Calendar = GregorianCalendar()
    yearAgoDayInCalendar.time = date
    yearAgoDayInCalendar.add(interval, step)
    return yearAgoDayInCalendar.time
}
