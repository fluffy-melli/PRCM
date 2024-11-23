package net.shibadogs.prcm

import net.shibadogs.prcm.command.command
import net.shibadogs.prcm.command.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PRCMServer

fun main(args: Array<String>) {
    val currentDir = System.getProperty("user.dir")
    println("Load: $currentDir...")
    run(command(args))
    runApplication<PRCMServer>()
}
