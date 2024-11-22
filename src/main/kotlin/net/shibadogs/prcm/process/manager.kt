package net.shibadogs.prcm.process

import net.shibadogs.prcm.server.nodelist
import net.shibadogs.prcm.server.processlist
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant

fun run(processInfo: Builder) : Int {
    if (processlist.getOrElse(processInfo.status.id) { null } != null || !processInfo.status.exit) {
        return 0
    }
    nodelist[processInfo.status.id] = processInfo
    val processBuilder = ProcessBuilder(processInfo.node.node, processInfo.node.filepath, *processInfo.node.args)
    processBuilder.redirectErrorStream(false)
    val exists: Boolean = processInfo.node.workdir?.exists() ?: false
    if (exists) {
        processBuilder.directory(processInfo.node.workdir)
    }
    try {
        while (processInfo.status.errCount <= 10) {
            if (processInfo.status.rootExit) {
                processInfo.processInfo.pid = -1
                processInfo.status.endTime = Instant.now().epochSecond
                processInfo.status.exit = true
                processInfo.status.exitCode = 130
                processInfo.status.rootExit = false
                break
            }
            processInfo.status.startTime = Instant.now().epochSecond
            processInfo.status.endTime = -1
            processInfo.status.exit = false
            var line: String?
            val process = processBuilder.start()
            processlist[processInfo.status.id] = process
            processInfo.processInfo.pid = PID(process)
            val standardOutputReader = BufferedReader(InputStreamReader(process.inputStream))
            while (standardOutputReader.readLine().also { line = it } != null) {
                processInfo.status.out.append(line).append("\n")
            }
            val errorOutputReader = BufferedReader(InputStreamReader(process.errorStream))
            while (errorOutputReader.readLine().also { line = it } != null) {
                processInfo.status.err.append(line).append("\n")
            }
            val exit = process.waitFor()
            processInfo.status.endTime = Instant.now().epochSecond
            processInfo.status.exit = true
            processInfo.status.exitCode = exit
            if (processInfo.status.exitCode != 0) {
                processInfo.status.errCount += 1
            }
            processInfo.status.restartCount += 1
        }
    } catch (e: Exception) {
        processInfo.status.err.append(e.printStackTrace()).append("\n")
    }
    return processInfo.status.exitCode
}

fun stop(processInfo: Builder) : Boolean{
    if (!processInfo.status.exit) {
        processInfo.status.rootExit = true
        processlist[processInfo.status.id]?.destroyForcibly()
        processlist.remove(processInfo.status.id)
        nodelist.remove(processInfo.status.id)
        return true
    }
    return false
}