package com.example.universitysystem

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.FragmentGradesBinding
import kotlin.system.exitProcess


class GradesFragment : Fragment(R.layout.fragment_grades) {

    lateinit var binding: FragmentGradesBinding
    private var rcAdapter = GradesAdapter()
    private var clickBack = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentGradesBinding.inflate(layoutInflater)
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.gradesRcView)
        recyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)



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
                DoAsync {
                    Thread.sleep(2000)
                    clickBack = false
                }
            } else {
                Thread.sleep(150)
                exitProcess(0)
            }
        }
    }
    class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        init {
            execute()
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
    private fun initGradesRc(){
        binding.apply {
            //gradesRcView.adapter = rcAdapter
            val grArray:IntArray = intArrayOf(4,12,4,12,4,3,4,9,10,12,20)
            val array = ArrayList<String>()
            array.add("19-06-0245")
            array.add("19-06-0109")

            var sg = SubjectGrades("Немченко",36,"зачет",grArray, array[0])
            rcAdapter.addSubjectGrades(sg)
            sg = SubjectGrades("Сохина",36,"зачет",grArray, array[1])
            rcAdapter.addSubjectGrades(sg)
            //rcAdapter.addSubjectGrades(sg)
            /**В строке ниже теперь должен быть добавлен еще один параметр. То есть ты потом получишь
             * это все(параметры) из бд и передашь в конструктор.
             **/

        }
    }
    /*public fun startChat(){
        var intent = Intent(this.context,IndividualChatActivity::class.java)
        startActivity(intent)
    }*/

}
