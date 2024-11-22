package net.shibadogs.prcm.save

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

data class Config(
    val id: Int,
    val node: String,
    val workdir: String,
    val path: String,
    val args: List<String>
)

fun savexml(configs: Array<Config>, filename: String) {
    try {
        val document: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val root = document.createElement("Configs")
        document.appendChild(root)
        for (config in configs) {
            val configElement = document.createElement("Config")
            root.appendChild(configElement)
            val idElement = document.createElement("id")
            idElement.appendChild(document.createTextNode(config.id.toString()))
            configElement.appendChild(idElement)
            val nodeElement = document.createElement("node")
            nodeElement.appendChild(document.createTextNode(config.node))
            configElement.appendChild(nodeElement)
            val workdirElement = document.createElement("workdir")
            workdirElement.appendChild(document.createTextNode(config.workdir))
            configElement.appendChild(workdirElement)
            val pathElement = document.createElement("path")
            pathElement.appendChild(document.createTextNode(config.path))
            configElement.appendChild(pathElement)
            val argsElement = document.createElement("args")
            for (arg in config.args) {
                val argElement = document.createElement("arg")
                argElement.appendChild(document.createTextNode(arg))
                argsElement.appendChild(argElement)
            }
            configElement.appendChild(argsElement)
        }
        val transformer = TransformerFactory.newInstance().newTransformer()
        val source = DOMSource(document)
        val result = StreamResult(File(filename))
        transformer.transform(source, result)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadxml(filename: String): Array<Config> {
    try {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(filename))
        document.documentElement.normalize()
        val configNodes = document.getElementsByTagName("Config")
        val configs = mutableListOf<Config>()
        for (i in 0 until configNodes.length) {
            val configElement = configNodes.item(i) as Element
            val id = configElement.getElementsByTagName("id").item(0).textContent.toInt()
            val node = configElement.getElementsByTagName("node").item(0).textContent
            val workdir = configElement.getElementsByTagName("workdir").item(0).textContent
            val path = configElement.getElementsByTagName("path").item(0).textContent
            val argsNodeList = configElement.getElementsByTagName("arg")
            val args = mutableListOf<String>()
            for (j in 0 until argsNodeList.length) {
                val argElement = argsNodeList.item(j) as Element
                args.add(argElement.textContent)
            }
            configs.add(Config(id, node, workdir, path, args))
        }
        return configs.toTypedArray()
    } catch (e: Exception) {
        return emptyArray()
    }
}