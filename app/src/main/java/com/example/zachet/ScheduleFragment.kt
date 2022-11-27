package com.example.zachet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zachet.databinding.FragmentScheduleBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    private lateinit var database: DatabaseReference


    private lateinit var binding: FragmentScheduleBinding


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinner = requireView().findViewById<Spinner>(R.id.spinner)
        val switch = requireView().findViewById<Switch>(R.id.switchh)

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Расписание"
        getGroups(spinner)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val scheduleRc: RecyclerView = view.findViewById(R.id.scheduleRc)
        val progressBar:ProgressBar = view.findViewById(R.id.scheduleProgressBar)
        //timetableGet(view.findViewById<Spinner>(R.id.spinner).selectedItem.toString(), "up")
        scheduleRc.adapter = adapter
        scheduleRc.layoutManager = LinearLayoutManager(this.context)
        //timetableGet("П")


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
                val switchState: Boolean = switch.isChecked
                timetableGet(spinner.selectedItem.toString(), switchState)
                progressBar.visibility = View.GONE
            }
        }


        switch.setOnCheckedChangeListener { _, _ ->
            val switchState: Boolean = switch.isChecked
            if (spinner.selectedItem != null) {
                timetableGet(spinner.selectedItem.toString(), switchState)
            }
        }


    }


    private fun getGroups(spinner: Spinner) {
        val groups: ArrayList<String> = arrayListOf()
        database = FirebaseDatabase.getInstance().getReference("timetable")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            for (item in it.children) {
                groups.add(item.key.toString())
            }
            try {
                val arrayAdapter: ArrayAdapter<String> =
                    ArrayAdapter(
                        this.requireContext(),
                        android.R.layout.simple_spinner_item,
                        groups
                    )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = arrayAdapter
            } catch (e: IllegalStateException) {
            }
            val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, groups)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }
    }

    private var subjects = arrayListOf<ScheduleRecordItem>()
    private fun timetableGet(group: String, upDown: Boolean) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        val scheduleRc: RecyclerView = requireView().findViewById(R.id.scheduleRc)
        var upDownText = "up"
        if (upDown) upDownText = "down"

        val rusDay = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
        val day = arrayOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
        database = FirebaseDatabase.getInstance().getReference("timetable/$group/$upDownText")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener { gets ->
            day.forEachIndexed { index, element ->
                for (timeCabSub in gets.child(element).children) {
                    val subject =
                        ScheduleRecordItem(
                            timeCabSub.key.toString(),
                            timeCabSub.child("cabName").value.toString(),
                            timeCabSub.child("subject").value.toString()
                        )
                    subjects.add(subject)
                }
                subjects = java.util.ArrayList(subjects.sortedWith(compareBy { it.time }))
                adapter.add(WeekDayItem(rusDay[index]))
                for (item in subjects) {
                    adapter.add(ScheduleRecordItem(item.time, item.cabName, item.subject))
                }
                subjects.clear()
            }
            scheduleRc.adapter = adapter
            scheduleRc.layoutManager = LinearLayoutManager(this.context)
        }


    }

}


class WeekDayItem(val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.weekday_tv).text = text
    }

    override fun getLayout(): Int {
        return R.layout.weekday_item
    }

}

class ScheduleRecordItem(val time: String, val subject: String, val cabName: String) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.time_tv).text = time
        viewHolder.itemView.findViewById<TextView>(R.id.subject_and_type_tv).text = subject
        viewHolder.itemView.findViewById<TextView>(R.id.room_n_fio_tv).text = cabName
    }

    override fun getLayout(): Int {
        return R.layout.schedule_str_item
    }

}