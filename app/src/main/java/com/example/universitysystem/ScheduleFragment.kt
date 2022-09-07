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
import com.example.universitysystem.databinding.FragmentScheduleBinding


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private var mondayAdapter = ScheduleAdapter()
    private var tuesdayAdapter = ScheduleAdapter()
    private var wednesdayAdapter = ScheduleAdapter()
    private var thursdayAdapter = ScheduleAdapter()
    private var fridayAdapter = ScheduleAdapter()
    private var saturdayAdapter = ScheduleAdapter()
    private lateinit var binding:FragmentScheduleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "Расписание"
        var groups: Array<String> = arrayOf("ПО-91б","ПО-92б")
        val grSpinner = view.findViewById<Spinner>(R.id.groupSpinner)
        var arrayAdapter:ArrayAdapter<String> = ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item,groups)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        grSpinner.adapter = arrayAdapter

        val mondayRc: RecyclerView = view.findViewById(R.id.mondayRc)
        mondayRc.layoutManager = LinearLayoutManager(this.context)
        mondayAdapter.clearRecords()
        mondayAdapter.scheduleRecordsList =  ArrayList()
        mondayAdapter.notifyDataSetChanged()
        mondayRc.adapter = mondayAdapter
        initMondayRc()
        mondayRc.adapter = mondayAdapter
        mondayRc.layoutManager = LinearLayoutManager(this.context)

        val tuesdayRc: RecyclerView = view.findViewById(R.id.tuesdayRc)
        tuesdayRc.layoutManager = LinearLayoutManager(this.context)
        tuesdayAdapter.clearRecords()
        tuesdayAdapter.scheduleRecordsList =  ArrayList()
        tuesdayAdapter.notifyDataSetChanged()
        tuesdayRc.adapter = tuesdayAdapter
        initTuesdayRc()
        tuesdayRc.adapter = tuesdayAdapter
        tuesdayRc.layoutManager = LinearLayoutManager(this.context)

        val wednesdayRc: RecyclerView = view.findViewById(R.id.wednesdayRc)
        wednesdayRc.layoutManager = LinearLayoutManager(this.context)
        wednesdayAdapter.clearRecords()
        wednesdayAdapter.scheduleRecordsList =  ArrayList()
        wednesdayAdapter.notifyDataSetChanged()
        wednesdayRc.adapter = wednesdayAdapter
        initWednesdayRc()
        wednesdayRc.adapter = wednesdayAdapter
        wednesdayRc.layoutManager = LinearLayoutManager(this.context)

        val thursdayRc: RecyclerView = view.findViewById(R.id.thursdayRc)
        thursdayRc.layoutManager = LinearLayoutManager(this.context)
        thursdayAdapter.clearRecords()
        thursdayAdapter.scheduleRecordsList =  ArrayList()
        thursdayAdapter.notifyDataSetChanged()
        thursdayRc.adapter = thursdayAdapter
        initThursdayRc()
        thursdayRc.adapter = thursdayAdapter
        thursdayRc.layoutManager = LinearLayoutManager(this.context)

        val fridayRc: RecyclerView = view.findViewById(R.id.fridayRc)
        fridayRc.layoutManager = LinearLayoutManager(this.context)
        fridayAdapter.clearRecords()
        fridayAdapter.scheduleRecordsList =  ArrayList()
        fridayAdapter.notifyDataSetChanged()
        fridayRc.adapter = fridayAdapter
        initFridayRc()
        fridayRc.adapter = fridayAdapter
        fridayRc.layoutManager = LinearLayoutManager(this.context)

        val saturdayRc: RecyclerView = view.findViewById(R.id.saturdayRc)
        saturdayRc.layoutManager = LinearLayoutManager(this.context)
        saturdayAdapter.clearRecords()
        saturdayAdapter.scheduleRecordsList =  ArrayList()
        saturdayAdapter.notifyDataSetChanged()
        saturdayRc.adapter = saturdayAdapter
        initSaturdayRc()
        saturdayRc.adapter = saturdayAdapter
        saturdayRc.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initSaturdayRc() {
        binding.apply {
            val schr1 = ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И.")
            saturdayAdapter.addScheduleRecord(schr1)
            saturdayAdapter.notifyDataSetChanged()
            saturdayAdapter.addScheduleRecord(schr1)
            saturdayAdapter.notifyDataSetChanged()
        }
    }

    private fun initFridayRc() {
        binding.apply {
            val schr1 = ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И.")
            fridayAdapter.addScheduleRecord(schr1)
            fridayAdapter.notifyDataSetChanged()
            fridayAdapter.addScheduleRecord(schr1)
            fridayAdapter.notifyDataSetChanged()
        }
    }

    private fun initThursdayRc() {
        binding.apply {
            val schr1 = ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И.")
            thursdayAdapter.addScheduleRecord(schr1)
            thursdayAdapter.notifyDataSetChanged()
            thursdayAdapter.addScheduleRecord(schr1)
            thursdayAdapter.notifyDataSetChanged()
        }
    }

    private fun initWednesdayRc() {
        binding.apply {
            val schr1 = ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И.")
            wednesdayAdapter.addScheduleRecord(schr1)
            wednesdayAdapter.notifyDataSetChanged()
            wednesdayAdapter.addScheduleRecord(schr1)
            wednesdayAdapter.notifyDataSetChanged()

        }
    }

    private fun initTuesdayRc() {
        binding.apply {
            val schr1 = ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И.")
            tuesdayAdapter.addScheduleRecord(schr1)
            tuesdayAdapter.notifyDataSetChanged()
            tuesdayAdapter.addScheduleRecord(schr1)
            tuesdayAdapter.notifyDataSetChanged()
        }
    }

    private fun initMondayRc() {
        binding.apply {
            val schr1 = ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И.")
            mondayAdapter.addScheduleRecord(schr1)
            mondayAdapter.notifyDataSetChanged()
            mondayAdapter.addScheduleRecord(schr1)
            mondayAdapter.notifyDataSetChanged()

        }
    }


}