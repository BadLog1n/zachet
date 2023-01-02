package ratingUniversity

import org.json.JSONArray

class InfoOfStudent {

    fun getFormOfStudy(authText: String): String {
        return authText.substringAfter("[Code] => ").substringBefore(" [Name]")
    }

    fun getGroupOfStudent(authText: String): String {
        return authText.substringAfter("[GroupCode] => ").substringBefore(" [GroupName]")
    }

    fun getSemesterOfStudent(semester: JSONArray): ArrayList<String> {
        val arrayOfSemester = arrayListOf<String>()
        for (i in 0 until semester.length()) {
            arrayOfSemester.add(semester.getJSONObject(i).getString("ID"))
        }
        return arrayOfSemester
    }

}