package com.oneseed.zachet.data

import android.content.Context
import android.content.SharedPreferences
import com.oneseed.zachet.R
import com.oneseed.zachet.domain.repository.Repository


class GetSemesterListImpl(private val context: Context) : Repository.GetSemesterList {
    override fun getSemesterList(getSemesterListCallback: (result: Array<String>?) -> Unit) {
        val sharedPrefGrades: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.gradesShared), Context.MODE_PRIVATE
        )
        val loginWeb =
            sharedPrefGrades.getString((context.getString(R.string.loginWebShared)), "").toString()
        val passwordWeb =
            sharedPrefGrades.getString((context.getString(R.string.passwordWebShared)), "")
                .toString()
        val strSemester =
            sharedPrefGrades.getString((context.getString(R.string.listOfSemesterToChange)), "")
                .toString()
        getSemesterListCallback(isDataCorrect(loginWeb, passwordWeb, strSemester))
    }

    private fun isDataCorrect(loginWeb: String, passwordWeb: String, strSemester: String): Array<String>? {
        return if (loginWeb != "" && passwordWeb != "" && strSemester != "")
            strSemester.split(",").toTypedArray() else null
    }

}
