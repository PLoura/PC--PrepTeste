package org.example.teste2425_1

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import kotlin.time.Duration

class PeriodicMessageEndTime(val message: String, val delayBetweenPresentations: Duration) {

    // não é trade-safe

    private val guard = Mutex()

    fun show(showString: (String) -> Unit, totalDuration: Duration, vararg periodicMessages: PeriodicMessageEndTime)  =
        runBlocking {
            launch {
                //para mostrar o time //pai que ao terminar
                val endTime = Instant.now().plusMillis(totalDuration.inWholeMilliseconds)
                periodicMessages.forEach { periodicMessage ->
                    launch {
                        //lanças varias coroutines
                        while (Instant.now() < endTime) {
                            guard.withLock {
                                showString(periodicMessage.message)
                            }
                            delay(periodicMessage.delayBetweenPresentations)
                        }
                    }
                }
            }

        }

    }