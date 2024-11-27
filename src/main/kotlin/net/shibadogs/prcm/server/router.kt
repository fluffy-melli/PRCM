package net.shibadogs.prcm.server

import net.shibadogs.prcm.process.*
import net.shibadogs.prcm.save.Config
import net.shibadogs.prcm.save.loadxml
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

    @GetMapping("/api/node/get-log/{number}")
    fun getLog(@PathVariable number: Int): StringBuilder {
        if (logList.getOrElse(number) { null } == null) {
            return StringBuilder()
        }
        return logList[number]!!
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

    @GetMapping("/api/node/usage/{number}")
    fun usageMemoryNode(@PathVariable number: Int): MutableMap<String, Any> {
        val respond: MutableMap<String, Any> = mutableMapOf()
        respond["label"] = label
        if (memoryUsageList.getOrElse(number) { null } == null) {
            respond["usage-memory"] = MutableList(20) {0}
        } else {
            respond["usage-memory"] = memoryUsageList[number]!!
        }
        return respond
    }

    @PostMapping("/api/new-config")
    fun newConfig (@RequestBody body: Map<String, Any>): Boolean {
        val loadConfigs = loadxml("configs.xml")
        val args: List<String> = (body["args"] as? List<*>)?.filterIsInstance<String>() ?: listOf()
        val config = Config(
            loadConfigs.size,
            body["node"] as String,
            body["workdir"] as String,
            body["file"] as String,
            args
        )
        return add(loadConfigs.size, config)
    }

    @PostMapping("/api/edit-config")
    fun editConfig (@RequestBody body: Map<String, Any>): Boolean {
        val args: List<String> = (body["args"] as? List<*>)?.filterIsInstance<String>() ?: listOf()
        val config = Config(
            body["id"] as Int,
            body["node"] as String,
            body["workdir"] as String,
            body["file"] as String,
            args
        )
        return edit(body["id"] as Int, config)
    }

    @PostMapping("/api/del-config")
    fun delConfig (@RequestBody body: Map<String, Any>): Boolean {
        return del(body["id"] as Int)
    }
}
