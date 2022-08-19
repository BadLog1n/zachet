package com.example.universitysystem

data class SubjectGrades(val subject:String, val allGradesCount:Int,val typeOfSubject:String,
                         val grades:IntArray,
                         val segments:MutableList<String> = mutableListOf("1 к.т.","2 к.т.","3 к.т.","4 к.т."))
