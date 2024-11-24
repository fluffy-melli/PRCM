package net.shibadogs.prcm.process

import oshi.SystemInfo
import oshi.software.os.OSProcess
import oshi.software.os.OperatingSystem

fun PID(process: Process): Long {
    return process.pid()
}

fun PID_INFO(pid: Long): OSProcess? {
    val systemInfo = SystemInfo()
    val os: OperatingSystem = systemInfo.operatingSystem
    val processes = os.processes
    for (process in processes) {
        if (process.processID == pid.toInt()) {
            return process
        }
    }
    return null
}

fun MemoryUsage(pid: Long): Long {
    val process = PID_INFO(pid)
    val result: Long
    if (process == null) {
        result = 0
    } else {
        result = process.residentSetSize
    }
    return result
}