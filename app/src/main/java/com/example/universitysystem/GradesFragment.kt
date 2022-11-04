package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import com.example.universitysystem.databinding.FragmentGradesBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONArray
import ratingUniversity.RatingUniversity
import java.util.concurrent.Executors
import kotlin.system.exitProcess



class GradesFragment : Fragment(R.layout.fragment_grades) {
    private val authCheck = AuthCheck()
    private lateinit var database: DatabaseReference
    private val ratingUniversity = RatingUniversity()

    private lateinit var binding: FragmentGradesBinding
    private var rcAdapter = GradesAdapter()
    private var clickBack = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCheck.check(view, this@GradesFragment.context)
        binding = FragmentGradesBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.gradesRcView)
        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "Мои баллы"

        rcAdapter.clearRecords()
        rcAdapter.gradesList =  ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        initGradesRc()
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!clickBack) {
                Toast.makeText(activity, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show()
                clickBack = true
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    Thread.sleep(2000)
                    clickBack = false
                }
            } else {
                Thread.sleep(150)
                exitProcess(0)
            }
        }

        database = FirebaseDatabase.getInstance().getReference("version")
        val requestToDatabase = database.get()
        val versionName = getAppVersion(requireContext())
        requestToDatabase.addOnSuccessListener {
            if (versionName < it.value.toString()) {
                Toast.makeText(
                    this.context,
                    "Доступна новая версия! Перейдите в \"О приложении\" чтобы её скачать!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }


    private fun initGradesRc(){
        binding.apply {
            //gradesRcView.adapter = rcAdapter
            authCheck.check(requireView(), this@GradesFragment.context)


            val document = "[{\"RaspisID\":\"44928\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000001245\",\"SubjCode\":\"000000772\",\"SubjName\":\"\\u0411\\u0430\\u0437\\u044b \\u0434\\u0430\\u043d\\u043d\\u044b\\u0445\",\"LoadCode\":\"000000002\",\"LoadName\":\"\\u042d\\u043a\\u0437\\u0430\\u043c\\u0435\\u043d\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"\",\"MoodleCourseID\":\"6528\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2520\",\"FIO\":\"\\u0410\\u043d\\u0438\\u043a\\u0438\\u043d\\u0430 \\u0415\\u043b\\u0435\\u043d\\u0430 \\u0418\\u0433\\u043e\\u0440\\u0435\\u0432\\u043d\\u0430\",\"Email\":\"elenaanikina@inbox.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/povt\\/index.php\"]}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"2kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"3kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"36\"}],\"pb\":[{\"Ball\":\"0\"}]}},{\"RaspisID\":\"46785\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000000419\",\"SubjCode\":\"000000830\",\"SubjName\":\"\\u0422\\u0435\\u043e\\u0440\\u0438\\u044f \\u0432\\u044b\\u0447\\u0438\\u0441\\u043b\\u0438\\u0442\\u0435\\u043b\\u044c\\u043d\\u044b\\u0445 \\u043f\\u0440\\u043e\\u0446\\u0435\\u0441\\u0441\\u043e\\u0432\",\"LoadCode\":\"000000002\",\"LoadName\":\"\\u042d\\u043a\\u0437\\u0430\\u043c\\u0435\\u043d\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"https:\\/\\/vk.com\\/club188948307\",\"MoodleCourseID\":\"6203\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2519\",\"FIO\":\"\\u041c\\u0430\\u043b\\u044b\\u0448\\u0435\\u0432 \\u0410\\u043b\\u0435\\u043a\\u0441\\u0430\\u043d\\u0434\\u0440 \\u0412\\u0430\\u0441\\u0438\\u043b\\u044c\\u0435\\u0432\\u0438\\u0447\",\"Email\":\"alta76@yandex.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/povt\\/index.php\"]}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"2kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"3kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"36\"}],\"pb\":[{\"Ball\":\"0\"}]}},{\"RaspisID\":\"46764\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000000419\",\"SubjCode\":\"000000995\",\"SubjName\":\"\\u041e\\u0444\\u0438\\u0441\\u043d\\u044b\\u0435 \\u0442\\u0435\\u0445\\u043d\\u043e\\u043b\\u043e\\u0433\\u0438\\u0438\",\"LoadCode\":\"000000002\",\"LoadName\":\"\\u042d\\u043a\\u0437\\u0430\\u043c\\u0435\\u043d\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"https:\\/\\/vk.com\\/club188948307\",\"MoodleCourseID\":\"6530\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2519\",\"FIO\":\"\\u041c\\u0430\\u043b\\u044b\\u0448\\u0435\\u0432 \\u0410\\u043b\\u0435\\u043a\\u0441\\u0430\\u043d\\u0434\\u0440 \\u0412\\u0430\\u0441\\u0438\\u043b\\u044c\\u0435\\u0432\\u0438\\u0447\",\"Email\":\"alta76@yandex.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/povt\\/index.php\"]}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"2kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"3kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"36\"}],\"pb\":[{\"Ball\":\"0\"}]}},{\"RaspisID\":\"46774\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000000419\",\"SubjCode\":\"000000739\",\"SubjName\":\"\\u0421\\u0438\\u0441\\u0442\\u0435\\u043c\\u043d\\u043e\\u0435 \\u043f\\u0440\\u043e\\u0433\\u0440\\u0430\\u043c\\u043c\\u043d\\u043e\\u0435 \\u043e\\u0431\\u0435\\u0441\\u043f\\u0435\\u0447\\u0435\\u043d\\u0438\\u0435\",\"LoadCode\":\"000000003\",\"LoadName\":\"\\u0417\\u0430\\u0447\\u0435\\u0442\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"https:\\/\\/vk.com\\/club188948307\",\"MoodleCourseID\":\"6535\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2519\",\"FIO\":\"\\u041c\\u0430\\u043b\\u044b\\u0448\\u0435\\u0432 \\u0410\\u043b\\u0435\\u043a\\u0441\\u0430\\u043d\\u0434\\u0440 \\u0412\\u0430\\u0441\\u0438\\u043b\\u044c\\u0435\\u0432\\u0438\\u0447\",\"Email\":\"alta76@yandex.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/povt\\/index.php\"]}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"2kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"3kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"36\"}],\"pb\":[{\"Ball\":\"0\"}]}},{\"RaspisID\":\"46918\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000001246\",\"SubjCode\":\"000000832\",\"SubjName\":\"\\u0422\\u0435\\u043e\\u0440\\u0438\\u044f \\u043f\\u0440\\u0438\\u043d\\u044f\\u0442\\u0438\\u044f \\u0440\\u0435\\u0448\\u0435\\u043d\\u0438\\u0439\",\"LoadCode\":\"000000003\",\"LoadName\":\"\\u0417\\u0430\\u0447\\u0435\\u0442\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"\",\"MoodleCourseID\":\"10549\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2132\",\"FIO\":\"\\u0410\\u043f\\u0430\\u043b\\u044c\\u043a\\u043e\\u0432 \\u0412\\u043b\\u0430\\u0434\\u0438\\u043c\\u0438\\u0440 \\u0412\\u0430\\u0441\\u0438\\u043b\\u044c\\u0435\\u0432\\u0438\\u0447\",\"Email\":\"vva57@mail.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/povt\\/index.php\"]}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"10\"}},\"2kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"10\"}},\"3kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"9\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"11\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"0\"}],\"pb\":[{\"Ball\":\"0\"}]}},{\"RaspisID\":\"44384\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000000841\",\"SubjCode\":\"000000879\",\"SubjName\":\"\\u041c\\u0435\\u0442\\u043e\\u0434\\u044b \\u0438 \\u0441\\u0440\\u0435\\u0434\\u0441\\u0442\\u0432\\u0430 \\u0437\\u0430\\u0449\\u0438\\u0442\\u044b \\u043a\\u043e\\u043c\\u043f\\u044c\\u044e\\u0442\\u0435\\u0440\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0446\\u0438\\u0438\",\"LoadCode\":\"000000003\",\"LoadName\":\"\\u0417\\u0430\\u0447\\u0435\\u0442\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"\",\"MoodleCourseID\":\"16905\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2237\",\"FIO\":\"\\u0425\\u0430\\u043d\\u0438\\u0441 \\u0410\\u043d\\u0434\\u0440\\u0435\\u0439 \\u041b\\u0435\\u043e\\u043d\\u0438\\u0434\\u043e\\u0432\\u0438\\u0447\",\"Email\":\"uinfo2@mail.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/kafedra-ib\\/index.php\"]},{\"ID\":\"2422\",\"FIO\":\"\\u041c\\u0430\\u0440\\u0443\\u0445\\u043b\\u0435\\u043d\\u043a\\u043e \\u0410\\u043d\\u0430\\u0442\\u043e\\u043b\\u0438\\u0439 \\u041b\\u0435\\u043e\\u043d\\u0438\\u0434\\u043e\\u0432\\u0438\\u0447\",\"Email\":\"proxy33@mail.ru\",\"KafedrUrl\":{\"1\":\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/kafedra-ib\\/index.php\",\"2\":\"https:\\/\\/swsu.ru\\/structura\\/up\\/fivt\\/kvt\\/index.php\"}}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"4\"}},\"2kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"3kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"12\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"17\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"0\"}],\"pb\":[{\"Ball\":\"0\"}]}},{\"RaspisID\":\"45650\",\"FacultCode\":\"000000027\",\"FacultName\":\"\\u0424\\u0430\\u043a\\u0443\\u043b\\u044c\\u0442\\u0435\\u0442 \\u0444\\u0443\\u043d\\u0434\\u0430\\u043c\\u0435\\u043d\\u0442\\u0430\\u043b\\u044c\\u043d\\u043e\\u0439 \\u0438 \\u043f\\u0440\\u0438\\u043a\\u043b\\u0430\\u0434\\u043d\\u043e\\u0439 \\u0438\\u043d\\u0444\\u043e\\u0440\\u043c\\u0430\\u0442\\u0438\\u043a\\u0438\",\"GroupCode\":\"\\u042d00000133\",\"GroupName\":\"\\u041f\\u041e-91\\u0431\",\"TutorCode\":\"000000682\",\"SubjCode\":\"000002140\",\"SubjName\":\"\\u0411\\u0430\\u0437\\u043e\\u0432\\u044b\\u0435 \\u0444\\u0438\\u0437\\u043a\\u0443\\u043b\\u044c\\u0442\\u0443\\u0440\\u043d\\u043e-\\u0441\\u043f\\u043e\\u0440\\u0442\\u0438\\u0432\\u043d\\u044b\\u0435 \\u0432\\u0438\\u0434\\u044b\",\"LoadCode\":\"000000003\",\"LoadName\":\"\\u0417\\u0430\\u0447\\u0435\\u0442\",\"FormObCode\":\"000000001\",\"SemestrCode\":\"000000007\",\"SemestrName\":\"\\u0428\\u0435\\u0441\\u0442\\u043e\\u0439 \\u0441\\u0435\\u043c\\u0435\\u0441\\u0442\\u0440\",\"SubjStatus\":\"t\",\"Comment\":\"\\u0432\\u0430\\u0442\\u0441\\u0430\\u043f\",\"MoodleCourseID\":\"17498\",\"UID\":\"19-06-0245\",\"TutorArr\":[{\"ID\":\"2254\",\"FIO\":\"\\u041a\\u0443\\u0440\\u0430\\u0441\\u0431\\u0435\\u0434\\u0438\\u0430\\u043d\\u0438 \\u0417\\u0443\\u0440\\u0430\\u0431\\u0438 \\u0412\\u0430\\u0436\\u0430\\u0435\\u0432\\u0438\\u0447\",\"Email\":\"kurasbediani-zv-kstu@yandex.ru\",\"KafedrUrl\":[\"https:\\/\\/swsu.ru\\/structura\\/up\\/flmk\\/kfv\\/\"]}],\"ReitingCode\":{\"1kt\":{\"pos\":{\"Ball\":\"2\"},\"usp\":{\"Ball\":\"4\"}},\"2kt\":{\"pos\":{\"Ball\":\"2\"},\"usp\":{\"Ball\":\"4\"}},\"3kt\":{\"pos\":{\"Ball\":\"2\"},\"usp\":{\"Ball\":\"4\"}},\"4kt\":{\"pos\":{\"Ball\":\"4\"},\"usp\":{\"Ball\":\"19\"}},\"dopol\":[{\"Ball\":\"0\"}],\"zed\":[{\"Ball\":\"25\"}]}}]"
            val jsonArray = JSONArray(document)
            val listOfGrades: ArrayList<ArrayList<String>> =
                ratingUniversity.gradesCollector(jsonArray)
            Log.d("text", listOfGrades.toString())
            for (item in listOfGrades) {
                val sg = SubjectGrades(
                    item[0],
                    item[1].toInt(),
                    item[2],
                    item[3].split(" ").toList(),
                    item[4],
                    item[5]
                )
                    rcAdapter.addSubjectGrades(sg)
                }
            }
        }
    }
    /*public fun startChat(){
        var intent = Intent(this.context,IndividualChatActivity::class.java)
        startActivity(intent)
    }*/



