package com.acap.demo.lesson

import com.acap.ec.BaseEvent
import com.acap.ec.listener.OnEventNextListener
import java.sql.DriverManager.println


class SimperEvent() : BaseEvent<Int, Int>() {
    override fun onCall(params: Int) {
        val result = params * params
        next(result)
    }
}

fun main2() {

SimperEvent()
    .listener(OnEventNextListener { println("result:$it") })
    .chain(SimperEvent())
    .listener(OnEventNextListener { println("result:$it") })
    .start(2)

}


/**
 *
 */
class AddEvent2(private val value: Int) : BaseEvent<Int, Int>() {
    override fun onCall(params: Int?) {
        next(value + (params ?: 0))
    }
}


fun main() {

    AddEvent2(2)
        .listener(OnEventNextListener { println("结果：$it") })
        .start(1)


}
