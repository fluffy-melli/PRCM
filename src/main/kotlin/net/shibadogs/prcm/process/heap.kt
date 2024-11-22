package net.shibadogs.prcm.process

import java.io.File

data class Status (
    val id: Int,
    var startTime: Long = -1,
    var endTime: Long = -1,
    val out: StringBuilder = StringBuilder(),
    val err: StringBuilder = StringBuilder(),
    var errCount: Int = 0,
    var restartCount: Int = 0,
    var exit: Boolean = false,
    var exitCode: Int = -1,
)

data class Node (
    val node: String,
    val workdir: File?,
    val filepath: String,
    val args: Array<out String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (node != other.node) return false
        if (workdir != other.workdir) return false
        if (filepath != other.filepath) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = node.hashCode()
        result = 31 * result + workdir.hashCode()
        result = 31 * result + filepath.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}

data class ProcessINFO (
    var pid: Long = -1,
)

data class Builder (
    val node: Node,
    val processInfo: ProcessINFO,
    val status: Status,
)

data class Processing (
    val node: Builder,
    var info: Process
)