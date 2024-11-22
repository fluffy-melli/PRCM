package net.shibadogs.prcm.server.routers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import net.shibadogs.prcm.process.Builder
import net.shibadogs.prcm.save.Config
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.save.savexml

var nodelist: Array<Builder> = arrayOf()

@Controller
class WebHTML {
    @GetMapping("/")
    fun index(): String {
        return "index"
    }
}

@RestController
class Router {
    @GetMapping("/api/get-run-node")
    fun getNode(): Array<Builder> {
        return nodelist
    }

    @GetMapping("/api/get-config")
    fun getConfig(): Array<Config> {
        return loadxml("configs.xml")
    }

    @PostMapping("/api/new-config")
    fun newConfig (@RequestBody body: Map<String, Any>): Array<Config> {
        val loadConfigs = loadxml("configs.xml")
        val args: List<String> = (body["args"] as? List<*>)?.filterIsInstance<String>() ?: listOf()
        val config = Config(
            loadConfigs.size,
            body["node"] as String,
            body["workdir"] as String,
            body["file"] as String,
            args
        )
        val updatedConfigs: Array<Config> = loadConfigs + config
        savexml(updatedConfigs, "configs.xml")
        return updatedConfigs
    }
}
