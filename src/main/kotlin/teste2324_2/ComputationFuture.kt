package org.example.teste2324_2

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class ComputationFuture<T>(val provider: () -> T) {

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private var state = 0
    private var result: T? = null
    private var error: Throwable? = null

    init {
        Thread {
            try {
                var r = provider()

                guard.withLock {
                    result = r
                    state = 1
                    condition.signalAll()
                }

            } catch (e: Throwable) {
                guard.withLock {
                    error = e
                    state = 2
                    condition.signalAll()
                }
            }
        }.start()
    }

    @Throws(ExecutionException::class,
        TimeoutException::class,
        InterruptedException::class)

    fun get(timeout: Duration): T {
        guard.withLock {

            var remaining = timeout.inWholeNanoseconds

            while (state == 0) {

                remaining = condition.awaitNanos(remaining)

                if (remaining <= 0) {
                    throw TimeoutException()
                }
            }

            val res =
                if (state == 1) {result as T}
            else {
                throw ExecutionException(error)
                }
            return res
        }
    }
}