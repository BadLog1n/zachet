package com.example.universitysystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.FragmentScheduleBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {


    private lateinit var binding:FragmentScheduleBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "Расписание"
        var groups: Array<String> = arrayOf("ПО-91б","ПО-92б")
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        var arrayAdapter:ArrayAdapter<String> = ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item,groups)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        val adapter = GroupAdapter<GroupieViewHolder>()
        val scheduleRc: RecyclerView = view.findViewById(R.id.scheduleRc)
        adapter.add(WeekDayItem("Понедельник"))
        adapter.add(ScheduleRecordItem("8.00","Базы данных(лб)","а-207, Аникина Е.И."))
        adapter.add(WeekDayItem("Понедельник"))
        adapter.add(ScheduleRecordItem("8.00","Базы данных(лб)","а-207, Аникина Е.И."))
        scheduleRc.adapter = adapter
        scheduleRc.layoutManager = LinearLayoutManager(this.context)
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

class ScheduleRecordItem(val time: String, val subject:String, val cabName:String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.time_tv).text = time
        viewHolder.itemView.findViewById<TextView>(R.id.subject_and_type_tv).text = subject
        viewHolder.itemView.findViewById<TextView>(R.id.room_n_fio_tv).text = cabName
    }

    override fun getLayout(): Int {
        return R.layout.schedule_str_item
    }

}