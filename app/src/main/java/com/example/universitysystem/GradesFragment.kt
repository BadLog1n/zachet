package com.example.universitysystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import com.example.universitysystem.databinding.FragmentGradesBinding
import java.util.concurrent.Executors
import kotlin.system.exitProcess



class GradesFragment : Fragment(R.layout.fragment_grades) {
    private val authCheck = AuthCheck()

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
    }

    private fun initGradesRc(){
        binding.apply {
            //gradesRcView.adapter = rcAdapter
            authCheck.check(requireView(), this@GradesFragment.context)

            val grArray = listOf(4,12,4,12,4,3,4,9,10,12,20)
            val arrayTest = listOf("19-06-0245", "19-06-0109")

            var sg = SubjectGrades("Базы данных",36,"зачет",grArray, arrayTest[0],"19-06-0118",arrayTest[0],"Аникина Елена Игоревна")
            rcAdapter.addSubjectGrades(sg)
            sg = SubjectGrades("Экономика",36,"зачет",grArray, arrayTest[1],"Иванов Петр Петрович",arrayTest[0],"Аникина Елена Игоревна")
            rcAdapter.addSubjectGrades(sg)

        }
    }
    /*public fun startChat(){
        var intent = Intent(this.context,IndividualChatActivity::class.java)
        startActivity(intent)
    }*/


}
