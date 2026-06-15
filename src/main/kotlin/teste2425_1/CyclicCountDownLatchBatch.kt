package org.example.teste2425_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class CyclicCountDownLatchBatch (val initialCount: Int) {

    init {
        require(initialCount > 0)
    }

    private var count = initialCount
    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request (var signalled: Boolean = false, var batchSize: Int = 1)
    private var sharedRequest: Request? = null


    @Throws(InterruptedException::class)
    fun countDownAndAwait(timeout: Duration): Boolean {
        guard.withLock {
            if (--count == 0) {
                count = initialCount
                signalAllWaitingThreads()
                return true
            }

            val myRequest = sharedRequest ?: Request()
            myRequest.batchSize += 1
           // waiting.add(myRequest)

            try {
                var remaining = timeout.inWholeNanoseconds

                while (true) {
                    remaining = condition.awaitNanos(remaining)

                    if (myRequest.signalled) {
                        return true
                    }

                    if (remaining <= 0) {
                        myRequest.batchSize -= 1
                        if (myRequest.batchSize == 0) {
                            sharedRequest = null
                        }
                    }

                }
            } catch (ie: InterruptedException) {
                myRequest.batchSize -= 1
                if (myRequest.batchSize == 0) {
                    sharedRequest = null
                }

                Thread.currentThread().interrupt()
                throw ie
            }
        }
    }

    private fun signalAllWaitingThreads() {
        val currentBatch = sharedRequest
        sharedRequest = null
        currentBatch?.let {it.signalled = true} //sinalizar todas threads
        condition.signalAll()

    }
}