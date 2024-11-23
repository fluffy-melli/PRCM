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

fun MemoryUsage(pid: Long): Map<Int, Long> {
    val process = PID_INFO(pid)
    val result: MutableMap<Int, Long> = mutableMapOf()
    if (process == null) {
        result[0] = 0
        result[1] = 0
    } else {
        result[0] = process.virtualSize
        result[1] = process.residentSetSize
    }
    return result
}