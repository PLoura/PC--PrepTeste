package org.example.teste2324_1

import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class UnboundedMessageQueue<T> {

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private var isClosed = false

    private data class Request<T> (
        var value: T? = null,
        var signalled: Boolean = false,
    )

    private val messages = ArrayDeque<T>()

    private val waiters = ArrayDeque<Request<T>>()

    @Throws(RejectedExecutionException::class)
    fun enqueue(value: T): Unit {

        guard.withLock {
            if (isClosed) {
                throw RejectedExecutionException()
            }

            if (waiters.isNotEmpty()) {
                val w = waiters.removeFirst()
                w.value = value
                w.signalled = true
            } else {
                messages.add(value)
            }
            condition.signal()
        }

    }

    @Throws(InterruptedException::class)
    fun tryDequeue(timeout: Duration): T? {

        val request = Request<T>()
        var remaining = timeout.inWholeNanoseconds

        guard.withLock {
            if (isClosed && messages.isEmpty()) {
               return null
            }
            if (messages.isNotEmpty()) {
              return messages.removeFirst()
            }

            waiters.addLast(request)

            while (true) {

                if (request.signalled) {
                    return request.value
                }

                if (remaining <= 0) {
                    waiters.remove(request)
                    return null
                }
                remaining = condition.awaitNanos(remaining)
            }
        }
    }

    @Throws(InterruptedException::class)
    fun closeAndWaitForEmpty(timeout: Duration): Boolean {
        var remaining = timeout.inWholeNanoseconds

        guard.withLock {
            isClosed = true
            condition.signalAll()

            while (true) {

                remaining = condition.awaitNanos(remaining)

                if (messages.isEmpty() && waiters.isEmpty()){
                    return  true
                }
                if (remaining <= 0) {
                    return false
                }
            }
        }
    }
}