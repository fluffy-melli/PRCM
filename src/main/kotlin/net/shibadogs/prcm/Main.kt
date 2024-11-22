package net.shibadogs.prcm

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.shibadogs.prcm.command.command
import net.shibadogs.prcm.process.new
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.server.PRCMServer
import net.shibadogs.prcm.server.routers.nodelist
import org.springframework.boot.runApplication

fun main(args: Array<String>): Unit = runBlocking {
    val currentDir = System.getProperty("user.dir")
    println("Load: $currentDir...")
    val command = command(args)
    if (command.version) {
        println("beta 0.0.1")
    } else {
        val loadedConfigs = loadxml("configs.xml")
        if (loadedConfigs.isNotEmpty()) {
            for (config in loadedConfigs) {
                val node = new(config)
                nodelist = nodelist.plus(node)
                println(nodelist[node.status.id])
                launch {
                    net.shibadogs.prcm.process.run(nodelist[node.status.id])
                }
            }
        }
        launch {
            runApplication<PRCMServer>()
        }
    }
}
