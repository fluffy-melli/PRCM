package net.shibadogs.prcm.command

data class List (
    var version: Boolean = false,
    var onloads: Boolean = false
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
        if (
            l == "l" ||
            l == "load" ||
            l == "-l" ||
            l == "-load" ||
            l == "--l" ||
            l == "--load"
        ) {
            co.onloads = true
        }
    }
    return co
}