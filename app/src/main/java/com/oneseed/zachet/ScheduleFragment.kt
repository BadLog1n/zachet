package com.oneseed.zachet

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oneseed.zachet.databinding.FragmentScheduleBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.*


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    private lateinit var database: DatabaseReference


    private lateinit var binding: FragmentScheduleBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinner = requireView().findViewById<Spinner>(R.id.spinner)
        val switch = requireView().findViewById<Switch>(R.id.switchh)
        val swipeRefreshLayout = requireView().findViewById<SwipeRefreshLayout>(R.id.swipe)
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.settingsShared),
            Context.MODE_PRIVATE
        )
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduleBinding.inflate(layoutInflater)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Расписание"
        getGroups(spinner)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val scheduleRc: RecyclerView = view.findViewById(R.id.scheduleRc)

        //timetableGet(view.findViewById<Spinner>(R.id.spinner).selectedItem.toString(), "up")
        scheduleRc.adapter = adapter
        scheduleRc.layoutManager = LinearLayoutManager(this.context)
        val progressBar: ProgressBar = view.findViewById(R.id.scheduleProgressBar)
        //timetableGet("П")
        val isDownWeekFirstLoad = sharedPref?.getBoolean(getString(R.string.isDownWeek), false)

        val upDownTextFirstLoad = if (isDownWeekFirstLoad == true) "down" else "up"

        val loadScheduleFirstLoad =
            sharedPref?.getString("scheduleShared$upDownTextFirstLoad", "").toString()

        if (loadScheduleFirstLoad.isNotEmpty()) {
            timetableGetCache(loadScheduleFirstLoad)
        }


        swipeRefreshLayout.setOnRefreshListener {

            try {
                val spinnerElement = spinner.selectedItem.toString()
                val switchState: Boolean = switch.isChecked

                if (spinnerElement != "") {
                    progressBar.visibility = View.VISIBLE
                    timetableGet(spinnerElement, switchState)
                    progressBar.visibility = View.GONE
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    swipeRefreshLayout.isRefreshing = false
                }, 1000)
            }
            catch (_: Exception) {
                swipeRefreshLayout.isRefreshing = false
            }
        }


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


                val spinnerElement = spinner.selectedItem.toString()
                sharedPref?.edit()?.putString(getString(R.string.groupSpinner), spinnerElement)
                    ?.apply()


                val isDownWeek = sharedPref?.getBoolean(getString(R.string.isDownWeek), false)

                val upDownText = if (isDownWeek == true) "down" else "up"

                val loadSchedule =
                    sharedPref?.getString("scheduleShared$upDownText", "").toString()
                val group =
                    sharedPref?.getString("groupShared$upDownText", "").toString()


                if (isDownWeek == true) {
                    switch.isChecked = true
                }
                val switchState: Boolean = switch.isChecked
                if (loadSchedule.isNotEmpty() && group == spinnerElement) {
                    timetableGetCache(loadSchedule)
                } else {
                    progressBar.visibility = View.VISIBLE
                    timetableGet(spinnerElement, switchState)
                    progressBar.visibility = View.GONE

                }
            }


        }


        // div with class=masthead


        switch.setOnCheckedChangeListener { _, _ ->
            val switchState: Boolean = switch.isChecked
            val upDownText = if (switchState) "down" else "up"
            val loadSchedule =
                sharedPref?.getString("scheduleShared$upDownText", "").toString()

            if (spinner.selectedItem != null) {
                progressBar.visibility = View.VISIBLE


                val group =
                    sharedPref?.getString("groupShared$upDownText", "").toString()
                if (loadSchedule.isNotEmpty() && group == spinner.selectedItem.toString()) {
                    timetableGetCache(loadSchedule)
                    progressBar.visibility = View.GONE

                } else {
                    timetableGet(spinner.selectedItem.toString(), switchState)
                    progressBar.visibility = View.GONE


                }
            }

            if (loadSchedule.isNotEmpty()) {
                progressBar.visibility = View.GONE
                timetableGetCache(loadSchedule)
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
            val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
                getString(R.string.settingsShared),
                Context.MODE_PRIVATE
            )
            val groupLoad = sharedPref?.getString(getString(R.string.groupSpinner), "").toString()

            if (groupLoad != "") {
                groups.remove(groupLoad)
                groups.add(0, groupLoad)
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
            } catch (_: IllegalStateException) {
            }
            val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_item, groups)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }
    }

    private var subjects = arrayListOf<ScheduleRecordItem>()
    private fun timetableGet(group: String, upDown: Boolean) {
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.settingsShared),
            Context.MODE_PRIVATE
        )
        var stringOfSchedule = ""
        val adapter = GroupAdapter<GroupieViewHolder>()
        val scheduleRc: RecyclerView = requireView().findViewById(R.id.scheduleRc)
        val upDownText = if (upDown) "down" else "up"

        val rusDay = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
        val day = arrayOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
        database = FirebaseDatabase.getInstance().getReference("timetable/$group/$upDownText")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener { gets ->
            day.forEachIndexed { index, element ->
                stringOfSchedule += "$element;"
                for (timeCabSub in gets.child(element).children) {
                    val subject =
                        ScheduleRecordItem(
                            timeCabSub.key.toString(),
                            timeCabSub.child("cabName").value.toString(),
                            timeCabSub.child("subject").value.toString()
                        )
                    stringOfSchedule += timeCabSub.key.toString() + ";"
                    stringOfSchedule += timeCabSub.child("cabName").value.toString() + ";"
                    stringOfSchedule += timeCabSub.child("subject").value.toString() + ";"
                    subjects.add(subject)
                }
                sharedPref?.edit()?.putString("scheduleShared$upDownText", stringOfSchedule)
                    ?.apply()
                sharedPref?.edit()?.putString("groupShared$upDownText", group)
                    ?.apply()

                subjects = ArrayList(subjects.sortedWith(compareBy { it.time }))
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

    private fun timetableGetCache(scheduleText: String) {
        var scheduleTextLocal = scheduleText
        scheduleTextLocal = scheduleTextLocal.substringAfter("monday;")

        val scheduleArray = arrayListOf<String>()
        val adapter = GroupAdapter<GroupieViewHolder>()
        val scheduleRc: RecyclerView = requireView().findViewById(R.id.scheduleRc)
        val rusDay = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
        val dayForSplit =
            arrayOf("tuesday;", "wednesday;", "thursday;", "friday;", "saturday;", ";")
        for (item in dayForSplit) {
            if (item != ";") {
                scheduleArray.add(scheduleTextLocal.substringBefore(item))
                scheduleTextLocal = scheduleTextLocal.substringAfter(item)
            } else {
                scheduleTextLocal = scheduleTextLocal.substringBeforeLast(item) + " "
                scheduleArray.add(scheduleTextLocal)
            }
        }

        var index = 0
        for (timeCabSub in scheduleArray) {
            if (timeCabSub.isNotEmpty() && timeCabSub != " ") {
                val scheduleTextArray =
                    timeCabSub.substring(0, timeCabSub.length - 1).split(";").toTypedArray()
                println(scheduleTextArray.size)
                for (item in scheduleTextArray) {
                    println(item)
                }
                //  println(scheduleTextArray[7])
                var indexLocal = 0
                while (indexLocal < scheduleTextArray.size) {
                    val subject =
                        ScheduleRecordItem(
                            scheduleTextArray[indexLocal],
                            scheduleTextArray[indexLocal + 1],
                            scheduleTextArray[indexLocal + 2]
                        )
                    indexLocal += 3
                    subjects.add(subject)
                }
                subjects = ArrayList(subjects.sortedWith(compareBy { it.time }))
                adapter.add(WeekDayItem(rusDay[index]))
                for (item in subjects) {
                    adapter.add(ScheduleRecordItem(item.time, item.cabName, item.subject))
                }
                subjects.clear()
                index += 1
            } else {
                adapter.add(WeekDayItem(rusDay[index]))
                index += 1
            }
        }

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