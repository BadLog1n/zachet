package authCheck

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.example.zachet.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AuthCheck {
    private val saveUserid = "save_userid"
    private val savePassword = "save_password"
    private val checkSettings = "check_settings"
    private var un = ""
    private var pw = ""
    private lateinit var database: DatabaseReference

    fun check(view: View, context: Context?){
        val sharedPref: SharedPreferences? = context?.getSharedPreferences("Settings", MODE_PRIVATE)

        if (sharedPref?.getBoolean(checkSettings, false) == true) {
        un = sharedPref.getString(saveUserid, "").toString()
        pw = sharedPref.getString(savePassword, "").toString()

            database = FirebaseDatabase.getInstance().getReference("users/$un")
            val requestToDatabase = database.get()
            requestToDatabase.addOnSuccessListener {
                val password = it.child("password").value.toString()
                if (password != pw) {
                    sharedPref.edit()?.putBoolean(checkSettings, false)?.apply()
                    Toast.makeText(context, "Логин или пароль не верен.", Toast.LENGTH_SHORT).show()
                    findNavController(view).navigate(R.id.loginFragment)
                }
            }
    }
    }


    }


