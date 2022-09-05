package com.example.universitysystem

data class SubjectGrades(val subject:String, val allGradesCount:Int,val typeOfSubject:String,
                         val grades:List<Int>, val userChatId:String, val FIO1:String, val userChatId2:String,
                         val FIO2:String,val segments:MutableList<String> = mutableListOf("1 к.т.","2 к.т.","3 к.т.","4 к.т."))

