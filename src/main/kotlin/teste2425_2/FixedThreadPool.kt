package org.example.teste2425_er

import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock



class FixedThreadPool(
    nOfThreads: Int,
) {

    private val workQueue = ArrayDeque<Runnable>()

    private var activeWorkers = nOfThreads
    //As worker threads devem ser criadas quando a instância de FixedThreadPool
    // é criada e só devem terminar após ser realizado o primeiro shutdown .
    init {
        require(nOfThreads >= 1)

        repeat(nOfThreads) {
            val t = Thread {
                println("Worker started: ${Thread.currentThread().name}")

                //semelhante ao A2-R2, mas fixo, sem timeOute fora do execute
                workerLoop()
            }
            t.start()
        }
    }

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private var shutdownSignal = false

    @Throws(RejectedExecutionException::class)
    fun execute(runnable: Runnable) {
        guard.withLock {
            // Neste modo, todas as chamadas ao meetodo execute devera
            // lancar a excepcao RejectedExecutionException .
            if (shutdownSignal) {
                throw RejectedExecutionException()
            }
            workQueue.add(runnable)
            condition.signalAll()
        }
    }

    fun shutdown() {
        guard.withLock {
            shutdownSignal = true
            condition.signalAll()
        }
    }

    //O mé todo awaitTermination permite a qualquer thread invocante sincronizar-se
    // com a conclusão do processo de encerramento do thread pool , isto é, aguarda
    // até que sejam executados todos os comandos aceites e que todas as w orker threads
    // activas terminem. Este método deve suportar o protocolo de interrupções
    //  da plataforma Java.
    @Throws(InterruptedException::class)
    fun awaitTermination() {
        guard.withLock {
            while (true) {
                if (workQueue.isEmpty() && shutdownSignal && activeWorkers == 0) {
                    return
                }
                condition.await()
            }
        }
    }

    private fun workerLoop() {

        while (true) {
            val task = guard.withLock {
                while (workQueue.isEmpty() && !shutdownSignal) {
                    condition.await()
                }
                if(shutdownSignal && workQueue.isEmpty()) {
                    activeWorkers--
                    println("${Thread.currentThread().name} terminating" +
                            "(remaining=$activeWorkers)"
                    )

                    if (activeWorkers == 0) {
                        condition.signalAll()
                    }

                    return
                }

                workQueue.removeFirst()

            }

            try {
                task.run()
            } finally {
                guard.withLock {
                    if (shutdownSignal &&
                        workQueue.isEmpty() &&
                        activeWorkers == 0
                    ) {
                        condition.signalAll()
                    }
                }

            }
        }
    }
}