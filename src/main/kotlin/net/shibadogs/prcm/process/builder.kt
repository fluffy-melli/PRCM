package net.shibadogs.prcm.process

import net.shibadogs.prcm.save.Config
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.Instant

fun builder(id: Int, node: String, workdir: String, path: String, vararg args: String) : Builder {
    val work = File(workdir)
    return Builder(
        node = Node(
            node = node,
            workdir = work,
            filepath = path,
            args = args,
        ),
        processInfo = ProcessINFO(),
        status = Status(
            id = id,
        )
    )
}

fun new(config: Config) : Builder {
    val work = File(config.workdir)
    return Builder(
        node = Node(
            node = config.node,
            workdir = work,
            filepath = config.path,
            args = config.args.toTypedArray(),
        ),
        processInfo = ProcessINFO(),
        status = Status(
            id = config.id,
        )
    )
}

fun run(processInfo: Builder) : Int {
    val processBuilder = ProcessBuilder(processInfo.node.node, processInfo.node.filepath, *processInfo.node.args)
    processBuilder.redirectErrorStream(false)
    val exists: Boolean = processInfo.node.workdir?.exists() ?: false
    if (exists) {
        processBuilder.directory(processInfo.node.workdir)
    }
    try {
        while (processInfo.status.errCount <= 10) {
            processInfo.status.startTime = Instant.now().epochSecond
            processInfo.status.endTime = -1
            processInfo.status.exit = false
            var line: String?
            val process = processBuilder.start()
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