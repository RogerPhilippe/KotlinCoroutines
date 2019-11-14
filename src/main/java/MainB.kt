import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    exampleWithContext()
}

private suspend fun printLnDelayed(msg: String) {
    delay(1000)
    println("$msg - ${getCurrentThreadName()}")
}

private suspend fun calculateHardThings(startNum: Int): Int {
    delay(1000)
    return startNum * 10
}

/**
 * Todos prints rodarão na thread principal
 */
private fun exampleBlocking() {
    println("one - ${getCurrentThreadName()}")
    runBlocking { printLnDelayed("two") }
    println("tree - ${getCurrentThreadName()}")
}

/**
 * Segundo print rodará em um outro thread
 */
private fun exampleBlockingB() {
    println("one - ${getCurrentThreadName()}")
    runBlocking(Dispatchers.Default) { printLnDelayed("two") }
    println("tree - ${getCurrentThreadName()}")
}

/**
 * Todos prints rodarão na thread principal
 */
private fun exampleBlockingC() = runBlocking {
    println("one - ${getCurrentThreadName()}")
    printLnDelayed("two")
    println("tree - ${getCurrentThreadName()}")
}

/**
 * Todos rodarão em outra thread
 */
private fun exampleBlockingD() = runBlocking(Dispatchers.Default) {
    println("one - ${getCurrentThreadName()}")
    printLnDelayed("two")
    println("tree - ${getCurrentThreadName()}")
}

/**
 * O primeiro e segundo print rodarão em outra thread
 */
private fun exampleBlockingE() {
    runBlocking(Dispatchers.Default) {
        println("one - ${getCurrentThreadName()}")
        printLnDelayed("two")
    }
    println("tree - ${getCurrentThreadName()}")
}

/**
 * Não haverá tempo de rodar o segundo print, já que ele possui um delay
 */
private fun exampleLaunchGlobal() = runBlocking {
    println("one - ${getCurrentThreadName()}")
    GlobalScope.launch {
        printLnDelayed("two")
    }
    println("tree - ${getCurrentThreadName()}")
}

/**
 * O segundo print irá rodar, já que aguardamos um tempo maior, porém será o ultimo
 */
private fun exampleLaunchGlobalB() = runBlocking {
    println("one - ${getCurrentThreadName()}")
    GlobalScope.launch {
        printLnDelayed("two")
    }
    println("tree - ${getCurrentThreadName()}")
    delay(3000)
}

/**
 * Todos irão rodar, e segundo em uma outra thread. Será o último a rodar.
 */
private fun exampleLaunchGlobalC() = runBlocking {
    println("one - ${getCurrentThreadName()}")
    val job = GlobalScope.launch {
        printLnDelayed("two")
    }
    println("tree - ${getCurrentThreadName()}")
    job.join()
}

/**
 * Todos irão rodar na trhead principal, e segundo em uma outra thread. Será o último a rodar.
 */
private fun exampleLaunchCoroutineScope() = runBlocking {
    println("one - ${getCurrentThreadName()}")
    launch {
        printLnDelayed("two")
    }
    println("tree - ${getCurrentThreadName()}")
}

/**
 * O segundo print irá rodar um uma outra thread, e serpa o último a terminar.
 */
private fun exampleLaunchCoroutineScopeB() = runBlocking {
    println("one - ${getCurrentThreadName()}")
    launch(Dispatchers.Default) {
        printLnDelayed("two")
    }
    println("tree - ${getCurrentThreadName()}")
}

/**
 * Segundo print roda na thread customizada, porém o programa fica travado.
 */
private fun exampleLaunchCustomExecutor() = runBlocking {
    println("one - ${getCurrentThreadName()}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printLnDelayed("two")
    }

    println("tree - ${getCurrentThreadName()}")
}

/**
 * Segundo print roda na thread customizada, e o programa é liberado.
 */
private fun exampleLaunchCustomExecutorB() = runBlocking {
    println("one - ${getCurrentThreadName()}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printLnDelayed("two")
    }

    println("tree - ${getCurrentThreadName()}")

    (customDispatcher.executor as ExecutorService).shutdown()

}

/**
 * Dessa maneira, o processo é mais demorado - Está levando em média tres vezes mais que o proximo exemplo. +- 3037 ms
 * Está rodando na treah principal
 */
private fun exampleAsyncAwait() = runBlocking {
    val startTime = System.currentTimeMillis()
    val deferred1 = async { calculateHardThings(10) }.await()
    val deferred2 = async { calculateHardThings(20) }.await()
    val deferred3 = async { calculateHardThings(30) }.await()
    val sum = deferred1 + deferred2 + deferred3
    println("async/await result = $sum - Thread - ${getCurrentThreadName()}")
    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

/**
 * Melhor maneira - Esta demorando tres vezes menos que o primeiro exemplo +- 1030 ms
 */
private fun exampleAsyncAwaitBest() = runBlocking {
    val startTime = System.currentTimeMillis()
    val deferred1 = async { calculateHardThings(10) }
    val deferred2 = async { calculateHardThings(20) }
    val deferred3 = async { calculateHardThings(30) }
    val sum = deferred1.await() + deferred2.await() + deferred3.await()
    println("async/await result = $sum - Thread - ${getCurrentThreadName()}")
    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

/**
 * Demora até tres vezes que o exemplo anterior.
 */
private fun exampleWithContext() = runBlocking {
    val startTime = System.currentTimeMillis()
    val deferred1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
    val deferred2 = withContext(Dispatchers.Default) { calculateHardThings(20) }
    val deferred3 = withContext(Dispatchers.Default) { calculateHardThings(30) }
    val sum = deferred1 + deferred2 + deferred3
    println("async/await result = $sum - Thread - ${getCurrentThreadName()}")
    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

// ********************************************************
// ************ General Purpose Methods *******************
// ********************************************************

private fun getCurrentThreadName() = Thread.currentThread().name