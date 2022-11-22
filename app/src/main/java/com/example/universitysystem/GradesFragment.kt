package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import com.example.universitysystem.databinding.FragmentGradesBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import ratingUniversity.InfoOfStudent
import ratingUniversity.RatingUniversity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.system.exitProcess


class GradesFragment : Fragment(R.layout.fragment_grades) {
    private val authCheck = AuthCheck()
    private lateinit var database: DatabaseReference
    private val ratingUniversity = RatingUniversity()
    private val infoOfStudent = InfoOfStudent()

    private lateinit var binding: FragmentGradesBinding
    private var rcAdapter = GradesAdapter()
    private var clickBack = false

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinner = requireView().findViewById<Spinner>(R.id.sem_num_spinner)

        authCheck.check(view, this@GradesFragment.context)

        binding = FragmentGradesBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.gradesRcView)
        val progressBar:ProgressBar = view.findViewById(R.id.gradesProgressBar)

        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Мои баллы"
        val sharedPref: SharedPreferences? = context?.getSharedPreferences(
            "Settings",
            Context.MODE_PRIVATE
        )

        rcAdapter.clearRecords()
        rcAdapter.gradesList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter

        val loginWeb = sharedPref?.getString("loginWeb", "").toString()
        val passwordWeb = sharedPref?.getString("passwordWeb", "").toString()
        if (loginWeb != "" && passwordWeb != "") {
            spinner.visibility = View.VISIBLE

            getDataOfStudent(sharedPref, loginWeb, passwordWeb, spinner)
        }


/*        try {
            //initGradesRc()
        } catch (e: Exception) {
        }*/
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
        val versionName = getAppVersion(requireContext())
        database.get().addOnSuccessListener {
            if (versionName < it.value.toString()) {
                /*Toast.makeText(
                    this.context,
                    "Доступна новая версия! Перейдите в \"О приложении\" чтобы её скачать!",
                    Toast.LENGTH_LONG
                ).show()*/
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(R.string.updateText)
                builder.setTitle(R.string.updateTitle)
                builder.setPositiveButton("Ок") { _, _ ->

                }
                builder.setNeutralButton("Перейти") { _, _ ->
                    findNavController().navigate(R.id.helpFragment)
                }
                val alertDialog = builder.create()
                alertDialog.show()

                val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                with(autoBtn) {
                    setTextColor(Color.BLACK)
                }
                val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                with(userBtn) {
                    setTextColor(Color.BLACK)
                }

            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
                binding.apply {
                    GlobalScope.launch {
                        val gr = sharedPref?.getString("groupOfStudent", "").toString()
                        val fo = sharedPref?.getString("formOfStudent", "").toString()
                        val ls = sharedPref?.getInt("lastSemester", 0)

                        val result =
                            spinner.selectedItem.toString().filter { it.isDigit() }.toInt() + 1

                        val status = if (result + 1 >= ls!!.toInt()) "false" else "true"

                        val semester = result.toString().padStart(9, '0')

                        val listOfGrades = returnRating(loginWeb, gr, semester, fo, status)
                        withContext(Dispatchers.Main) {
                            if (listOfGrades != null && listOfGrades.size != 0) {
                                rcAdapter.clearRecords()
                                for (item in listOfGrades) {
                                    rcAdapter.addSubjectGrades(
                                        SubjectGrades(
                                            item[0],
                                            item[1].toInt(),
                                            item[2],
                                            item[3].split(" ").toList(),
                                            item[5],
                                            item[4]
                                        )
                                    )
                                }
                                progressBar.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }


                //val switchState: Boolean = switch.isChecked
                //timetableGet(spinner.selectedItem.toString(), switchState)
            }
        }


    }

    @Suppress("DEPRECATION")
    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= 33) {
                context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
                //TODO "Добавить следующий код когда будет переведено на новую версию проекта")
                //context?.packageManager?.getPackageInfo(requireContext().packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            }
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }


    @SuppressLint("SimpleDateFormat")
    @OptIn(DelicateCoroutinesApi::class)
    private fun getDataOfStudent(
        sharedPref: SharedPreferences?,
        login: String,
        password: String,
        spinner: Spinner
    ) {
        binding.apply {
            GlobalScope.launch {

                val infoOfStudent = getSemester(login, password)
                withContext(Dispatchers.Main) {
                    if (infoOfStudent != null) {

                        val dateFormat: DateFormat = SimpleDateFormat("MM")
                        val date = Date()
                        val month = dateFormat.format(date)
                        val arrayOfSemester = arrayOf("1", "9", "10", "11", "12")
                        val semesterCurrent = month in arrayOfSemester


                        val semester = (infoOfStudent[1] + infoOfStudent[2]).toMutableList()
                        semester.sort()
                        semester.reverse()
                        sharedPref?.edit()?.putInt("lastSemester", semester.first().toInt())
                            ?.apply()
                        semester.forEachIndexed { index, element ->
                            val correctSemester = element.toInt() - 1
                            semester[index] = "Семестр $correctSemester"
                        }
                        if (semesterCurrent) semester.removeFirst()

                        val arrayAdapter: ArrayAdapter<String> =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                semester
                            )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = arrayAdapter
                        sharedPref?.edit()?.putString("groupOfStudent", infoOfStudent[0][1])
                            ?.apply()
                        sharedPref?.edit()?.putString("formOfStudent", infoOfStudent[0][0])
                            ?.apply()
                        /*              Toast.makeText(requireContext(), item, Toast.LENGTH_SHORT)
                                          .show()*/

                    }
                }
            }
        }
    }


    /*                           returnRating(
                                login,
                                infoOfStudent[0][1],
                                semester.first(),
                                infoOfStudent[0][0],
                                "false"
                            )*/


    private fun getFormAndGroup(login: String, password: String): ArrayList<String>? {
        try {
            val arrayToReturn = arrayListOf("", "")
            val document: String
            val sitePath =
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=$login&password=$password&type=array"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(30000)
                .execute()

            val statusCode: Int = response.statusCode()

            if (statusCode == 200) {
                document = Jsoup.connect(sitePath).get().text()
            } else return null
            arrayToReturn[0] = infoOfStudent.getFormOfStudy(document)
            arrayToReturn[1] = infoOfStudent.getGroupOfStudent(document)
            return arrayToReturn

        } catch (e: Exception) {
            Log.d("getFormAndGroup", e.toString())


            return null
        }
    }


    private fun getSemester(login: String, password: String): ArrayList<ArrayList<String>>? {
        try {
            val arrayToReturn = arrayListOf<ArrayList<String>>()
            val infoStudent = getFormAndGroup(login, password) ?: return null
            arrayToReturn.add(infoStudent)
            var document: String
            val sitePath =
                arrayOf(
                    "https://info.swsu.ru/scripts/student_diplom/auth.php?act=semestr&group=${infoStudent[1]}&status=false&type=json",
                    "https://info.swsu.ru/scripts/student_diplom/auth.php?act=semestr&group=${infoStudent[1]}&status=true&type=json"
                )
            for (item in sitePath) {
                val response: Connection.Response = Jsoup.connect(item)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(30000)
                    .execute()
                val statusCode: Int = response.statusCode()
                if (statusCode == 200) {
                    document = Jsoup.connect(item).get().text()
                } else return null
                val jsonArray = JSONArray(document)
                arrayToReturn.add(infoOfStudent.getSemesterOfStudent(jsonArray))
            }
            return arrayToReturn
        } catch (e: Exception) {
            Log.d("getFormAndGroup", e.toString())


        }
        return null
    }

    private fun returnRating(
        login: String,
        group: String,
        semester: String,
        form: String,
        status: String
    ): ArrayList<ArrayList<String>>? {
        try {
            val document: String
            val sitePath =
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=reiting&uid=$login&group=$group&semestr=$semester&status=$status&fo=$form&type=json"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(30000)
                .execute()

            val statusCode: Int = response.statusCode()

            if (statusCode == 200) {
                document = Jsoup.connect(sitePath).get().text()
            } else return null

            val jsonArray = JSONArray(document)
            return ratingUniversity.gradesCollector(jsonArray)

        } catch (e: Exception) {
            Log.d("getFormAndGroup", e.toString())


        }
        return null
    }
}




