package com.example.app.jasper.routinedeveloper_v2.model

import java.io.Serializable

class Todo : Serializable {
    var id: Long = -1
    var name: String? = null
    var isDone = false


    override fun toString(): String {
        return name!!
    }
}