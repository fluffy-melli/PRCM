package net.shibadogs.prcm.process

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getTime() : String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val time = currentDateTime.format(formatter)
    return time
}

fun getShortTime() : String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val time = currentDateTime.format(formatter)
    return time
}