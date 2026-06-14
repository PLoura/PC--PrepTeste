package org.example.teste2425_1


import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

class PeriodicMessage(val message: String, val delayBetweenPresentations: Duration) {

    // não é trade-safe

    private val guard = Mutex()

    fun show(showString: (String) -> Unit, totalDuration: Duration, vararg periodicMessages: PeriodicMessage)  =
        runBlocking {
            //para mostrar o time //pai que ao terminar
            val outerJob = launch {
                periodicMessages.forEach {
                    launch {
                        //lanças varias coroutines
                        while (true) {
                            guard.withLock {
                                showString(it.message)
                            }

                            delay(it.delayBetweenPresentations)
                        }
                    }
                }
            }
            //para finalizar todas propaga-se aos filhos
            delay(totalDuration)
            // join implicito
            outerJob.cancel()
        }

    }
