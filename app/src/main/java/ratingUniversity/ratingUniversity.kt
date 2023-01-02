package ratingUniversity

import android.util.Log
import org.json.JSONArray
import org.json.JSONException


class RatingUniversity {

    fun gradesCollector(jsonArray: JSONArray): ArrayList<MutableMap<String, String>> {
        val globalArrayToReturn = arrayListOf<MutableMap<String, String>>()

        for (i in 0 until jsonArray.length()) {
            //val localArray = arrayListOf<String>()
            val localArray = mutableMapOf(
                "getSubjectName" to "",
                "ratingScore" to "",
                "subjectType" to "",
                "rating" to "",
                "tutorName" to "",
                "tutorId" to "",
            )
            // ID
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


    private fun getSubjectType(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("LoadName")
    }


    private fun getSubjectName(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("SubjName")
    }


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
