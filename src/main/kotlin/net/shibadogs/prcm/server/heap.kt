package net.shibadogs.prcm.server

import net.shibadogs.prcm.process.Builder

var label: MutableList<String> = MutableList(20) {"x"}
var statusList: MutableMap<Int, MutableList<Boolean>> = mutableMapOf()
var logList: MutableMap<Int, StringBuilder> = mutableMapOf()
var nodeList: MutableMap<Int, Builder> = mutableMapOf()
var processlist: MutableMap<Int, Process> = mutableMapOf()
var memoryUsageList: MutableMap<Int, MutableList<Long>> = mutableMapOf()