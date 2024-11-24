package net.shibadogs.prcm.process.flow

import net.shibadogs.prcm.process.MemoryUsage
import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.server.memoryUsageList
import net.shibadogs.prcm.server.processlist

fun useMemory() {
    val list = loadxml("configs.xml")
    for (i in list) {
        if (memoryUsageList.getOrElse(i.value.id) { null } == null) {
            memoryUsageList[i.value.id] = MutableList(20) {0}
        }
        if (processlist.getOrElse(i.value.id) { null } == null) {
            memoryUsageList[i.value.id]?.add(0)
        } else {
            val pid = (processlist[i.value.id]!!).pid()
            memoryUsageList[i.value.id]?.add(MemoryUsage(pid))
        }
        if (memoryUsageList[i.value.id]?.size!! >= 20) {
            memoryUsageList[i.value.id]?.subList(0, memoryUsageList[i.value.id]?.size!! - 20)?.clear()
        }
        return
    }
}