package org.example.teste2425_er

import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UnaryCapacityStream<T> {

    private val guard = Mutex()

    private var currentIndex = -1
    private var currentValue: T? = null

    private data class Waiter<T>(
        val wantedIndex: Int,
        val continuation: Continuation<Pair<Int,T>>
    )

    private val waiters = mutableListOf<Waiter<T>>()

    suspend fun add(item: T): Int {
        guard.lock()
        try {

            currentIndex += 1
            currentValue = item

            val result = currentIndex to item // cria Pair
            //identifica os waiters que tem valores <= que o novo currentIndex
            val toWake = waiters.filter { it.wantedIndex <= currentIndex }
            //remove estes waiters da lista, porque vão ver satisfeitos os seus requisitos
            waiters.removeAll(toWake)
            // vai acordar os waiters a satisfazer
            toWake.forEach {
                it.continuation.resume(result)
            }

            return currentIndex
        } finally {
            guard.unlock()
        }
    }

    suspend fun read(index: Int): Pair<Int, T> {
        guard.lock()
        //se atinge valos >= não suspende
        if (currentIndex >= index) {
            guard.unlock()
            return Pair(currentIndex, currentValue!!)
        }
       // aqui suspende
        return suspendCoroutine { cont ->
            waiters.add(Waiter(index, cont))
            guard.unlock()
        }
    }
}