package com.example.universitysystem

import java.util.*

data class SubjectGrades(val subject:String, val allGradesCount:Int,val typeOfSubject:String,
                         val grades:IntArray, val userChatId:UUID,
                         val segments:MutableList<String> = mutableListOf("1 к.т.","2 к.т.","3 к.т.","4 к.т."))
