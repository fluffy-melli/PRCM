package net.shibadogs.prcm.process.flow

import net.shibadogs.prcm.process.getShortTime
import net.shibadogs.prcm.server.label
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun newFlow() {
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    scheduler.scheduleAtFixedRate({
        useMemory()
        label.add(getShortTime())
        if (label.size >= 20) {
            label = label.takeLast(20).toMutableList()
        }
    }, 0, 5, TimeUnit.SECONDS)
}

fun newFlowStatus() {
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    scheduler.scheduleAtFixedRate({
        nodeStatus()
    }, 0, 1, TimeUnit.MINUTES)
}