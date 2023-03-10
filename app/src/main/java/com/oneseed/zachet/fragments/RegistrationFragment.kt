package com.oneseed.zachet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup


class RegistrationFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val regEmailText = view.findViewById<EditText>(R.id.regEmailText)
        val switch = requireView().findViewById<SwitchMaterial>(R.id.switchCode)
        val regPasswordText = view.findViewById<EditText>(R.id.regPasswordText)
        val regCodeText = view.findViewById<EditText>(R.id.regCodeText)
        val layoutRegCode = view.findViewById<TextInputLayout>(R.id.layoutRegCode)
        val inputPasswordInfo = view.findViewById<TextInputLayout>(R.id.inputPasswordInfo)
        val inputPasswordInfoText = view.findViewById<EditText>(R.id.inputPasswordInfoText)
        val inputLoginInfo = view.findViewById<TextInputLayout>(R.id.inputLoginInfo)
        val inputLoginInfoText = view.findViewById<EditText>(R.id.inputLoginInfoText)
        auth = Firebase.auth
        view.findViewById<Button>(R.id.regButton).setOnClickListener {
            while (true) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(regEmailText.text.toString())
                        .matches()
                ) {
                    Toast.makeText(
                        requireContext(), "Ввденная почта некорректна", Toast.LENGTH_SHORT
                    ).show()
                    break
                }
                if (regPasswordText.text.toString().length < 6) {
                    Toast.makeText(
                        requireContext(),
                        "Пароль должен быть минимум из 6 знаков",
                        Toast.LENGTH_SHORT
                    ).show()
                    break
                }

                if (switch.isChecked) {
                    Toast.makeText(
                        requireContext(), "Ожидайте", Toast.LENGTH_SHORT
                    ).show()
                    val databaseRefNew = FirebaseDatabase.getInstance().getReference("noCode").get()
                    databaseRefNew.addOnSuccessListener { element ->
                        if (!element.exists()) {
                            Toast.makeText(
                                requireContext(),
                                "Регистрация временно недоступна",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        }
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                if (authInfo(
                                        inputLoginInfoText.text.toString(),
                                        inputPasswordInfoText.text.toString()
                                    )
                                ) {

                                    withContext(Dispatchers.Main) {
                                        auth.createUserWithEmailAndPassword(
                                            regEmailText.text.toString(),
                                            regPasswordText.text.toString()
                                        ).addOnCompleteListener(requireActivity()) { task ->
                                            if (task.isSuccessful) {
                                                val uid = Firebase.auth.currentUser?.uid

                                                val userLogin = uid.toString().lowercase()
                                                    .take(6) + ('a'..'z').random()

                                                FirebaseDatabase.getInstance().getReference("login")
                                                    .child(userLogin).setValue(uid)
                                                FirebaseDatabase.getInstance()
                                                    .getReference("users/${uid.toString()}/login")
                                                    .setValue(userLogin)
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Успешная регистрация",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                findNavController().navigate(R.id.loginFragment)
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Пожалуйста, проверьте вводимые данные",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Не удается подключиться к сайту. Проверьте вводимые данные",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }

                                }

                            }
                        }
                    }

                } else {
                    var isTeacher: Boolean
                    val databaseRefNew = FirebaseDatabase.getInstance().getReference("code")
                        .child(regCodeText.text.toString()).get()
                    databaseRefNew.addOnSuccessListener { element ->
                        if (!element.exists()) {
                            Toast.makeText(
                                requireContext(), "Код не подходит", Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        } else {
                            isTeacher = element.value.toString().toBoolean()
                        }
                        auth.createUserWithEmailAndPassword(
                            regEmailText.text.toString(), regPasswordText.text.toString()
                        ).addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val uid = Firebase.auth.currentUser?.uid

                                val userLogin =
                                    uid.toString().lowercase().take(6) + ('a'..'z').random()

                                FirebaseDatabase.getInstance().getReference("login")
                                    .child(userLogin).setValue(uid)
                                FirebaseDatabase.getInstance()
                                    .getReference("users/${uid.toString()}/login")
                                    .setValue(userLogin)
                                FirebaseDatabase.getInstance().getReference("code")
                                    .child(regCodeText.text.toString()).removeValue()
                                if (isTeacher) {
                                    FirebaseDatabase.getInstance()
                                        .getReference("users/${uid.toString()}/isTeacher")
                                        .setValue(true)
                                }
                                Toast.makeText(
                                    requireContext(), "Успешная регистрация", Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.loginFragment)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Пожалуйста, проверьте вводимые данные",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                break
            }
        }




        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                layoutRegCode.visibility = View.GONE
                inputPasswordInfo.visibility = View.VISIBLE
                inputLoginInfo.visibility = View.VISIBLE
            } else {
                layoutRegCode.visibility = View.VISIBLE
                inputPasswordInfo.visibility = View.GONE
                inputLoginInfo.visibility = View.GONE
            }
        }


    }


    private fun authInfo(login: String, password: String): Boolean {
        try {
            val document: String
            val sitePath =
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=$login&password=$password&type=array"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(30000).execute()

            val statusCode: Int = response.statusCode()

            if (statusCode == 200) {
                document = Jsoup.connect(sitePath).get().text()
            } else return false
            return document.isNotEmpty()
        } catch (e: Exception) {
            Log.d("getFormAndGroup", e.toString())
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

}