package com.example.app.jasper.routinedeveloper_v2.model

import java.io.Serializable

class Todo : Serializable {
    var id: Long = -1
    var name: String? = null
    var isDone = false
    var position = 0

    override fun toString(): String {
        return "id: $ id, name: $name, is done: $isDone, position: $position"
    }
}