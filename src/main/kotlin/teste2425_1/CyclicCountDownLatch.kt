package org.example.teste2425_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.time.Duration

class CyclicCountDownLatch(val initialCount: Int) {

    init {
        require(initialCount > 0)
    }

    private var count = initialCount
    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request (var signalled: Boolean = false)
    private val waiting = mutableListOf<Request>()


    @Throws(InterruptedException::class)
    fun countDownAndAwait(timeout: Duration): Boolean {
        guard.withLock {
            if (--count == 0) {
                count = initialCount
                signallAllWaitingThreads()
                return true
            }

            val myRequest = Request()
            waiting.add(myRequest)

            try {
                var remaining = timeout.inWholeNanoseconds

                while (true) {
                    remaining = condition.awaitNanos(remaining)

                    if (myRequest.signalled) {
                        return true
                    }

                    if (remaining <= 0) {
                        waiting.remove(myRequest)
                        return false
                    }

                }
            } catch (ie: InterruptedException) {
                waiting.remove(myRequest)
                Thread.currentThread().interrupt()
                throw ie
            }
        }
    }

    private fun signallAllWaitingThreads() {
        while (waiting.isNotEmpty()) {
            waiting.removeFirst().signalled = true
        }
        condition.signalAll()
    }
}