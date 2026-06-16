package org.example.teste2324_1

import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun <T> either(f1: CompletableFuture<T>, f2: CompletableFuture<T>): T {

   return suspendCoroutine { cont ->
        val guard = ReentrantLock()
        var completed = false

        fun complete(value: T) {
            guard.lock()
            if (completed) {
                guard.unlock()
                return
            }
            completed = true
            guard.unlock()

            cont.resume(value)
        }

        f1.thenAccept { value ->
            complete(value)
        }

        f2.thenAccept { value ->
            complete(value)
        }
    }
}