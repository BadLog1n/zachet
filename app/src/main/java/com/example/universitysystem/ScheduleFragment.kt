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
        //mondayRc.layoutManager = LinearLayoutManager(this.context)
        mondayAdapter.clearRecords()
        mondayAdapter.scheduleRecordsList =  ArrayList()
        mondayAdapter.notifyDataSetChanged()
        mondayRc.adapter = mondayAdapter
        mondayRc.adapter = mondayAdapter
        mondayRc.layoutManager = LinearLayoutManager(this.context)

        val tuesdayRc: RecyclerView = view.findViewById(R.id.tuesdayRc)
        //tuesdayRc.layoutManager = LinearLayoutManager(this.context)
        tuesdayAdapter.clearRecords()
        tuesdayAdapter.scheduleRecordsList =  ArrayList()
        tuesdayAdapter.notifyDataSetChanged()
        tuesdayRc.adapter = tuesdayAdapter
        tuesdayRc.adapter = tuesdayAdapter
        tuesdayRc.layoutManager = LinearLayoutManager(this.context)

        val wednesdayRc: RecyclerView = view.findViewById(R.id.wednesdayRc)
        //wednesdayRc.layoutManager = LinearLayoutManager(this.context)
        wednesdayAdapter.clearRecords()
        wednesdayAdapter.scheduleRecordsList =  ArrayList()
        wednesdayAdapter.notifyDataSetChanged()
        wednesdayRc.adapter = wednesdayAdapter
        wednesdayRc.adapter = wednesdayAdapter
        wednesdayRc.layoutManager = LinearLayoutManager(this.context)

        val thursdayRc: RecyclerView = view.findViewById(R.id.thursdayRc)
        //thursdayRc.layoutManager = LinearLayoutManager(this.context)
        thursdayAdapter.clearRecords()
        thursdayAdapter.scheduleRecordsList =  ArrayList()
        thursdayAdapter.notifyDataSetChanged()
        thursdayRc.adapter = thursdayAdapter
        thursdayRc.adapter = thursdayAdapter
        thursdayRc.layoutManager = LinearLayoutManager(this.context)

        val fridayRc: RecyclerView = view.findViewById(R.id.fridayRc)
        //fridayRc.layoutManager = LinearLayoutManager(this.context)
        fridayAdapter.clearRecords()
        fridayAdapter.scheduleRecordsList =  ArrayList()
        fridayAdapter.notifyDataSetChanged()
        fridayRc.adapter = fridayAdapter
        fridayRc.adapter = fridayAdapter
        fridayRc.layoutManager = LinearLayoutManager(this.context)

        val saturdayRc: RecyclerView = view.findViewById(R.id.saturdayRc)
        //saturdayRc.layoutManager = LinearLayoutManager(this.context)
        saturdayAdapter.clearRecords()
        saturdayAdapter.scheduleRecordsList =  ArrayList()
        saturdayAdapter.notifyDataSetChanged()
        saturdayRc.adapter = saturdayAdapter
        saturdayRc.adapter = saturdayAdapter
        saturdayRc.layoutManager = LinearLayoutManager(this.context)

        var records:MutableList<ScheduleRecord> = mutableListOf()
        records.add(ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Вторник","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Среда","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Четверг","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Пятница","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Суббота","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Понедельник","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Вторник","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Среда","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Четверг","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Пятница","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Суббота","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Четверг","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Суббота","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Среда","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Среда","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Среда","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        records.add(ScheduleRecord("Среда","8.00","Базы данных(лб)","а-217, Аникина Е.И."))
        initRcs(records)
    }

    private fun initRcs(records:MutableList<ScheduleRecord>) {
        binding.apply {
            for (i in records){
                when (i.weekday){
                    "Понедельник"->{
                        mondayAdapter.addScheduleRecord(i)
                        mondayAdapter.notifyDataSetChanged()
                        mondayRc.adapter = mondayAdapter
                        mondayRc.layoutManager = LinearLayoutManager(this@ScheduleFragment.context)}
                    "Вторник"->{
                        tuesdayAdapter.addScheduleRecord(i)
                        tuesdayAdapter.notifyDataSetChanged()
                        tuesdayRc.adapter = tuesdayAdapter
                        tuesdayRc.layoutManager = LinearLayoutManager(this@ScheduleFragment.context)}
                    "Среда"->{
                        wednesdayAdapter.addScheduleRecord(i)
                        wednesdayAdapter.notifyDataSetChanged()
                        wednesdayRc.adapter = wednesdayAdapter
                        wednesdayRc.layoutManager = LinearLayoutManager(this@ScheduleFragment.context)}
                    "Четверг"->{
                        thursdayAdapter.addScheduleRecord(i)
                        thursdayAdapter.notifyDataSetChanged()
                        thursdayRc.adapter = thursdayAdapter
                        thursdayRc.layoutManager = LinearLayoutManager(this@ScheduleFragment.context)}
                    "Пятница"->{
                        fridayAdapter.addScheduleRecord(i)
                        fridayAdapter.notifyDataSetChanged()
                        fridayRc.adapter = fridayAdapter
                        fridayRc.layoutManager = LinearLayoutManager(this@ScheduleFragment.context)}
                    "Суббота"->{
                        saturdayAdapter.addScheduleRecord(i)
                        saturdayAdapter.notifyDataSetChanged()
                        saturdayRc.adapter = saturdayAdapter
                        saturdayRc.layoutManager = LinearLayoutManager(this@ScheduleFragment.context)}
                }

            }
        }
    }
}