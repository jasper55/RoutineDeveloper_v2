package com.example.app.jasper.routinedeveloper_v2.model

import java.io.Serializable

class Todo : Serializable {
    var id: Long = -1
    var name: String? = null
    var isChecked = false
    var position = 0
    var doneCounts = 0
    var undoneCounts = 0

    override fun toString(): String {
        return "id: $ id, name: $name, is done: $isChecked, position: $position, doneCounts: $doneCounts, undoneCounts: $undoneCounts"
    }
}