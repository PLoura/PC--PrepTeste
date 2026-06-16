package org.example.teste2324_2

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UnsafePairSuccession<T>(
    private val items: Array<T>
) {
    private var currIndex = 0

    private val guard = ReentrantLock()

    fun nextConsecutiveItemsPair(): Pair<T, T>? {
        guard.withLock {
            if (currIndex + 2 > items.size) return null
            val first = items[currIndex++]
            val second = items[currIndex++]
            return Pair(first, second)
        }
    }
}