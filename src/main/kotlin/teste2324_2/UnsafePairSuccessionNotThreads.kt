package org.example.teste2324_2

import java.util.concurrent.atomic.AtomicInteger

class UnsafePairSuccessionNotThreads<T>(
    private val items: Array<T>
) {
    private var currIndex = AtomicInteger(0)

    fun nextConsecutiveItemsPair(): Pair<T, T>? {

        val idx = currIndex.getAndAdd(2)

        if (idx + 2 > items.size) return null

        return Pair(items[idx], items[idx+1])

    }
}