package net.shibadogs.prcm.server

import net.shibadogs.prcm.process.Builder
import net.shibadogs.prcm.process.new
import net.shibadogs.prcm.process.run
import net.shibadogs.prcm.process.stop
import net.shibadogs.prcm.save.Config
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.save.savexml
import org.springframework.web.bind.annotation.*

@RestController
class Router {
    @GetMapping("/api/get-run-node")
    fun getNode(): MutableMap<Int,Builder> {
        return nodeList
    }

    @GetMapping("/api/get-config")
    fun getConfig(): MutableMap<Int, Config> {
        return loadxml("configs.xml")
    }

    @GetMapping("/api/node/start/{number}")
    fun startNode(@PathVariable number: Int): Boolean {
        val loadConfigs = loadxml("configs.xml")
        if (loadConfigs.getOrElse(number) { null } == null) {
            return false
        } else {
            Thread {
                run(new(loadConfigs[number]!!))
            }.start()
            return true
        }
    }

    @GetMapping("/api/node/stop/{number}")
    fun stopNode(@PathVariable number: Int): Boolean {
        if (processlist.getOrElse(number) { null } == null) {
            return false
        }
        return stop(nodeList[number]!!)
    }

    @GetMapping("/api/node/usage/memory/{number}")
    fun usagememoryNode(@PathVariable number: Int): List<Map<Int, Long>> {
        if (memoryUsageList.getOrElse(number) { null } == null) {
            return listOf()
        }
        return memoryUsageList[number]!!
    }

    @PostMapping("/api/new-config")
    fun newConfig (@RequestBody body: Map<String, Any>): MutableMap<Int, Config> {
        val loadConfigs = loadxml("configs.xml")
        val args: List<String> = (body["args"] as? List<*>)?.filterIsInstance<String>() ?: listOf()
        val config = Config(
            loadConfigs.size,
            body["node"] as String,
            body["workdir"] as String,
            body["file"] as String,
            args
        )
        loadConfigs[loadConfigs.size] = config
        savexml(loadConfigs, "configs.xml")
        return loadConfigs
    }
}
