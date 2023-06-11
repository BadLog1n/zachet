package com.oneseed.zachet.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.oneseed.zachet.R
import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.repository.Repository
import org.json.JSONArray
import org.json.JSONException
import org.jsoup.Connection
import org.jsoup.Jsoup


class GetRatingImpl(private val context: Context) : Repository.GetRating {
    private val sharedPrefGrades: SharedPreferences?
        get() {
            return context?.getSharedPreferences(
                context.getString(R.string.gradesShared), Context.MODE_PRIVATE
            )
        }

    override suspend fun getRating(
//        login: String,
//        group: String,
//        semester: String,
//        form: String,
//        status: String,
        semNumSpinner: Int,
        getRatingCallback: (ArrayList<SubjectGrades>) -> Unit
    ) {
        val actualGrades =
            sharedPrefGrades?.getString(context.getString(R.string.actualGrades), "").toString()
                .split(" ").toList().toMutableList()
        val sharedPrefSetting: SharedPreferences? = context?.getSharedPreferences(
            context.getString(R.string.settingsShared), Context.MODE_PRIVATE
        )
        val login =
            sharedPrefGrades?.getString(context.getString(R.string.loginWebShared), "").toString()
        val password =
            sharedPrefGrades?.getString(context.getString(R.string.passwordWebShared), "")
                .toString()
        val strSemesterOriginal =
            sharedPrefGrades?.getString(context.getString(R.string.listOfSemester), "").toString()
        val strSemester =
            sharedPrefGrades?.getString(context.getString(R.string.listOfSemesterToChange), "")
                .toString()
        val group =
            sharedPrefGrades?.getString(context.getString(R.string.groupOfStudent), "").toString()
        val form =
            sharedPrefGrades?.getString(context.getString(R.string.formOfStudent), "").toString()
        val ls =
            sharedPrefGrades?.getInt(context.getString(R.string.lastSemester), 0).toString().toInt()


        val status = if (semNumSpinner + 1 >= ls) "false" else "true"
        val semester = semNumSpinner.toString().padStart(9, '0') //дополнение нулями впереди

        getRatingCallback(returnRating(login, group, semester, form, status)!!)//мы тут возвращаем только ретурн рейтинг мы возвращаем то что
    }

    /**
     * Функция для запроса к сайту с параметром reiting
     * Запрос происходит с помощью Jsoup
     * Возвращает разобранный в массив строковых словарей JSON с данными о рейтинге по предметам
     */ //+
    private fun returnRating(
        login: String, group: String, semester: String, form: String, status: String
    ): ArrayList<SubjectGrades>? {
        try {
            val document: String
            val sitePath =
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=reiting&uid=$login&group=$group&semestr=$semester&status=$status&fo=$form&type=json"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(10000).execute()

            val statusCode: Int = response.statusCode()

            if (statusCode == 200) {
                document = Jsoup.connect(sitePath).get().text()
            } else return null
            val jsonArray = JSONArray(document)
            return gradesCollector(jsonArray)
        } catch (e: Exception) {
            return null
        }
    }

    private fun gradesCollector(jsonArray: JSONArray): ArrayList<SubjectGrades> {
        val globalArrayToReturn = arrayListOf<SubjectGrades>()
        for (i in 0 until jsonArray.length()) {

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
            globalArrayToReturn.add(
                SubjectGrades(
                    getSubjectName,
                    ratingScore,
                    subjectType,
                    rating,
                    tutorName,
                    tutorId
                )
            )
        }
        return globalArrayToReturn

    }

    /** Функция для получения баллов не за контрольные точки (премиальных, дополнительных или
     *  за экзамен/зачет) из JSONа по индексу объекта предмета из параметра "ReitingCode" и
     *  вложенных параметров.так
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
