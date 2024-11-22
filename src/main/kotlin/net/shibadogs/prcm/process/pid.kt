package net.shibadogs.prcm.process

fun PID(process: Process): Long {
    return process.pid()
}