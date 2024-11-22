package net.shibadogs.prcm.server

import net.shibadogs.prcm.process.Builder

var nodelist: MutableMap<Int, Builder> = mutableMapOf()
var processlist: MutableMap<Int, Process> = mutableMapOf()