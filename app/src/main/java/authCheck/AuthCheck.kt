package authCheck

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.oneseed.zachet.R
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup


class AuthCheck {
    private val saveEmail = "email"
    private val savePassword = "save_password"
    private val checkSettings = "check_settings"

    @OptIn(DelicateCoroutinesApi::class)
    fun check(view: View, context: Context?) {
        val sharedPref: SharedPreferences? = context?.getSharedPreferences("Settings", MODE_PRIVATE)


        val user = FirebaseAuth.getInstance().currentUser
        val email = sharedPref?.getString(saveEmail, "null").toString()
        val password = sharedPref?.getString(savePassword, "null").toString()

        val credential = EmailAuthProvider
            .getCredential(email, password)
        GlobalScope.launch {
            try {
                val sitePath =
                    "https://ya.ru/"
                val response: Connection.Response = Jsoup.connect(sitePath)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(10000)
                    .execute()
                val statusCode: Int = response.statusCode()
                val document =
                    if (statusCode == 200) Jsoup.connect(sitePath).get().text() else ""
                if (document != "") {
                    FirebaseAuth.getInstance().currentUser?.reload()
                    withContext(Dispatchers.Main) {
                        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                sharedPref?.edit()?.putBoolean(checkSettings, false)?.apply()
                                Toast.makeText(
                                    context,
                                    "Логин или пароль не верен",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                findNavController(view).navigate(R.id.loginFragment)
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                Log.d("dat", "error in AuthCheck")

            }
        }
    }


}


