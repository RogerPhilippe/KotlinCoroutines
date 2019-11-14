fun main(args: Array<String>) {
    exampleBlocking()
}

private fun printLnDelayed(msg: String) {
    Thread.sleep(1000)
    println("$msg - ${getCurrentThreadName()}")
}

private fun exampleBlocking() {
    println("one - ${getCurrentThreadName()}")
    printLnDelayed("two")
    println("tree - ${getCurrentThreadName()}")
}

// ********************************************************
// ************ General Purpose Methods *******************
// ********************************************************

private fun getCurrentThreadName() = Thread.currentThread().name