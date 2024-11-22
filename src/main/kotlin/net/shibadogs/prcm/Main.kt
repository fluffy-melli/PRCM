package net.shibadogs.prcm

import net.shibadogs.prcm.command.command
import net.shibadogs.prcm.process.new
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.server.nodelist
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PRCMServer

fun main(args: Array<String>) {
    val currentDir = System.getProperty("user.dir")
    println("Load: $currentDir...")
    val command = command(args)
    if (command.version) {
        println("beta 0.0.1")
    }
    if (command.onloads) {
        try {
            val loadedConfigs = loadxml("configs.xml")
            if (loadedConfigs.isNotEmpty()) {
                for (config in loadedConfigs) {
                    val node = new(config.value)
                    nodelist[config.value.id] = node
                    Thread {
                        net.shibadogs.prcm.process.run(nodelist[node.status.id]!!)
                    }.start()
                    println(nodelist[node.status.id])
                }
            }
        } catch (e: Exception) {
            println("Error loading configurations: ${e.message}")
        }
    }
    runApplication<PRCMServer>()
}
