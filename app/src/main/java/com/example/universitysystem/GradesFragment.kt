package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import com.example.universitysystem.databinding.FragmentGradesBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import org.json.JSONArray
import org.jsoup.Jsoup
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

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Мои баллы"

        rcAdapter.clearRecords()
        rcAdapter.gradesList = ArrayList()
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

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    private fun initGradesRc() {
        binding.apply {
            //gradesRcView.adapter = rcAdapter
            authCheck.check(requireView(), this@GradesFragment.context)
            val executor = Executors.newSingleThreadExecutor()
            GlobalScope.launch {
                val connection =
                    Jsoup.connect("https://info.swsu.ru/scripts/student_diplom/auth.php?act=reiting&uid=19-06-0245&group=Э00000133&semestr=000000008&status=false&fo=000000001&type=json")
                //val document = connection.get().text()
                val document = connection.get().text()

                val jsonArray = JSONArray(document)
                val listOfGrades: ArrayList<ArrayList<String>> =
                    ratingUniversity.gradesCollector(jsonArray)
                for (item in listOfGrades) {
                    val sg = SubjectGrades(
                        item[0],
                        item[1].toInt(),
                        item[2],
                        item[3].split(" ").toList(),
                        item[4],
                        item[5]
                    )
                    withContext(Dispatchers.Main) {
                    rcAdapter.addSubjectGrades(sg)
                    }
                }
            }
        }
    }
}
/*public fun startChat(){
    var intent = Intent(this.context,IndividualChatActivity::class.java)
    startActivity(intent)
}*/



