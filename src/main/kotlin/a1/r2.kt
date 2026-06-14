package org.example.a1



fun main()
{
    println("main start \n")
    val sleeper : Thread = Thread ({
        println("thread start \n")
        Thread.sleep(2000)
        println("thread end \n")
    }, "sleeper")

    startAndObserved(sleeper, 10)
    println("main end \n")

}