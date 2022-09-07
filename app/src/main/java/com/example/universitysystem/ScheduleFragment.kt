package com.example.universitysystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private var mondayAdapter = ScheduleAdapter()
    private var tuesdayAdapter = ScheduleAdapter()
    private var wednesdayAdapter = ScheduleAdapter()
    private var thursdayAdapter = ScheduleAdapter()
    private var fridayAdapter = ScheduleAdapter()
    private var saturdayAdapter = ScheduleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "Расписание"
        var groups: Array<String> = arrayOf("ПО-91б","ПО-92б")
        val grSpinner = view.findViewById<Spinner>(R.id.groupSpinner)
        var arrayAdapter:ArrayAdapter<String> = ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item,groups)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        grSpinner.adapter = arrayAdapter
        val mondayRc: RecyclerView = view.findViewById(R.id.mondayRc)
        mondayRc.layoutManager = LinearLayoutManager(this.context)
    }


}