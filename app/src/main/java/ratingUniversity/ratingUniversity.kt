package ratingUniversity

import android.util.Log
import org.json.JSONArray
import org.json.JSONException


class RatingUniversity {

    fun gradesCollector(jsonArray: JSONArray): ArrayList<ArrayList<String>> {
        val globalArrayToReturn = arrayListOf<ArrayList<String>>()

        for (i in 0 until jsonArray.length()) {
            val localArray = arrayListOf<String>()

            // ID
            val subjectType = getSubjectType(jsonArray, i)
            val getSubjectName = getSubjectName(jsonArray, i)
            val tutorArr = getTutorNameAndID(jsonArray, i)
            val tutorName = tutorArr[0]
            val tutorId = tutorArr[1]
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

            for (item in additional){
                ratingScore += item.toInt()
                rating += "$item "
            }

            rating.dropLast(1)
            localArray.add(getSubjectName)
            localArray.add(ratingScore.toString())
            localArray.add(subjectType)
            localArray.add(rating)
            localArray.add(tutorName)
            localArray.add(tutorId)
            globalArrayToReturn.add(localArray)
        }
        return globalArrayToReturn

    }


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

    private fun getRating(jsonArray: JSONArray, index: Int): String {
        var stringToReturn = ""
        try {
            val ratingArray = jsonArray.getJSONObject(index).getJSONObject("ReitingCode")
            // val ratingArray = JSONArray(ratingArrayString)

            //val ratingArray = ratingArrayString.getJSONObject(index).getString("ReitingCode")

            val arrayKt = arrayOf("1kt", "2kt", "3kt", "4kt")
            for (item in arrayKt) {
                try {
                    val ratingObject = ratingArray.getJSONObject(item)
                    val visitScore = ratingObject.getJSONObject("pos").getString("Ball")
                    val recordScore = ratingObject.getJSONObject("usp").getString("Ball")
                    stringToReturn += "$visitScore $recordScore "
                } catch (e: JSONException) {
                }
            }
        } catch (e: JSONException) {
            Log.d("e", e.toString())
        }
        return stringToReturn
    }


    private fun getSubjectType(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("LoadName")
    }


    private fun getSubjectName(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("SubjName")
    }


    private fun getTutorNameAndID(jsonArray: JSONArray, index: Int): ArrayList<String> {
        val localArray = arrayListOf("", "")
        val tutorArray = jsonArray.getJSONObject(index).getJSONArray("TutorArr")

/*
        val document: JSONArray = JSONArray("[{\"ID\":\"2520\",\"FIO\":\"Аникина Елена Игоревна\",\"Email\":\"elenaanikina@inbox.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/povt\\/index.php\"]}]")
        //val tutorArray = tutorArrayString.getJSONObject(index).getJSONObject("TutorArr")
        val tutorJSON = JSONArray(tutorArrayString)
        //val tutorArray = tutorJSON.getJSONObject(index).getJSONObject("TutorArr")
        val additional = ratingArray.getJSONObject("dopol").getJSONObject("0").getString("Ball")
*/

        return if (tutorArray.length() != 0) {
            val tutorFirst = tutorArray.getJSONObject(0)
            localArray[0] = tutorFirst.getString("FIO")
            localArray[1] = tutorFirst.getString("ID")
            localArray

        } else localArray
    }


}

/*            sg = SubjectGrades(
                "Экономика",
                36,
                "зачет",
                grArray,
                arrayTest[1],
                "Иванов Петр Петрович",
                arrayTest[0],
                "Аникина Елена Игоревна"
            )*/