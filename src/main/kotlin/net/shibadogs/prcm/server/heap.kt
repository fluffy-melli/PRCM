package net.shibadogs.prcm.server

import net.shibadogs.prcm.process.Builder

var nodeList: MutableMap<Int, Builder> = mutableMapOf()
var processlist: MutableMap<Int, Process> = mutableMapOf()
var memoryUsageList: MutableMap<Int, List<Map<Int, Long>>> = mutableMapOf()