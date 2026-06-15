package org.example.teste2425_2

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

fun <T1, T2> both(
    f1: suspend ()->T1,
    f2: suspend ()->T2,
    timeout: Duration
) : Pair<T1, T2>? {  // ou = se return
    return  runBlocking {
        withTimeoutOrNull(timeout) {
            coroutineScope {
                val frist = async { f1() }
                val second = async { f2() }

                Pair(first = frist.await(), second = second.await())
            }
        }
    }
} // eliminar , casi =
