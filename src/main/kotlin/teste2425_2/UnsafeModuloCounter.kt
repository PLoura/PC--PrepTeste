package org.example.teste2425_er

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UnsafeModuloCounter(
    private val modulo: Int,
) {
    init {
        require(modulo > 1)
    }

    private var counter: Int = 0
    private val guard = ReentrantLock()

    fun increment(): Int {
        guard.withLock {
            counter += 1

            if (counter == modulo) {
                counter = 0
            }

            return counter
        }
    }
}