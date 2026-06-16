package org.example.teste2324_2

import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class CyclicCountDownLatch(val initialCount: Int) {
    init { require(initialCount > 0) }

    private val guard = Mutex()

    private var count = initialCount

    private val continuations = ArrayDeque<Continuation<Unit>>()

    suspend fun countDown(): Int {

        guard.lock()
        if (--count == 0) {
            count = initialCount
            val toResume = continuations.toList()
            continuations.clear()

            guard.unlock()
            //acordam todas as coroutines
            toResume.forEach { it.resume(Unit) }
            return toResume.size
        }
        guard.unlock()
        //mão acorda coroutines, estão a terminar
        return 0
    }

    suspend fun await(): Unit {
        guard.lock()
        suspendCoroutine<Unit> { cont ->
            continuations.add(cont)
            guard.unlock()
        }
    }
}

