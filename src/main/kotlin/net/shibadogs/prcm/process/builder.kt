package net.shibadogs.prcm.process

import net.shibadogs.prcm.save.Config
import java.io.File

fun builder(id: Int, node: String, workdir: String, path: String, vararg args: String) : Builder {
    val work = File(workdir)
    return Builder(
        node = Node(
            node = node,
            workdir = work,
            filepath = path,
            args = args,
        ),
        processInfo = ProcessINFO(),
        status = Status(
            id = id,
        )
    )
}

fun new(config: Config) : Builder {
    val work = File(config.workdir)
    return Builder(
        node = Node(
            node = config.node,
            workdir = work,
            filepath = config.path,
            args = config.args.toTypedArray(),
        ),
        processInfo = ProcessINFO(),
        status = Status(
            id = config.id,
        )
    )
}