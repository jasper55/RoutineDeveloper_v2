package com.example.app.jasper.routinedeveloper_v2.repository

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceHelper {

    const val SHARED_PREFS = "sharedPrefs"
    const val END_DATE = "date"
    const val DONE_COUNTER = "scorePlus"
    const val UNDONE_COUNTER = "scoreMinus"
    const val STORED_DAY = "storedDay"
    const val FIRST_START = "firstStart"

    var challengeEndingDate: String
        get() = get(END_DATE,"")
        set(value) = put(END_DATE, value)

    var storedDay: Int
        get() = get(STORED_DAY,0)
        set(value) = put(STORED_DAY, value)

    var doneCount
        get() = get(DONE_COUNTER,0)
        set(value) = put(DONE_COUNTER, value)

    var undoneCount
        get() = get(UNDONE_COUNTER,0)
        set(value) = put(UNDONE_COUNTER, value)

    var firstStart: Boolean
        get() = get(FIRST_START, true)
        set(value) = put(FIRST_START, value)

    private var prefs: SharedPreferences? = null

    fun initWith(context: Context) {
        if (prefs == null) prefs =     context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    fun clearAll() {
        if (prefs == null) throw RuntimeException("Initialize PreferenceHelper first with #initWith(context).")
        prefs?.edit()?.clear()?.apply()
    }

    fun updateDate(currentDay: Int) {
        storedDay = currentDay
    }



    private inline fun <reified T> get(key: String, defaultValue: T): T {

        if (prefs == null) throw RuntimeException("Initialize PreferenceHelper first with #initWith(context).")

        return when (T::class) {
            Boolean::class -> prefs?.getBoolean(key, defaultValue as Boolean) as T
            Float::class -> prefs?.getFloat(key, defaultValue as Float) as T
            Int::class -> prefs?.getInt(key, defaultValue as Int) as T
            Long::class -> prefs?.getLong(key, defaultValue as Long) as T
            String::class -> prefs?.getString(key, defaultValue as String) as T
            else -> defaultValue
        }
    }

    private inline fun <reified T> put(key: String, value: T) {

        if (prefs == null) throw RuntimeException("Initialize PreferenceHelper first with #initWith(context).")

        val editor = prefs?.edit()

        when (T::class) {
            Boolean::class -> editor?.putBoolean(key, value as Boolean)
            Float::class -> editor?.putFloat(key, value as Float)
            Int::class -> editor?.putInt(key, value as Int)
            Long::class -> editor?.putLong(key, value as Long)
            String::class -> editor?.putString(key, value as String)
        }

        editor?.apply()
    }
}