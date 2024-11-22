package prcm.kotlin

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.runApplication
import prcm.kotlin.command.command
import prcm.kotlin.process.new
import prcm.kotlin.save.loadxml
import prcm.kotlin.server.PRCMServer
import prcm.kotlin.server.routers.nodelist

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
                    prcm.kotlin.process.run(nodelist[node.status.id])
                }
            }
        }
        runApplication<PRCMServer>()
    }
}
