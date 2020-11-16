package com.example.app.jasper.routinedeveloper_v2.model

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.widget.Toast
import com.example.app.jasper.routinedeveloper_v2.repository.TodoListRepository
import com.example.app.jasper.routinedeveloper_v2.viewmodel.MainActivityViewModel
import java.util.*



class MySharedPrefs(context: Context) {
    var endingDate: String? = null
    var scorePlus: String? = null
        private set
    var scoreMinus: String? = null
        private set
    private val repo: TodoListRepository? = null
    private val prefs: SharedPreferences
    private val editor: Editor
    fun updateDate() {
        val currentday = Calendar.DAY_OF_YEAR
        editor.putInt(STOREDDAY, currentday)
        editor.apply()
    }

    fun loadSharedPrefs() {
        endingDate = prefs.getString(DATE, "")
        scorePlus = prefs.getString(SCOREPLUS, "0")
        scoreMinus = prefs.getString(SCOREMINUS, "0")
    }

    fun saveSharedPrefs() {
//        if (endingDate != null) {
//            val endingDate = repo!!.endingDate.value
//            this.endingDate = endingDate
//        }
//        if (repo!!.scorePlus != null) {
//            val scorePlus = repo.scorePlus.value
//            val scoreMinus = repo.scoreMinus.value
//            this.scorePlus = scorePlus
//            this.scoreMinus = scoreMinus
//        }
//        editor.putString(DATE, endingDate)
//        editor.putString(SCOREPLUS, scorePlus)
//        editor.putString(SCOREMINUS, scoreMinus)
//        editor.apply()
    }

    fun setScoreplus(plus: String?) {
        editor.putString(SCOREPLUS, plus)
        editor.apply()
    }

    fun setScorepMinus(minus: String?) {
        editor.putString(SCOREMINUS, minus)
        editor.apply()
    }

    fun applyPrefsToView(mainActivityViewModel: MainActivityViewModel) {
        mainActivityViewModel.setChallengeEndingDate(endingDate)
        mainActivityViewModel.setDoneCounter(scorePlus)
        mainActivityViewModel.setUndoneCounter(scoreMinus)
    }

    fun firstTimeStartingApp(context: Context) {
        val firstStart = prefs.getBoolean(FIRSTSTART, true)
        if (firstStart) {
            Toast.makeText(context.applicationContext, "Welcome to Routine Developer!", Toast.LENGTH_SHORT).show()
            updateDate()
            editor.putBoolean(FIRSTSTART, false)
            editor.apply()
        }
    }

    fun updateScore() {
        saveSharedPrefs()
    }

    fun clearDate() {
        editor.putString(DATE, null)
        editor.apply()
    }

    companion object {
        private const val SHARED_PREFS = "sharedPrefs"
        private const val DATE = "date"
        private const val SCOREPLUS = "scorePlus"
        private const val SCOREMINUS = "scoreMinus"
        private const val STOREDDAY = "storedDay"
        private const val FIRSTSTART = "firstStart"
//        private val context: Context
//            get() {
//
//            }
        private var instance: MySharedPrefs? = null
        @JvmStatic
        fun getInstance(context: Context): MySharedPrefs? {
            if (instance == null) {
                instance = MySharedPrefs(context)
            }
            return instance
        }
    }

    init {
//        this.context = context
        prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }
}