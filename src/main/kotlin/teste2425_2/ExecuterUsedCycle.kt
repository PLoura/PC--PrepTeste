package org.example.teste2425_2

import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun <T,R> Executor.map(
    f: (T)->R, input: List<T>
): List<R>  = suspendCoroutine { cont ->

    val guard = ReentrantLock()

    val results = MutableList<R?>(input.size) {null}
    var completed = input.size
    var done = false

    for (i in input.indices) {
        execute {
            val value = f(input[i])
            var toResume: List<R>? = null

            guard.withLock {

                if (done) {
                    return@withLock
                }

                results[i] = value
                completed--


                if (completed == 0) {
                    done = true
                    toResume = results.map{it as R}
                }
            }

            if (toResume != null) {
                cont.resume(toResume)
            }
        }
    }
}
