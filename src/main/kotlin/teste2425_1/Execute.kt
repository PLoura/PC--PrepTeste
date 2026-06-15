package org.example.teste2425_1

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

interface Executer {
    fun execute(task: () -> Unit)
}

class PairHolder<T1, T2> {
    private var first : T1? = null
    private var second : T2? = null
    private val guard = ReentrantLock()

    fun setFirst (value: T1): Pair<T1,T2>? =
        guard.withLock {
            first = value
            second?.let { Pair(first = value, second = it)}
        }


    fun setSecond (value: T2): Pair<T1,T2>? =
        guard.withLock {
            second  = value
            first?.let { Pair(first = it, second = value)}
        }

}

suspend fun <T1,T2> Executer.invoke(
    f1: ()->T1,
    f2: ()->T2
): Pair<T1,T2> =

    suspendCoroutine { continuation ->
        val holder = PairHolder<T1, T2>()
        execute {
            holder.setFirst(f1())?.let {
                continuation.resume(it)
            }
        }

        execute {
            holder.setSecond(f2())?.let {
                continuation.resume(it)
            }
        }

    }



