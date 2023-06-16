package com.oneseed.zachet.data

import android.content.Context
import android.content.SharedPreferences
import com.oneseed.zachet.R
import com.oneseed.zachet.domain.repository.Repository


class ChangeGradListImpl(private val context: Context) : Repository.ChangeGradList {
    override fun changeGradList(currentSemester: String) {
        val sharedPrefGrades: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.gradesShared), Context.MODE_PRIVATE)
        val strSemesterOriginal =
            sharedPrefGrades.getString(context.getString(R.string.listOfSemester), "").toString()
        val semesterList = "$currentSemester," + strSemesterOriginal.replace(
            "$currentSemester,", ""
        )
        sharedPrefGrades.edit()
            ?.putString(context.getString(R.string.listOfSemesterToChange), semesterList)?.apply()

    }
}