package net.shibadogs.prcm.process

import net.shibadogs.prcm.save.Config
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.save.savexml
import net.shibadogs.prcm.server.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant

fun run(processInfo: Builder) : Int {
    if (processlist.getOrElse(processInfo.status.id) { null } != null || !processInfo.status.exit) {
        return 0
    }
    if (logList.getOrElse(processInfo.status.id) { null } == null) {
        logList[processInfo.status.id] = StringBuilder()
    }
    val processBuilder: ProcessBuilder
    if (processInfo.node.filepath == "") {
        processBuilder = ProcessBuilder(processInfo.node.node, *processInfo.node.args)
    } else {
        processBuilder = ProcessBuilder(processInfo.node.node, processInfo.node.filepath, *processInfo.node.args)
    }
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
            var time = getTime()
            val startLogger: String
            if (exists) {
                startLogger = "[$time] :${processInfo.node.workdir}\$ > ${processInfo.node.node} ${processInfo.node.filepath} ${processInfo.node.args.joinToString(" ")} ERR:[${processInfo.status.errCount}/10] PID:${PID(process)}"
            } else {
                startLogger = "[$time] :~\$ > ${processInfo.node.node} ${processInfo.node.filepath} ${processInfo.node.args.joinToString(" ")} ERR:[${processInfo.status.errCount}/10] PID:${PID(process)}"
            }
            logList[processInfo.status.id]?.append(startLogger)?.append("\n")
            nodeList[processInfo.status.id] = processInfo
            processlist[processInfo.status.id] = process
            processInfo.processInfo.pid = PID(process)
            val outReader = Thread {
                val standardOutputReader = BufferedReader(InputStreamReader(process.inputStream, "UTF-8"))
                while (standardOutputReader.readLine().also { line = it } != null) {
                    time = getTime()
                    logList[processInfo.status.id]?.append("[$time] $line")?.append("\n")
                }
            }
            val errReader = Thread {
                val errorOutputReader = BufferedReader(InputStreamReader(process.errorStream, "UTF-8"))
                while (errorOutputReader.readLine().also { line = it } != null) {
                    time = getTime()
                    logList[processInfo.status.id]?.append("[$time] $line")?.append("\n")
                }
            }
            outReader.start()
            errReader.start()
            outReader.join()
            errReader.join()
            val exit = process.waitFor()
            time = getTime()
            val endLogger: String
            if (exists) {
                endLogger = "[$time] :${processInfo.node.workdir}\$ >  Exit: $exit"
            } else {
                endLogger = "[$time] :~\$ > Exit: $exit"
            }
            logList[processInfo.status.id]?.append(endLogger)?.append("\n")
            processInfo.status.endTime = Instant.now().epochSecond
            processInfo.status.exit = true
            processInfo.status.exitCode = exit
            if (processInfo.status.exitCode != 0) {
                processInfo.status.errCount += 1
            }
            processInfo.status.restartCount += 1
        }
        processlist.remove(processInfo.status.id)
        nodeList.remove(processInfo.status.id)
    } catch (e: Exception) {
        logList[processInfo.status.id]?.append(e.printStackTrace())?.append("\n")
    }
    return processInfo.status.exitCode
}

fun stop(processInfo: Builder) : Boolean {
    if (!processInfo.status.exit) {
        processInfo.status.rootExit = true
        processlist[processInfo.status.id]?.destroyForcibly()
        processlist.remove(processInfo.status.id)
        nodeList.remove(processInfo.status.id)
        return true
    }
    return false
}

fun del(id: Int) : Boolean {
    val Configs = loadxml("configs.xml")
    if (Configs.getOrElse(id) { null } == null) {
        return false
    }
    if (nodeList.getOrElse(id) { null } != null) {
        stop(nodeList[id]!!)
        nodeList.remove(id)
    }
    Configs.remove(id)
    savexml(Configs, "configs.xml")

    logList.remove(id)
    memoryUsageList.remove(id)
    statusList.remove(id)
    return true
}

fun add(id: Int, config: Config) : Boolean {
    val Configs = loadxml("configs.xml")
    Configs[id] = config
    savexml(Configs, "configs.xml")
    return true
}

fun edit(id: Int, config: Config) : Boolean {
    val Configs = loadxml("configs.xml")
    if (Configs.getOrElse(id) { null } == null) {
        return false
    }
    if (nodeList.getOrElse(id) { null } != null) {
        stop(nodeList[id]!!)
        nodeList.remove(id)
    }
    Configs[id] = config
    savexml(Configs, "configs.xml")

    logList.remove(id)
    memoryUsageList.remove(id)
    statusList.remove(id)
    return true
}