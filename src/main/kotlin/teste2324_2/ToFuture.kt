package org.example.teste2324_2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.CompletableFuture

fun <T> toFuture(
    scope: CoroutineScope,
    provider: suspend () -> T
) : CompletableFuture<T> {

    val future = CompletableFuture<T>()

    scope.launch {
        try {
            var result = provider()

            future.complete(result)
        } catch (e: Throwable) {
            future.completeExceptionally(e)

        }

    }
    return future
}
