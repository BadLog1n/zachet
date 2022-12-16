package com.oneseed.zachet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class RegistrationFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val regEmailText = view.findViewById<EditText>(R.id.regEmailText)
        val regLoginText = view.findViewById<EditText>(R.id.regLoginText)
        val regPasswordText = view.findViewById<EditText>(R.id.regPasswordText)
        val regCodeText = view.findViewById<EditText>(R.id.regCodeText)
        auth = Firebase.auth
        view.findViewById<Button>(R.id.regButton).setOnClickListener {

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(regEmailText.text.toString())
                    .matches()
            ) {
                Toast.makeText(
                    requireContext(),
                    "Ввденная почта некорректна",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
/*                user!!.updateEmail(emailInputText.text.toString())
                sharedPrefSettings?.edit()
                    ?.putString(getString(R.string.emailShared), emailInputText.text.toString())
                    ?.apply()*/
            }
            if (regPasswordText.text.toString().length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Пароль должен быть минимум из 6 знаков",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (regLoginText.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Логин не может быть пустым",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                var isTeacher: Boolean
                val databaseRef = FirebaseDatabase.getInstance().getReference("login")
                    .child(regLoginText.text.toString()).get()
                databaseRef.addOnSuccessListener { item ->
                    if (item.exists()) {
                        Toast.makeText(
                            requireContext(),
                            "Логин уже занят",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnSuccessListener
                    }
                    val databaseRefNew = FirebaseDatabase.getInstance().getReference("code")
                        .child(regCodeText.text.toString()).get()
                    databaseRefNew.addOnSuccessListener { element ->
                        if (!element.exists()) {
                            Toast.makeText(
                                requireContext(),
                                "Код не подходит",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        } else {
                            isTeacher = element.value.toString().toBoolean()
                        }
                        auth.createUserWithEmailAndPassword(
                            regEmailText.text.toString(),
                            regPasswordText.text.toString()
                        )
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    val uid = Firebase.auth.currentUser?.uid
                                    FirebaseDatabase.getInstance().getReference("login")
                                        .child(regLoginText.text.toString()).setValue(uid)
                                    FirebaseDatabase.getInstance()
                                        .getReference("users/${uid.toString()}/login")
                                        .setValue(regLoginText.text.toString())
                                    FirebaseDatabase.getInstance().getReference("code")
                                        .child(regCodeText.text.toString()).removeValue()
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(
                                        requireContext(),
                                        "Успешная регистрация",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (isTeacher) {
                                        FirebaseDatabase.getInstance()
                                            .getReference("users/${uid.toString()}/isTeacher")
                                            .setValue(true)
                                    }
                                    findNavController().navigate(R.id.loginFragment)


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(
                                        requireContext(),
                                        "Пожалуйста, проверьте вводимые данные",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    }
                }

            }


        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

}