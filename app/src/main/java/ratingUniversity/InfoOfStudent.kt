package ratingUniversity

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.jsoup.Jsoup

class InfoOfStudent {

    @OptIn(DelicateCoroutinesApi::class)
    fun getFormOfStudy(authText: String): String {
        return authText.substringAfter("[Code] => ").substringBefore(" [Name]")
    }

    fun getGroupOfStudent(authText: String): String {
        return authText.substringAfter("[GroupCode] => ").substringBefore(" [GroupName]")
    }

    fun getPastSemesterOfStudent(pastSemester: JSONArray): ArrayList<String> {
        val arrayOfSemester = arrayListOf<String>()
        for (i in 0 until pastSemester.length()) {
            arrayOfSemester.add(pastSemester.getJSONObject(i).getString("ID"))
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