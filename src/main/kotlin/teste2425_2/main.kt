package org.example.teste2425_er


    fun main() {

        val pool = FixedThreadPool(3)

        repeat(10) { i ->
            pool.execute {
                println("Task $i by ${Thread.currentThread().name}")
                Thread.sleep(200)
            }
        }

        Thread.sleep(1500)

        pool.shutdown()

        pool.awaitTermination()

        println("POOL TERMINATED")
    }
