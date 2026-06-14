package org.example.teste2324_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class CyclicCountDownLatchCyvle(val initialCount: Int) {

    init {
        require(initialCount > 0)
    }

    private var count = initialCount
    private var cycleCount = 0
    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    fun countDown(): Int {
        guard.withLock {
            if (-- count == 0) {
                count = initialCount
                signallAllWaitingThreads()
            }

            return count
        }
    }

    @Throws(InterruptedException::class)
    fun await(timeout: Duration): Boolean {

        guard.withLock {
            val myCycle = cycleCount

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

    private fun  signallAllWaitingThreads() {
        cycleCount += 1
        condition.signalAll()
    }

}