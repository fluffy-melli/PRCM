package net.shibadogs.prcm.process.calculate

import net.shibadogs.prcm.server.statusList

fun percent(): MutableMap<Int, Int> {
    val result: MutableMap<Int, Int> = mutableMapOf()
    for (i in statusList) {
        var runs = 0
        if (i.value.isEmpty()) {
            result[i.key] = 0
            continue
        }
        for (ck in i.value) {
            if (ck) {
                runs += 1
            }
        }
        if (runs == 0) {
            result[i.key] = 0
            continue
        }
        if (runs == i.value.size) {
            result[i.key] = 100
            continue
        }
        result[i.key] = (runs.toDouble() / i.value.size * 100).toInt()
    }
    return result
}