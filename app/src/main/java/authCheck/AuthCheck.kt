package authCheck

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.oneseed.zachet.R


class AuthCheck {
    private val saveEmail = "email"
    private val savePassword = "save_password"
    private val checkSettings = "check_settings"

    fun check(view: View, context: Context?) {
        val sharedPref: SharedPreferences? = context?.getSharedPreferences("Settings", MODE_PRIVATE)


        val user = FirebaseAuth.getInstance().currentUser
        val email = sharedPref?.getString(saveEmail, "null").toString()
        val password = sharedPref?.getString(savePassword, "null").toString()


        val credential = EmailAuthProvider
            .getCredential(email, password)

        if (isNetworkAvailable(context)) {
// Prompt the user to re-provide their sign-in credentials
            user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User re-authenticated.")
                } else {
                    sharedPref?.edit()?.putBoolean(checkSettings, false)?.apply()
                    Toast.makeText(context, "Логин или пароль не верен.", Toast.LENGTH_SHORT).show()
                    findNavController(view).navigate(R.id.loginFragment)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }


}


