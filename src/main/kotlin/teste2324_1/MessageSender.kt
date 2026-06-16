package org.example.teste2324_1

import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageSender<T> {

    private val guard = Mutex()

    private val waiters = ArrayDeque<Continuation<T>>()

    suspend fun waitForMessage(): T {

        guard.lock()
         return suspendCoroutine { cont ->
            waiters.addLast(cont)
            guard.unlock()
        }
    }

    suspend fun sendToOne(message: T): Boolean {
        guard.lock()

        if(waiters.isEmpty()) {
            guard.unlock()
            return false
        }

        val waiter = waiters.removeFirst()
        guard.unlock()

        waiter.resume(message)
        return true
    }
}