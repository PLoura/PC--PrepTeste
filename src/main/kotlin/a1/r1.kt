package org.example.a1

fun startAndObserved (target: Thread, pollIntervalMs : Long = 50L) {
    val startTime = pollIntervalMs
    var lastState = target.state

    println("0 ms ${target.name} $lastState")
    target.start()
    while (true) {
        val currentState = target.state
        val timestamp = System.currentTimeMillis() - startTime
        if (lastState != currentState) {
            println("$timestamp ms ${target.name} $currentState")
            lastState = currentState
        }
        if (currentState == Thread.State.TERMINATED) break
        Thread.sleep(pollIntervalMs)
    }
}
