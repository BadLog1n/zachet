package ratingUniversity

import android.util.Log
import org.json.JSONArray
import org.json.JSONException

/**
 * Класс для разбора JSONа рейтинга
 */
class RatingUniversity {

    /**
     * Функция для разбора JSON документа, которая записывает данные о предметах и баллах в массив
     * строковых словарей. Каждый элемент массива - словарь с параметрами одного предмета.
     */
    fun gradesCollector(jsonArray: JSONArray): ArrayList<MutableMap<String, String>> {
        val globalArrayToReturn = arrayListOf<MutableMap<String, String>>()
        for (i in 0 until jsonArray.length()) {
            val localArray = mutableMapOf(
                "getSubjectName" to "",
                "ratingScore" to "",
                "subjectType" to "",
                "rating" to "",
                "tutorName" to "",
                "tutorId" to "",
            )
            val subjectType = getSubjectType(jsonArray, i)
            val getSubjectName = getSubjectName(jsonArray, i)
            val tutorArr = getTutorNameAndID(jsonArray, i)
            val tutorName = tutorArr[1]
            val tutorId = tutorArr[0]
            var rating = getRating(jsonArray, i)
            while (rating.length < 16) {
                rating += "0 0 "
            }
            var ratingScore = 0
            for (item in rating.split(" ").toTypedArray()) {
                if (item != "") {
                    ratingScore += item.toInt()
                }
            }
            val additional = getDopolPbZed(jsonArray, i)
            for (item in additional) {
                ratingScore += item.toInt()
                rating += "$item "
            }

            rating.dropLast(1)
            localArray["getSubjectName"] = getSubjectName
            localArray["ratingScore"] = ratingScore.toString()
            localArray["subjectType"] = subjectType
            localArray["rating"] = rating
            localArray["tutorName"] = tutorName
            localArray["tutorId"] = tutorId
            globalArrayToReturn.add(localArray)
        }
        return globalArrayToReturn

    }

    /** Функция для получения баллов не за контрольные точки (премиальных, дополнительных или
     *  за экзамен/зачет) из JSONа по индексу объекта предмета из параметра "ReitingCode" и
     *  вложенных параметров.
     * zed - баллы за экзамен или зачет
     * dopol - дополнительные баллы
     * pb - премиальные баллы
     * index - порядковый номер предмета
     */
    private fun getDopolPbZed(jsonArray: JSONArray, index: Int): Array<String> {
        val arrayToReturn: Array<String> = arrayOf("0", "0", "0")
        try {

            val ratingArray = jsonArray.getJSONObject(index).getJSONObject("ReitingCode")
            try {
                val exam = ratingArray.getJSONArray("zed").getJSONObject(0).getString("Ball")
                arrayToReturn[0] = exam
            } catch (e: JSONException) {
                Log.d("e", e.toString())
            }
            try {
                val additional =
                    ratingArray.getJSONArray("dopol").getJSONObject(0).getString("Ball")
                arrayToReturn[1] = additional
            } catch (e: JSONException) {
                Log.d("e", e.toString())
            }
            try {
                val premium = ratingArray.getJSONArray("pb").getJSONObject(0).getString("Ball")
                arrayToReturn[2] = premium

            } catch (e: JSONException) {
                Log.d("e", e.toString())
            }


        } catch (e: JSONException) {
            Log.d("e", e.toString())

        }
        return arrayToReturn
    }

    /**
     * Функция для получения баллов за контрольные точки из JSONа по индексу объекта предмета
     * из параметра "ReitingCode" вложенных параметров:
     * pos - баллы за посещение
     * usp - баллы за успеваемость
     */
    private fun getRating(jsonArray: JSONArray, index: Int): String {
        var stringToReturn = ""
        try {
            val ratingArray = jsonArray.getJSONObject(index).getJSONObject("ReitingCode")
            val arrayKt = arrayOf("1kt", "2kt", "3kt", "4kt")
            for (item in arrayKt) {
                try {
                    val ratingObject = ratingArray.getJSONObject(item)
                    val visitScore = ratingObject.getJSONObject("pos").getString("Ball")
                    val recordScore = ratingObject.getJSONObject("usp").getString("Ball")
                    stringToReturn += "$visitScore $recordScore "
                } catch (_: JSONException) {
                }
            }
        } catch (e: JSONException) {
            Log.d("e", e.toString())
        }
        return stringToReturn
    }


    /**
     * Функция для получения типа предмета(зачет или экзамен) из документа JSON по индексу объекта
     * и имени параметра "LoadName" в нем
     */
    private fun getSubjectType(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("LoadName")
    }

    /**
     * Функция для получения названия предмета из документа JSON по индексу объекта
     * и имени параметра "SubjName" в нем
     */
    private fun getSubjectName(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("SubjName")
    }


    /**
     * Функция для получения ФИО и id преподавателя из документа JSON по индексу объекта (предмета),
     * в котором есть массив преподавателей "TutorArr". Из массива преподавателей получаем первого
     * по индексу и извлекаем информацию по именам параметров "FIO" и "ID".
     * Возвращает строковый массив с двумя элементами - ФИО и id преподавателя
     */
    private fun getTutorNameAndID(jsonArray: JSONArray, index: Int): ArrayList<String> {
        val localArray = arrayListOf("", "")
        val tutorArray = jsonArray.getJSONObject(index).getJSONArray("TutorArr")

        return if (tutorArray.length() != 0) {
            val tutorFirst = tutorArray.getJSONObject(0)
            localArray[0] = tutorFirst.getString("FIO")
            localArray[1] = tutorFirst.getString("ID")
            localArray

        } else localArray
    }


}
