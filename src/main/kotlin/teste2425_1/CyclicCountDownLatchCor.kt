package org.example.teste2425_en

import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CyclicCountDownLatchCor(val initialCount: Int) {

    init {
        require(initialCount > 0)
    }

    private var continuations = mutableListOf<Continuation<Unit>>()

    private val guard = Mutex()

    private var count = initialCount

    suspend fun countDownAndAwait(): Unit {
        guard.lock()
            if (--count == 0) {
                count = initialCount
                val toResume = continuations
                continuations = mutableListOf()
                guard.unlock()

                toResume.forEach { it.resume(Unit) }
                return
            }


            //suspend
        suspendCoroutine { continuation ->
            continuations.add(continuation)
            guard.unlock()
        }

    }
}