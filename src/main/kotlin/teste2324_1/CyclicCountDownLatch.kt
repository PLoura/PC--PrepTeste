package org.example.teste2324_en

import java.util.concurrent.locks.ReentrantLock
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
    private  val waiting = mutableListOf<Request>()

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
                return false
            }
        }
    }

    private fun  signallAllWaitingThreads() {
        while (waiting.isNotEmpty()) {
            waiting.removeFirst().signalled = true
        }
        condition.signalAll()
    }

}