package ratingUniversity

import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Test

class RatingUniversityTest {

    private val rating = RatingUniversity()

    private val document =
        "[\n" +
                "  {\n" +
                "    \"Comment\": \"\",\n" +
                "    \"FacultCode\": \"000000027\",\n" +
                "    \"FacultName\": \"Факультет фундаментальной и прикладной информатики\",\n" +
                "    \"FormObCode\": \"000000001\",\n" +
                "    \"GroupCode\": \"Э00000133\",\n" +
                "    \"GroupName\": \"ПО-91б\",\n" +
                "    \"LoadCode\": \"000000002\",\n" +
                "    \"LoadName\": \"Экзамен\",\n" +
                "    \"MoodleCourseID\": \"6528\",\n" +
                "    \"RaspisID\": \"44928\",\n" +
                "    \"ReitingCode\": {\n" +
                "      \"1kt\": {\n" +
                "        \"pos\": {\n" +
                "          \"Ball\": \"4\"\n" +
                "        },\n" +
                "        \"usp\": {\n" +
                "          \"Ball\": \"12\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"2kt\": {\n" +
                "        \"pos\": {\n" +
                "          \"Ball\": \"4\"\n" +
                "        },\n" +
                "        \"usp\": {\n" +
                "          \"Ball\": \"12\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"3kt\": {\n" +
                "        \"pos\": {\n" +
                "          \"Ball\": \"4\"\n" +
                "        },\n" +
                "        \"usp\": {\n" +
                "          \"Ball\": \"12\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"4kt\": {\n" +
                "        \"pos\": {\n" +
                "          \"Ball\": \"4\"\n" +
                "        },\n" +
                "        \"usp\": {\n" +
                "          \"Ball\": \"12\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"dopol\": [\n" +
                "        {\n" +
                "          \"Ball\": \"0\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"pb\": [\n" +
                "        {\n" +
                "          \"Ball\": \"0\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"zed\": [\n" +
                "        {\n" +
                "          \"Ball\": \"36\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    \"SemestrCode\": \"000000007\",\n" +
                "    \"SemestrName\": \"Шестой семестр\",\n" +
                "    \"SubjCode\": \"000000772\",\n" +
                "    \"SubjName\": \"Базы данных\",\n" +
                "    \"SubjStatus\": \"t\",\n" +
                "    \"TutorArr\": [\n" +
                "      {\n" +
                "        \"Email\": \"elenaanikina@inbox.ru\",\n" +
                "        \"FIO\": \"Аникина Елена Игоревна\",\n" +
                "        \"ID\": \"2520\",\n" +
                "        \"KafedrUrl\": [\n" +
                "          \"https://swsu.ru/structura/up/fivt/povt/index.php\"\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"TutorCode\": \"000001245\",\n" +
                "    \"UID\": \"19-06-0245\"\n" +
                "  }\n" +
                "]"
    private val jsonArray: JSONArray = JSONArray(document)
    private val index = 0

    @Test
    fun getDopolPbZed() {
        assertEquals("36", rating.getDopolPbZed(jsonArray, index)[0])
        assertEquals("0", rating.getDopolPbZed(jsonArray, index)[1])
        assertEquals("0", rating.getDopolPbZed(jsonArray, index)[2])
    }

    @Test
    fun getDopolPbZedFirstError() {
        val document =
            "[\n" +
                    "  {\n" +
                    "    \"Comment\": \"\",\n" +
                    "    \"FacultCode\": \"000000027\",\n" +
                    "    \"FacultName\": \"Факультет фундаментальной и прикладной информатики\",\n" +
                    "    \"FormObCode\": \"000000001\",\n" +
                    "    \"GroupCode\": \"Э00000133\",\n" +
                    "    \"GroupName\": \"ПО-91б\",\n" +
                    "    \"LoadCode\": \"000000002\",\n" +
                    "    \"LoadName\": \"Экзамен\",\n" +
                    "    \"MoodleCourseID\": \"6528\",\n" +
                    "    \"RaspisID\": \"44928\",\n" +
                    "    \"ReitingCode\": {\n" +
                    "      \"1kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"2kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"3kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"4kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"pb\": [\n" +
                    "        {\n" +
                    "          \"Ball\": \"0\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"zed\": [\n" +
                    "        {\n" +
                    "          \"Ball\": \"36\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    \"SemestrCode\": \"000000007\",\n" +
                    "    \"SemestrName\": \"Шестой семестр\",\n" +
                    "    \"SubjCode\": \"000000772\",\n" +
                    "    \"SubjName\": \"Базы данных\",\n" +
                    "    \"SubjStatus\": \"t\",\n" +
                    "    \"TutorArr\": [\n" +
                    "      {\n" +
                    "        \"Email\": \"elenaanikina@inbox.ru\",\n" +
                    "        \"FIO\": \"Аникина Елена Игоревна\",\n" +
                    "        \"ID\": \"2520\",\n" +
                    "        \"KafedrUrl\": [\n" +
                    "          \"https://swsu.ru/structura/up/fivt/povt/index.php\"\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"TutorCode\": \"000001245\",\n" +
                    "    \"UID\": \"19-06-0245\"\n" +
                    "  }\n" +
                    "]"
        val jsonArray = JSONArray(document)
        val index = 0
        assertEquals("Error", rating.getDopolPbZed(jsonArray, index)[1])
    }

    @Test
    fun getDopolPbZedSecondError() {
        val document =
            "[\n" +
                    "  {\n" +
                    "    \"Comment\": \"\",\n" +
                    "    \"FacultCode\": \"000000027\",\n" +
                    "    \"FacultName\": \"Факультет фундаментальной и прикладной информатики\",\n" +
                    "    \"FormObCode\": \"000000001\",\n" +
                    "    \"GroupCode\": \"Э00000133\",\n" +
                    "    \"GroupName\": \"ПО-91б\",\n" +
                    "    \"LoadCode\": \"000000002\",\n" +
                    "    \"LoadName\": \"Экзамен\",\n" +
                    "    \"MoodleCourseID\": \"6528\",\n" +
                    "    \"RaspisID\": \"44928\",\n" +
                    "    \"ReitingCode\": {\n" +
                    "      \"1kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"2kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"3kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"4kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"dopol\": [\n" +
                    "        {\n" +
                    "          \"Ball\": \"0\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"zed\": [\n" +
                    "        {\n" +
                    "          \"Ball\": \"36\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    \"SemestrCode\": \"000000007\",\n" +
                    "    \"SemestrName\": \"Шестой семестр\",\n" +
                    "    \"SubjCode\": \"000000772\",\n" +
                    "    \"SubjName\": \"Базы данных\",\n" +
                    "    \"SubjStatus\": \"t\",\n" +
                    "    \"TutorArr\": [\n" +
                    "      {\n" +
                    "        \"Email\": \"elenaanikina@inbox.ru\",\n" +
                    "        \"FIO\": \"Аникина Елена Игоревна\",\n" +
                    "        \"ID\": \"2520\",\n" +
                    "        \"KafedrUrl\": [\n" +
                    "          \"https://swsu.ru/structura/up/fivt/povt/index.php\"\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"TutorCode\": \"000001245\",\n" +
                    "    \"UID\": \"19-06-0245\"\n" +
                    "  }\n" +
                    "]"
        val jsonArray = JSONArray(document)
        val index = 0
        assertEquals("Error", rating.getDopolPbZed(jsonArray, index)[2])
    }

    @Test
    fun getDopolPbZedThirdError() {
        val document =
            "[\n" +
                    "  {\n" +
                    "    \"Comment\": \"\",\n" +
                    "    \"FacultCode\": \"000000027\",\n" +
                    "    \"FacultName\": \"Факультет фундаментальной и прикладной информатики\",\n" +
                    "    \"FormObCode\": \"000000001\",\n" +
                    "    \"GroupCode\": \"Э00000133\",\n" +
                    "    \"GroupName\": \"ПО-91б\",\n" +
                    "    \"LoadCode\": \"000000002\",\n" +
                    "    \"LoadName\": \"Экзамен\",\n" +
                    "    \"MoodleCourseID\": \"6528\",\n" +
                    "    \"RaspisID\": \"44928\",\n" +
                    "    \"ReitingCode\": {\n" +
                    "      \"1kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"2kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"3kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"4kt\": {\n" +
                    "        \"pos\": {\n" +
                    "          \"Ball\": \"4\"\n" +
                    "        },\n" +
                    "        \"usp\": {\n" +
                    "          \"Ball\": \"12\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"dopol\": [\n" +
                    "        {\n" +
                    "          \"Ball\": \"0\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"pb\": [\n" +
                    "        {\n" +
                    "          \"Ball\": \"0\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    \"SemestrCode\": \"000000007\",\n" +
                    "    \"SemestrName\": \"Шестой семестр\",\n" +
                    "    \"SubjCode\": \"000000772\",\n" +
                    "    \"SubjName\": \"Базы данных\",\n" +
                    "    \"SubjStatus\": \"t\",\n" +
                    "    \"TutorArr\": [\n" +
                    "      {\n" +
                    "        \"Email\": \"elenaanikina@inbox.ru\",\n" +
                    "        \"FIO\": \"Аникина Елена Игоревна\",\n" +
                    "        \"ID\": \"2520\",\n" +
                    "        \"KafedrUrl\": [\n" +
                    "          \"https://swsu.ru/structura/up/fivt/povt/index.php\"\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"TutorCode\": \"000001245\",\n" +
                    "    \"UID\": \"19-06-0245\"\n" +
                    "  }\n" +
                    "]"
        val jsonArray = JSONArray(document)
        val index = 0
        assertEquals("Error", rating.getDopolPbZed(jsonArray, index)[0])
    }


    @Test
    fun getRating() {
        assertEquals("4 12 4 12 4 12 4 12 ", rating.getRating(jsonArray, index))
    }

    @Test
    fun getRatingError() {
        val document =
            "[\n" +
                    "  {\n" +
                    "    \"Comment\": \"\",\n" +
                    "    \"FacultCode\": \"000000027\",\n" +
                    "    \"FacultName\": \"Факультет фундаментальной и прикладной информатики\",\n" +
                    "    \"FormObCode\": \"000000001\",\n" +
                    "    \"GroupCode\": \"Э00000133\",\n" +
                    "    \"GroupName\": \"ПО-91б\",\n" +
                    "    \"LoadCode\": \"000000002\",\n" +
                    "    \"LoadName\": \"Экзамен\",\n" +
                    "    \"MoodleCourseID\": \"6528\",\n" +
                    "    \"RaspisID\": \"44928\",\n" +
                    "    \"SemestrCode\": \"000000007\",\n" +
                    "    \"SemestrName\": \"Шестой семестр\",\n" +
                    "    \"SubjCode\": \"000000772\",\n" +
                    "    \"SubjName\": \"Базы данных\",\n" +
                    "    \"SubjStatus\": \"t\",\n" +
                    "    \"TutorArr\": [\n" +
                    "      {\n" +
                    "        \"Email\": \"elenaanikina@inbox.ru\",\n" +
                    "        \"FIO\": \"Аникина Елена Игоревна\",\n" +
                    "        \"ID\": \"2520\",\n" +
                    "        \"KafedrUrl\": [\n" +
                    "          \"https://swsu.ru/structura/up/fivt/povt/index.php\"\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"TutorCode\": \"000001245\",\n" +
                    "    \"UID\": \"19-06-0245\"\n" +
                    "  }\n" +
                    "]"
        val jsonArray = JSONArray(document)
        val index = 0
        assertEquals("Error", rating.getRating(jsonArray, index))
    }

    @Test
    fun getSubjectType() {
        assertEquals("Экзамен", rating.getSubjectType(jsonArray, index))

    }

    @Test
    fun getSubjectName() {
        assertEquals("Базы данных", rating.getSubjectName(jsonArray, index))
    }

    @Test
    fun getTutorNameAndID() {

        assertEquals("Аникина Елена Игоревна", rating.getTutorNameAndID(jsonArray, index)[0])
        assertEquals("2520", rating.getTutorNameAndID(jsonArray, index)[1])
    }

    @Test
    fun gradesCollector() {
        val globalArray = arrayListOf<ArrayList<String>>()
        val correct = arrayListOf(
            "Базы данных",
            "100",
            "Экзамен",
            "4 12 4 12 4 12 4 12 36 0 0 ",
            "Аникина Елена Игоревна",
            "2520"
        )
        globalArray.add(correct)
        assertEquals(globalArray, rating.gradesCollector(jsonArray))


    }

}