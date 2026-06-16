package org.example.teste2324_en

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UnsafeCyclicSuccessionWithoutLock<T>(
    private val items: Array<T>
) {
    private var index = AtomicInteger(0)


    fun next(): T {

        val idx = index.getAndUpdate { current ->
            if (current + 1 == items.size) 0
            else current + 1

        }
        return items[idx]

    }
}
