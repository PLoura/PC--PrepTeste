package org.example.teste2324_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UnsafeCyclicSuccessionWithoutLock<T>(
    private val items: Array<T>
) {
    private var index = 0
    private val guard = ReentrantLock()

    fun next(): T {
        TODO()
    }
}
