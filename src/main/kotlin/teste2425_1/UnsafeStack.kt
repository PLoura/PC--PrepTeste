package org.example.teste2425_en

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UnsafeStack<T> {

    class Node<T>(
        val value: T,
        val next: Node<T>?
    )

    private var head: Node<T>? = null
    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    fun push(value: T) {
        guard.withLock {
            head = Node(value, head)
        }
    }

    fun pop(): T? {
        guard.withLock {
            val observedHead = head

            return if (observedHead != null) {
                head = observedHead.next
                observedHead.value
            } else {
                null
            }
        }
    }
}