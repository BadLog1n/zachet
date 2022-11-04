package ratingUniversity

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

            for (item in additional) {
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


    fun getDopolPbZed(jsonArray: JSONArray, index: Int): Array<String> {
        val arrayToReturn: Array<String> = arrayOf("0", "0", "0")
        val ratingArray = jsonArray.getJSONObject(index).getJSONObject("ReitingCode")
        try {
            val exam = ratingArray.getJSONArray("zed").getJSONObject(0).getString("Ball")
            arrayToReturn[0] = exam
        } catch (e: JSONException) {
            arrayToReturn[0] = "Error"
            return arrayToReturn
        }
        try {
            val additional =
                ratingArray.getJSONArray("dopol").getJSONObject(0).getString("Ball")
            arrayToReturn[1] = additional
        } catch (e: JSONException) {
            arrayToReturn[1] = "Error"
            return arrayToReturn
        }
        try {
            val premium = ratingArray.getJSONArray("pb").getJSONObject(0).getString("Ball")
            arrayToReturn[2] = premium

        } catch (e: JSONException) {
            arrayToReturn[2] = "Error"

            return arrayToReturn
        }
        return arrayToReturn
    }

    fun getRating(jsonArray: JSONArray, index: Int): String {

        var stringToReturn = ""
        try {
            val ratingArray = jsonArray.getJSONObject(index).getJSONObject("ReitingCode")
            val arrayKt = arrayOf("1kt", "2kt", "3kt", "4kt")
            for (item in arrayKt) {

                val ratingObject = ratingArray.getJSONObject(item)
                val visitScore = ratingObject.getJSONObject("pos").getString("Ball")
                val recordScore = ratingObject.getJSONObject("usp").getString("Ball")
                stringToReturn += "$visitScore $recordScore "

            }
        } catch (e: JSONException) {
            stringToReturn = "Error"
            return stringToReturn
        }

        return stringToReturn
    }


    fun getSubjectType(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("LoadName")
    }


    fun getSubjectName(jsonArray: JSONArray, index: Int): String {
        return jsonArray.getJSONObject(index).getString("SubjName")
    }


    fun getTutorNameAndID(jsonArray: JSONArray, index: Int): ArrayList<String> {
        val localArray = arrayListOf<String>()
        val tutorArray = jsonArray.getJSONObject(index).getJSONArray("TutorArr")
        val tutorFirst = tutorArray.getJSONObject(0)
        localArray.add(tutorFirst.getString("FIO"))
        localArray.add(tutorFirst.getString("ID"))
        return localArray
    }


}
