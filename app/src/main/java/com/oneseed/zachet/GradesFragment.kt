package com.oneseed.zachet

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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oneseed.zachet.databinding.FragmentGradesBinding
import kotlinx.coroutines.*
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ratingUniversity.RatingUniversity
import java.util.*
import java.util.concurrent.Executors
import kotlin.system.exitProcess


class GradesFragment : Fragment(R.layout.fragment_grades) {
    private val authCheck = AuthCheck()
    private lateinit var database: DatabaseReference
    private val ratingUniversity = RatingUniversity()
    private lateinit var firebaseAuth: FirebaseAuth

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
        val progressBar: ProgressBar = view.findViewById(R.id.gradesProgressBar)
        val textviewNoAuthData: TextView = view.findViewById(R.id.textviewNeedAuth)

        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Мои баллы"
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val sharedPrefSetting: SharedPreferences? = context?.getSharedPreferences(
            getString(R.string.settingsShared),
            Context.MODE_PRIVATE
        )
        val sharedPrefGrades: SharedPreferences? = context?.getSharedPreferences(
            getString(R.string.gradesShared),
            Context.MODE_PRIVATE
        )


        rcAdapter.clearRecords()
        rcAdapter.gradesList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        progressBar.visibility = View.VISIBLE

        val loginWeb =
            sharedPrefGrades?.getString(getString(R.string.loginWebShared), "").toString()

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.uid == null) {
            sharedPrefSetting?.edit()?.putBoolean(getString(R.string.checkSettings), false)?.apply()
            Toast.makeText(
                context,
                "Логин или пароль не верен.",
                Toast.LENGTH_SHORT
            )
                .show()
            Navigation.findNavController(view).navigate(R.id.loginFragment)
        }
        val passwordWeb =
            sharedPrefGrades?.getString(getString(R.string.passwordWebShared), "").toString()
        val strSemesterOriginal =
            sharedPrefGrades?.getString(getString(R.string.listOfSemester), "").toString()
        val strSemester =
            sharedPrefGrades?.getString(getString(R.string.listOfSemesterToChange), "").toString()

        if (loginWeb != "" && passwordWeb != "" && strSemester != "") {
            val semester = strSemester.split(",").toTypedArray()
            spinner.visibility = View.VISIBLE
            textviewNoAuthData.visibility = View.GONE
            val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    semester
                )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        } else {
            textviewNoAuthData.visibility = View.VISIBLE
            spinner.visibility = View.GONE
            progressBar.visibility = View.GONE
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

        val versionCurrent =
            sharedPrefSetting?.getString(getString(R.string.versionShared), "").toString()


        database = FirebaseDatabase.getInstance().getReference("version")
        val versionName = getAppVersion(requireContext())
        if (versionCurrent < versionName && versionCurrent != "") {
            val builder = AlertDialog.Builder(
                requireActivity()
            )
            builder
                .setTitle("Обновление $versionName")
                .setView(R.layout.dialog_update)
                .setPositiveButton("OK", null)
                .create()
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.BLACK)
            }
        }




        sharedPrefSetting?.edit()?.putString(getString(R.string.versionShared), versionName)
            ?.apply()
        database.get().addOnSuccessListener {
            if (versionName < it.value.toString()) {
                /*Toast.makeText(
                    this.context,
                    "Доступна новая версия! Перейдите в \"О приложении\" чтобы её скачать!",
                    Toast.LENGTH_LONG
                ).show()*/
                try {
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

                } catch (e: Exception) {
                    Log.d("exceptionGrades", e.toString())
                }
            }
        }

        database = FirebaseDatabase.getInstance().getReference("minVersion")
        database.get().addOnSuccessListener {
            if (versionName < it.value.toString()) {
                Toast.makeText(
                    this.context,
                    "Чтобы пользоваться приложением, необходимо установить последнюю версию",
                    Toast.LENGTH_LONG
                ).show()
                exitProcess(0)
            }
        }


        var spinnerChange = false
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinner.isEnabled = false
                rcAdapter.clearRecords()

                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
                val actualGrades =
                    sharedPrefGrades?.getString(getString(R.string.actualGrades), "")
                        .toString().split(" ").toList().toMutableList()

                val semesterAll =
                    spinner.selectedItem.toString() + "," + strSemesterOriginal.replace(
                        "${spinner.selectedItem},",
                        ""
                    )
                if (semesterAll != strSemester || spinnerChange) {
                    actualGrades[0] = ""
                    sharedPrefGrades?.edit()
                        ?.putString(getString(R.string.actualGrades), "")
                        ?.apply()
                }
                spinnerChange = true
                binding.apply {
                    sharedPrefGrades?.edit()
                        ?.putString(getString(R.string.listOfSemesterToChange), semesterAll)
                        ?.apply()

                    val gr = sharedPrefGrades?.getString(getString(R.string.groupOfStudent), "")
                        .toString()
                    val fo = sharedPrefGrades?.getString(getString(R.string.formOfStudent), "")
                        .toString()
                    val ls =
                        sharedPrefGrades?.getInt(getString(R.string.lastSemester), 0).toString()
                            .toInt()

                    val result =
                        spinner.selectedItem.toString().filter { it.isDigit() }.toInt() + 1

                    val status = if (result + 1 >= ls) "false" else "true"

                    val semester = result.toString().padStart(9, '0')


                    GlobalScope.launch {
                        try {
                            val listOfGrades = returnRating(loginWeb, gr, semester, fo, status)
                            val isDownWeek = async { checkIsDownWeek() }

                            sharedPrefSetting?.edit()
                                ?.putBoolean(getString(R.string.isDownWeek), isDownWeek.await())
                                ?.apply()
                            withContext(Dispatchers.Main) {
                                var allGrades = ""
                                var isChange = false
                                if (listOfGrades != null && listOfGrades.size != 0) {
                                    rcAdapter.clearRecords()
                                    listOfGrades.forEachIndexed { index, item ->
                                        var changeSubject = false
                                        allGrades += "${item["ratingScore"].toString().toInt()} "
                                        if (actualGrades.size > index &&
                                            actualGrades[0] != "" &&
                                            item["ratingScore"].toString() != actualGrades[index]
                                        ) {
                                            changeSubject = true
                                            isChange = true
                                        }
                                        rcAdapter.addSubjectGrades(
                                            SubjectGrades(
                                                item["getSubjectName"].toString(),
                                                item["ratingScore"].toString().toInt(),
                                                item["subjectType"].toString(),
                                                item["rating"].toString().split(" ").toList(),
                                                item["tutorName"].toString(),
                                                item["tutorId"].toString(),
                                                subjectIsChange = changeSubject
                                            )
                                        )
                                    }
                                    sharedPrefGrades?.edit()
                                        ?.putString(getString(R.string.actualGrades), allGrades)
                                        ?.apply()


                                    progressBar.visibility = View.GONE
                                    recyclerView.visibility = View.VISIBLE

                                }
                                rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)
                                spinner.isEnabled = true

                                if (isChange) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Некоторые баллы были обновлены",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        } catch (_: Exception) {

                        }

                    }
                }
                //val switchState: Boolean = switch.isChecked
                //timetableGet(spinner.selectedItem.toString(), switchState)
            }
        }


    }

    private fun checkIsDownWeek(): Boolean {

        try {
            val sitePath =
                "https://swsu.ru/rzs/"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(5000)
                .execute()

            val statusCode: Int = response.statusCode()

            val document: Document? = if (statusCode == 200) Jsoup.connect(sitePath).get() else null

            val masthead: Element? = document?.select("div.current-week")?.select("b")?.first()

            Log.d("tag", masthead.toString())
            return "нижняя" in masthead.toString()
        } catch (_: Exception) {

        }
        return false

    }


    @Suppress("DEPRECATION")
    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= 33) {
                context?.packageManager?.getPackageInfo(
                    requireContext().packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            }
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }


    private fun returnRating(
        login: String,
        group: String,
        semester: String,
        form: String,
        status: String
    ): ArrayList<MutableMap<String, String>>? {
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




