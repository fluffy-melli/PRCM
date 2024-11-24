package net.shibadogs.prcm.server

import net.shibadogs.prcm.process.Builder

var label: MutableList<Long> = MutableList(20) {0}
var logList: MutableMap<Int, StringBuilder> = mutableMapOf()
var nodeList: MutableMap<Int, Builder> = mutableMapOf()
var processlist: MutableMap<Int, Process> = mutableMapOf()
var memoryUsageList: MutableMap<Int, MutableList<Long>> = mutableMapOf()