package com.oneseed.zachet.ui.grades

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import authCheck.AuthCheck
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.R
import com.oneseed.zachet.adapters.GradesAdapter
import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.databinding.FragmentGradesBinding
import com.oneseed.zachet.domain.models.StudentState
import kotlinx.coroutines.*
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ratingUniversity.RatingUniversity
import java.util.*
import java.util.concurrent.Executors


class GradesFragment : Fragment() {

    private lateinit var strSemester: String
    private var _binding: FragmentGradesBinding? = null
    private val binding get() = _binding!!
    private var spinnerChange = false

    private val authCheck = AuthCheck()
    private lateinit var database: DatabaseReference
    private val ratingUniversity = RatingUniversity()
    private lateinit var firebaseAuth: FirebaseAuth
    private var rcAdapter = GradesAdapter()
    private var clickBack = false
    private val viewModel: GradesFragmentViewModel by lazy {
        ViewModelProvider(this)[GradesFragmentViewModel::class.java]
    }
    private val sharedPrefGrades: SharedPreferences?
        get() {
            return context?.getSharedPreferences(
                getString(R.string.gradesShared), Context.MODE_PRIVATE
            )
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar1 = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
        toolbar1?.isEnabled = true
        toolbar1?.findViewById<ImageButton>(R.id.menuBtn)?.isEnabled = true
        val spinner = requireView().findViewById<Spinner>(R.id.sem_num_spinner)
        authCheck.check(view, this@GradesFragment.context)
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.gradesRecyclerView)
        val progressBar: ProgressBar = view.findViewById(R.id.gradesProgressBar)
        val textviewNoAuthData: TextView = view.findViewById(R.id.textviewNeedAuth)
        val swipeRefreshLayout =
            requireView().findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        swipeRefreshLayout.isEnabled = false //NULL
        spinner.isEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)
        toolbar1?.title = "Мои баллы"
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val sharedPrefSetting: SharedPreferences? = context?.getSharedPreferences(
            getString(R.string.settingsShared), Context.MODE_PRIVATE
        )

        rcAdapter.clearRecords()
        rcAdapter.gradesList = ArrayList()
        rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)
        recyclerView.adapter = rcAdapter
        progressBar.visibility = View.VISIBLE

        viewModel.listToObserve.observe(viewLifecycleOwner) {
            when (it) {
                is StudentState.Success -> {
                   rcAdapter.gradesList = ArrayList(it.ratingData)

                }

                is StudentState.Error -> TODO()
                StudentState.Loading -> TODO()
            }
        }



        firebaseAuth = FirebaseAuth.getInstance()


        if (firebaseAuth.uid == null) {
            sharedPrefSetting?.edit()?.putBoolean(getString(R.string.checkSettings), false)?.apply()
            Toast.makeText(
                context, "Логин или пароль не верен.", Toast.LENGTH_SHORT
            ).show()
            Navigation.findNavController(view).navigate(R.id.loginFragment)
        }

        val loginWeb =
            sharedPrefGrades?.getString(getString(R.string.loginWebShared), "").toString()
        val passwordWeb =
            sharedPrefGrades?.getString(getString(R.string.passwordWebShared), "").toString()
        val strSemesterOriginal =
            sharedPrefGrades?.getString(getString(R.string.listOfSemester), "").toString()
        val strSemester =
            sharedPrefGrades?.getString(getString(R.string.listOfSemesterToChange), "")
                .toString()
        if (loginWeb != "" && passwordWeb != "" && strSemester != "") {
            val semester = strSemester.split(",").toTypedArray()
            spinner.visibility = View.VISIBLE
            textviewNoAuthData.visibility = View.GONE
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, semester
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        } else {
            textviewNoAuthData.visibility = View.VISIBLE
            spinner.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)
        // при нажатии кнопки "назад" на экране баллов(который является домашним), приложение выходит
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
                activity?.finish()
            }
        }

        /**
         * Действия, которые происходят при запуске только обновленного приложения. Появляется окно,
         * в котором подробно расписаны изменения в новой версии
         */
        val versionCurrent =
            sharedPrefSetting?.getString(getString(R.string.versionShared), "").toString()


        val versionName = getAppVersion(requireContext())
        if (versionCurrent < versionName && versionCurrent != "") {
            val builder = AlertDialog.Builder(
                requireActivity()
            )
            builder.setTitle("Обновление $versionName").setView(R.layout.dialog_new_version)
                .setPositiveButton("OK", null).create()
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.BLACK)
            }
        }

        sharedPrefSetting?.edit()?.putString(getString(R.string.versionShared), versionName)
            ?.apply()

        /**
         *  Проверка версии приложения при отрисовке экрана.
         *  Если версия на телефоне меньше, чем минимальная допустимая версия приложения,
         *  появляется диалог для обновления, который нельзя скрыть, при отказе обновления
         *  приложение принудительно закрывается.
         *  Если версия на телефоне больше минимально допустимой, но меньше самой новой (актуальной),
         *  появляется диалог с информацией о новой версии и предложением обновления. Его можно скрыть.
         *//*
        database = FirebaseDatabase.getInstance().getReference("versionInfo")
        database.get().addOnSuccessListener {
            val actualVersion = it.child("actualVersion").value.toString()
            val activity: Activity? = activity
            if (activity != null) {

                if (versionName < it.child("minVersion").value.toString()) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage(R.string.updateTextForced)
                    builder.setTitle(R.string.updateTitleForced)
                    builder.setNeutralButton("Выход") { _, _ ->
                        activity.finish()
                    }
                    builder.setPositiveButton("Обновить") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/BadLog1n/zachet/releases/download/$actualVersion/$actualVersion.apk")
                        )
                        startActivity(openDownloadFile)
                        activity.finish()
                    }
                    builder.setNegativeButton("RuStore") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://apps.rustore.ru/app/com.oneseed.zachet")
                        )
                        startActivity(openDownloadFile)
                        activity.finish()
                    }
                    val alertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK)

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                } else if (versionName < actualVersion) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage(R.string.updateText)
                    builder.setTitle(R.string.updateTitle)
                    builder.setNeutralButton("Ок") { _, _ ->
                    }
                    builder.setPositiveButton("Обновить") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/BadLog1n/zachet/releases/download/$actualVersion/$actualVersion.apk")
                        )
                        startActivity(openDownloadFile)
                    }
                    builder.setNegativeButton("RuStore") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://apps.rustore.ru/app/com.oneseed.zachet")
                        )
                        startActivity(openDownloadFile)
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK)

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

                }
            }

        }*/


        /**
         * Функция, которая обновляет список баллов на экране в соответствии с полученными данными
         */


        /** то, что проиходит во время выбора элемента в спиннере (списке семестров): обновление
         *  баллов и пока они обновляются спиннер будет не доступен
         */ //+
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                spinner.isEnabled = false
                gradesChange(loginWeb)
            }
        }

        // при свайпе и обновлении происходит следующее: +
        swipeRefreshLayout.setOnRefreshListener {
            val spinnerElement = spinner.selectedItem.toString()
            if (rcAdapter.itemCount > 0 && spinnerElement != "") {
                try {
                    gradesChange(loginWeb)
                    Handler(Looper.getMainLooper()).postDelayed({
                        swipeRefreshLayout.isRefreshing = false
                    }, 500)
                    Firebase.analytics.logEvent("grades_update") {
                        param("grades_update", "")
                    }
                } catch (_: Exception) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }


    private fun gradesChange(loginWeb: String) {
        with(binding) {
            swipeRefreshLayout.isEnabled = false
            semNumSpinner.isEnabled = false
            rcAdapter.clearRecords()
            gradesProgressBar.visibility = View.VISIBLE
            gradesRecyclerView.visibility = View.INVISIBLE


            val actualGrades =
                sharedPrefGrades?.getString(getString(R.string.actualGrades), "").toString()
                    .split(" ").toList().toMutableList()
            val strSemesterOriginal =
                sharedPrefGrades?.getString(getString(R.string.listOfSemester), "").toString()
            strSemester =
                sharedPrefGrades?.getString(getString(R.string.listOfSemesterToChange), "")
                    .toString()
            // с какого на какой семестр поменяли. если не поменялся то "на какой" будет пустым
            val semesterAll =
                semNumSpinner.selectedItem.toString() + "," + strSemesterOriginal.replace(
                    "${semNumSpinner.selectedItem},", ""
                )
            if ((semesterAll != strSemester) || spinnerChange) {
                actualGrades[0] = ""
                sharedPrefGrades?.edit()?.putString(getString(R.string.actualGrades), "")?.apply()
            }
            sharedPrefGrades?.edit()
                ?.putString(getString(R.string.listOfSemesterToChange), semesterAll)?.apply()

            spinnerChange = true

            val gr =
                sharedPrefGrades?.getString(getString(R.string.groupOfStudent), "").toString()
            val fo =
                sharedPrefGrades?.getString(getString(R.string.formOfStudent), "").toString()
            val ls =
                sharedPrefGrades?.getInt(getString(R.string.lastSemester), 0).toString().toInt()
            val result = semNumSpinner.selectedItem.toString().filter { it.isDigit() }
                .toInt() + 1 // номер выбранного семестра

            // проверка обновились ли семестры. если result( факт. последн.сем. в спиннере)>=полученному
            // по запросу ls, то не обновились, иначе обновились
            val status = if (result + 1 >= ls) "false" else "true"
            val semester = result.toString().padStart(9, '0') //дополнение нулями впереди

            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) { 
                        checkIsDownWeek()
                        val listOfGrades = returnRating(
                            loginWeb,
                            gr,
                            semester,
                            fo,
                            status
                        ) 
                        withContext(Dispatchers.Main) { 
                            if (listOfGrades != null) { 
                                var allGrades = ""
                                var isChange = false
                                if (listOfGrades.size != 0) {
                                    rcAdapter.clearRecords()
                                    listOfGrades.forEachIndexed { index, item ->
                                        var changeSubject = false
                                        allGrades += "${
                                            item["ratingScore"].toString().toInt()
                                        } "
                                        if (actualGrades.size > index && actualGrades[0] != "" && item["ratingScore"].toString() != actualGrades[index]) {
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
                                    gradesProgressBar.visibility = View.GONE
                                    gradesRecyclerView.visibility = View.VISIBLE
                                }
                                swipeRefreshLayout.isEnabled = true
                                rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)
                                if (rcAdapter.itemCount > 0) semNumSpinner.isEnabled = true

                                if (isChange) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Некоторые баллы были обновлены",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                gradesProgressBar.visibility = View.GONE
                                semNumSpinner.visibility = View.GONE
                                textviewNeedAuth.text =
                                    "Не удаётся подключиться к сайту. Проверьте подключение к интернету."
                                textviewNeedAuth.visibility = View.VISIBLE
                            }
                        }
                    }
                } catch (_: Exception) {

                }


            }
        }
    }
    /**
     * Функция, которая получает информацию о том, какая версия приложения установлена на телефоне
     */
    private fun getAppVersion(context: Context?): String {//это
        var version = ""
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= 33) {
                context?.packageManager?.getPackageInfo(
                    requireContext().packageName, PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION") context?.packageManager?.getPackageInfo(
                    requireContext().packageName, 0
                )
            }
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }


    private fun checkIsDownWeek() {
        val sharedPrefSetting: SharedPreferences? = context?.getSharedPreferences(
            getString(R.string.settingsShared), Context.MODE_PRIVATE
        )
        try {
            val sitePath = "https://swsu.ru/rzs/"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(5000).execute()

            val statusCode: Int = response.statusCode()

            val document: Document? = if (statusCode == 200) Jsoup.connect(sitePath).get() else null

            val masthead: Element? = document?.select("div.current-week")?.select("b")?.first()

            Log.d("tag", masthead.toString())
            if (masthead != null) {
                sharedPrefSetting?.edit()
                    ?.putBoolean(getString(R.string.isDownWeek), "нижняя" in masthead.toString())
                    ?.apply()
            }
        } catch (_: Exception) {

        }
    }

    /**
     * Функция, проверяющая по парсингу сайта юзгу, нижняя ли сейчас неделя. Если нижняя, то
     * переменной isDownWeek в SharedPreferences будет присвоено значение true, в противном случае
     * false
     */
    

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}



