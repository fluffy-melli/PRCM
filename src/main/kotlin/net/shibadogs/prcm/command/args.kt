package net.shibadogs.prcm.command

data class List (
    var version: Boolean = false
)

fun command(args: Array<String>) : List {
    val co = List()
    for (l in args) {
        if (
            l == "v" ||
            l == "version" ||
            l == "-v" ||
            l == "-version" ||
            l == "--v" ||
            l == "--version"
            ) {
            co.version = true
        }
    }
    return co
}