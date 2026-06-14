package org.example.teste2425_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class CyclicCountDownLatchCycles (val initialCount: Int) {

    init {
        require(initialCount > 0)
    }

    private var count = initialCount
    private var cycleCount = 0

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request (var signalled: Boolean = false, var batchSize: Int = 1)
    private var sharedRequest: Request? = null


    @Throws(InterruptedException::class)
    fun countDownAndAwait(timeout: Duration): Boolean {
        guard.withLock {
            if (--count == 0) {
                count = initialCount
               // signallAllWaitingThreads()
                return true
            }

            val myCycle = cycleCount
           // waiting.add(myRequest)

           var remaining = timeout.inWholeNanoseconds

            while (true) {
                remaining = condition.awaitNanos(remaining)

                if (cycleCount > myCycle) {
                    return true
                }

                if (remaining <= 0) {
                    return false
                }

            }
        }
    }

    private fun signallAllWaitingThreads() {
        cycleCount += 1
        condition.signalAll()//sinalizar todas threads
    }
}