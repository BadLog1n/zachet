package com.oneseed.zachet.ui.grades

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.R
import com.oneseed.zachet.databinding.FragmentGradesBinding
import com.oneseed.zachet.domain.models.StudentState
import com.oneseed.zachet.ui.grades.adapter.GradesAdapter
import java.util.concurrent.Executors


class GradesFragment : Fragment() {

    private lateinit var strSemester: String
    private var _binding: FragmentGradesBinding? = null
    private val binding get() = _binding!!
    private var rcAdapter = GradesAdapter()
    private var clickBack = false
    private val viewModel: GradesFragmentViewModel by lazy {
        ViewModelProvider(this)[GradesFragmentViewModel::class.java]
    }
    private val sharedPrefGrades: SharedPreferences? = context?.getSharedPreferences(
        getString(R.string.gradesShared), Context.MODE_PRIVATE
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            super.onViewCreated(view, savedInstanceState)
            val toolbar1 = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
            toolbar1?.isEnabled = true
            toolbar1?.findViewById<ImageButton>(R.id.menuBtn)?.isEnabled = true
            //authCheck.check(view, this@GradesFragment.context) //todo
            activity?.findViewById<DrawerLayout>(R.id.drawer)
                ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            gradesRecyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)
            toolbar1?.title = "Мои баллы" //todo вынести в string
            activity?.findViewById<DrawerLayout>(R.id.drawer)
                ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            viewModel.getGrades(requireContext(), 7)
            viewModel.listToObserve.observe(viewLifecycleOwner) {
                when (it) {
                    is StudentState.Success -> {
                        rcAdapter.gradesList = ArrayList(it.ratingData) //!!!!
                        binding.gradesProgressBar.visibility = View.GONE
                        binding.gradesRecyclerView.visibility = View.VISIBLE
                        swipeRefreshLayout.isEnabled = true  //NULL
                        semNumSpinner.isEnabled = true
                    }
                    is StudentState.Error -> TODO()
                    StudentState.Loading -> TODO()

                }
            }

            val loginWeb =
                sharedPrefGrades?.getString(getString(R.string.loginWebShared), "").toString()
            val passwordWeb =
                sharedPrefGrades?.getString(getString(R.string.passwordWebShared), "").toString()
            val strSemester =
                sharedPrefGrades?.getString(getString(R.string.listOfSemesterToChange), "")
                    .toString()

            if (loginWeb != "" && passwordWeb != "" && strSemester != "") {
                val semester = strSemester.split(",").toTypedArray()
                semNumSpinner.visibility = View.VISIBLE
                textviewNeedAuth.visibility = View.GONE
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item, semester
                )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                semNumSpinner.adapter = arrayAdapter
            } else {
                textviewNeedAuth.visibility = View.VISIBLE
                semNumSpinner.visibility = View.GONE
                gradesProgressBar.visibility = View.GONE
            }
            gradesRecyclerView.adapter = rcAdapter
            gradesRecyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)

            // при нажатии кнопки "назад" на экране баллов(который является домашним), приложение выходит
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (!clickBack) {
                    Toast.makeText(activity, getString(R.string.confirmReturn), Toast.LENGTH_SHORT)
                        .show()
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

            /** то, что проиходит во время выбора элемента в спиннере (списке семестров): обновление
             *  баллов и пока они обновляются спиннер будет не доступен
             */
            semNumSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        semNumSpinner.isEnabled = false
                        //gradesChange(loginWeb)
                    }
                }

            //перенести обработку свайпа в слушатель
            swipeRefreshLayout.setOnRefreshListener {
                val spinnerElement = semNumSpinner.selectedItem.toString()
                if (rcAdapter.itemCount > 0 && spinnerElement != "") {
                    try {
//                     gradesChange(loginWeb)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}