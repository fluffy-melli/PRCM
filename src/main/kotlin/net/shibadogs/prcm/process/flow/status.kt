package net.shibadogs.prcm.process.flow

import net.shibadogs.prcm.save.loadxml
import net.shibadogs.prcm.server.nodeList
import net.shibadogs.prcm.server.statusList

fun nodeStatus() {
    val loadConfigs = loadxml("configs.xml")
    for (i in loadConfigs) {
        if (statusList.getOrElse(i.value.id) { null } == null) {
            statusList[i.value.id] = MutableList(60) {false}
        }
        if (nodeList.getOrElse(i.value.id) { null } == null) {
            statusList[i.value.id]?.add(false)
        } else {
            statusList[i.value.id]?.add(true)
        }
        if (statusList[i.value.id]?.size ?: 0 >= 60) {
            statusList[i.value.id]?.subList(0, statusList[i.value.id]!!.size - 60)?.clear()
        }
    }
}