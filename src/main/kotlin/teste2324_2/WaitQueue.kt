package org.example.teste2324_2

import kotlin.time.Duration
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class WaitQueue {
    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request (
        val thread: Thread,
        var signalled : Boolean = false,
        val condition: Condition
    )

    private val waiters = ArrayDeque<Request>()

    @Throws(InterruptedException::class)
    fun await(timeout: Duration): Boolean {

        val request = Request(
            Thread.currentThread(),
            false,
            guard.newCondition()
        )
        var remaining = timeout.inWholeNanoseconds
        guard.withLock {

            waiters.addLast(request)

            try {
                while (true) {

                    remaining = condition.awaitNanos(remaining)

                    if (request.signalled) {
                        waiters.remove(request)
                    }

                    if (remaining <= 0) {
                        waiters.remove(request)
                    }
                }
            } catch (ie: InterruptedException) {
                waiters.remove(request)
                throw ie
            }
        }
    }
    fun signalOne(): Thread? {
        guard.withLock {
            if (waiters.isEmpty())   {
                return null
            }

            val waiter = waiters.removeFirst()
            waiter.signalled = true
            waiter.condition.signal()
            return waiter.thread
        }
    }
}