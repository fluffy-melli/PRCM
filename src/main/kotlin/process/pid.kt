package prcm.kotlin.process

fun PID(process: Process): Long {
    return process.pid()
}