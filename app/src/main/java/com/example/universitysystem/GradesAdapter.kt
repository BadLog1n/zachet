package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.SubjectGradesItemBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.*
import kotlin.collections.ArrayList

lateinit var userChatId:UUID // наша переменная для айди чата или препода на уровне адаптера(файла в котором мы сейчас)
class GradesAdapter:RecyclerView.Adapter<GradesAdapter.GradesHolder>() {
    var gradesList = ArrayList<SubjectGrades>()

    class GradesHolder(item:View):RecyclerView.ViewHolder (item){

        private val binding = SubjectGradesItemBinding.bind(item)
        fun bind(subjectGrades: SubjectGrades) = with(binding){
            tv1Subject.text = subjectGrades.subject
            grAll.text = subjectGrades.allGradesCount.toString()
            expandBtn.setImageResource(R.drawable.ic_arrow_down)
            tv2Subject.text = subjectGrades.subject
            collapseBtn.setImageResource(R.drawable.ic_arrow_up)
            typeOfSubject.text = subjectGrades.typeOfSubject
            segment1.text = subjectGrades.segments[0]
            segment2.text = subjectGrades.segments[1]
            segment3.text = subjectGrades.segments[2]
            segment4.text = subjectGrades.segments[3]
            seg1Visit.text = subjectGrades.grades[0].toString()
            seg1Academ.text = subjectGrades.grades[1].toString()
            seg2Visit.text = subjectGrades.grades[2].toString()
            seg2Academ.text = subjectGrades.grades[3].toString()
            seg3Visit.text = subjectGrades.grades[4].toString()
            seg3Academ.text = subjectGrades.grades[5].toString()
            seg4Visit.text = subjectGrades.grades[6].toString()
            seg4Academ.text = subjectGrades.grades[7].toString()
            val test = "Баллы за ${subjectGrades.typeOfSubject}:"
            grForTestingTv.text = test
            grForTestingCount.text = subjectGrades.grades[8].toString()
            additionalGrTv.text = "Дополнительные баллы:"
            additionalGrCount.text = subjectGrades.grades[9].toString()
            premiumGrTv.text = "Премиальные баллы:"
            premiumGrCount.text = subjectGrades.grades[10].toString()
            /**
             * Здесь мы этой красоте присваиваем значение. А именно:
             * Теперь когда мы добавляем в ресайклер вью новый айтем(новый предмет типа) мы туда передаем +1
             * новый параметр userChatId (название и тип можешь поменять потом если я промахнулась и
             * не угадала). Этот параметр мы отлавливаем так сказать в строке ниже, присваиваем значение,
             * которое мы получили когда создавали новый объект(добавляли в список новый предмет грубо говоря)
             * переменной на уровне адаптера.
             */
            userChatId = subjectGrades.userChatId


        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_grades_item,parent,false)
        view.findViewById<ImageButton>(R.id.expandBtn).setOnClickListener {
            val expView = view.findViewById<LinearLayout>(R.id.expandedView)
            expView.visibility = View.VISIBLE
            val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
            collView.visibility = View.GONE
        }

        view.findViewById<ImageButton>(R.id.collapseBtn).setOnClickListener {
            val expView = view.findViewById<LinearLayout>(R.id.expandedView)
            expView.visibility = View.GONE
            val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
            collView.visibility = View.VISIBLE
        }
        val expView = view.findViewById<LinearLayout>(R.id.expandedView)
        expView.visibility = View.GONE

        view.findViewById<ImageButton>(R.id.spechBubbblesImg).setOnClickListener {
            val intent = Intent(parent.context,IndividualChatActivity::class.java)
            parent.context.startActivity(intent)
        }
        view.findViewById<TextView>(R.id.connectWTeacher_tv).setOnClickListener {
            val intent = Intent(parent.context,IndividualChatActivity::class.java)
            /**Здесь мы значение переменной грубо говоря передаем каждой кнопке в разных окнах.
             * То есть у каждого окна свой обработчик нажатия засчет того что userChatId - переменная.
             * Если интересна логика - то метод вверху и этот метод вызывается для каждого элемента списка.
             * **/
            intent.putExtra("smthn",userChatId)
            parent.context.startActivity(intent)
        }
        return GradesHolder(view)
    }

    override fun onBindViewHolder(holder: GradesHolder, position: Int) {
        holder.bind(gradesList[position])
    }

    override fun getItemCount(): Int {
        return gradesList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addSubjectGrades(subjectGrades: SubjectGrades){
        gradesList.add(subjectGrades)
        notifyDataSetChanged()
    }

    fun clearRecords(){
        gradesList.removeAll(gradesList.toSet())
    }
}