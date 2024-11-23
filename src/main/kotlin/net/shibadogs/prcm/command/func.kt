package net.shibadogs.prcm.command

import net.shibadogs.prcm.process.new
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.server.nodeList
import kotlin.system.exitProcess

fun run(command: List) {
    if (command.version) {
        println("beta 0.0.1")
        exitProcess(0)
    }
    if (command.onloads) {
        try {
            val loadedConfigs = loadxml("configs.xml")
            if (loadedConfigs.isNotEmpty()) {
                for (config in loadedConfigs) {
                    val node = new(config.value)
                    nodeList[config.value.id] = node
                    Thread {
                        net.shibadogs.prcm.process.run(nodeList[node.status.id]!!)
                    }.start()
                    println(nodeList[node.status.id])
                }
            }
        } catch (e: Exception) {
            println("Error loading configurations: ${e.message}")
        }
    }
}