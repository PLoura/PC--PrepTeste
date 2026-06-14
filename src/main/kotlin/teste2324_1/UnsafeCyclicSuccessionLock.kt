package org.example.teste2324_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UnsafeCyclicSuccessionLock<T>(
    private val items: Array<T>
) {
    private var index = 0
    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    fun next(): T {
        guard.withLock {
            val res = items[index]
            index += 1
            if (index == items.size) index = 0
            return res
        }
    }
}
