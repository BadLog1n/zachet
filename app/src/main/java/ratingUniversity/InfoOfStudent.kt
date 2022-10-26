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


    fun getCurrentSemesterOfStudent(currentSemester: JSONArray): ArrayList<String> {
        val arrayOfSemester = arrayListOf<String>()
        for (i in 0 until currentSemester.length()) {
            arrayOfSemester.add(currentSemester.getJSONObject(i).getString("ID"))
        }
        return arrayOfSemester
    }

}


/*            var document = ""
            val connection =
                Jsoup.connect("https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=@$login&password=$password&type=array")
            //val document = connection.get().text()
            try {
                document = connection.get().text()
            } catch (e: Exception) {
                return@launch
            }*/