package com.example.universitysystem.espresso

import android.widget.EditText
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.example.universitysystem.Message
import com.example.universitysystem.MyDatabase
import com.example.universitysystem.R
import com.example.universitysystem.SettingsFragment
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsDbIntegrTest {
    private var scenario: FragmentScenario<SettingsFragment>? = null
    private lateinit var mnet: EditText
    private lateinit var mset: EditText
    private lateinit var mlet: EditText
    private lateinit var mpet: EditText
    private lateinit var mdb: Message
    private lateinit var mun: String


    @Test
    fun dbAndActualInfo() {
        val result = mdb.sendMessage("1", "1", "1", "text")
        Thread.sleep(3000)

        Assert.assertEquals(true, result)

/*

    @Test
    fun changePasswordActual() {
        onView(withId(R.id.passText)).perform(clearText())
        onView(withId(R.id.passText)).perform(typeText("password"))
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.saveSettingsBtn)).perform(click())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mdb.extractInfo()

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val dbPassword: String? = mdb.settingsMap["password"]
        Assert.assertEquals("password", dbPassword)
        onView(withId(R.id.passText)).perform(replaceText("1"))
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        onView(withId(R.id.saveSettingsBtn)).perform(click())
    }

    @Test
    fun changeLoginActual() {
        onView(withId(R.id.loginText)).check(matches(isNotEnabled()))
    }

    @Test
    fun changeNameActual() {
        onView(withId(R.id.nameText)).perform(clearText())
        //onView(withId(R.id.nameText)).perform(typeText("имя"))
        onView(withId(R.id.nameText)).perform(replaceText("имя"))
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.saveSettingsBtn)).perform(click())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mdb.extractInfo()

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val dbPassword: String? = mdb.settingsMap["name"]
        Assert.assertEquals("имя", dbPassword)

        onView(withId(R.id.nameText)).perform(replaceText("Е"))
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        onView(withId(R.id.saveSettingsBtn)).perform(click())
    }

    @Test
    fun changeSurnameActual() {
        onView(withId(R.id.surnameText)).perform(clearText())
        //onView(withId(R.id.surnameText)).perform(typeText("фамилия"))
        onView(withId(R.id.surnameText)).perform(replaceText("фамилия"))
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.saveSettingsBtn)).perform(click())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mdb.extractInfo()

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val dbPassword: String? = mdb.settingsMap["surname"]
        Assert.assertEquals("фамилия", dbPassword)
        onView(withId(R.id.surnameText)).perform(replaceText("А"))
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        onView(withId(R.id.saveSettingsBtn)).perform(click())
    }

    @After
    fun tearDown() {
*/

        /*onView(withId(R.id.nameText)).perform(replaceText("Е"))

        //onView(withId(R.id.surnameText)).perform(clearText())
        onView(withId(R.id.surnameText)).perform(replaceText("А"))

        onView(withId(R.id.passText)).perform(replaceText("1"))
        onView(withId(R.id.settingsLinearLayout)).perform(closeSoftKeyboard())
        onView(withId(R.id.saveSettingsBtn)).perform(scrollTo())
        onView(withId(R.id.saveSettingsBtn)).perform(click())*/
    }
}