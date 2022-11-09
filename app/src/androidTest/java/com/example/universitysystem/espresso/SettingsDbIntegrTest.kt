package com.example.universitysystem.espresso

import android.widget.EditText
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.universitysystem.MyDatabase
import com.example.universitysystem.R
import com.example.universitysystem.SettingsFragment
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsDbIntegrTest {
    private var scenario: FragmentScenario<SettingsFragment>? = null
    var fr: SettingsFragment = SettingsFragment()
    private lateinit var mnet:EditText
    private lateinit var mset:EditText
    private lateinit var mlet:EditText
    private lateinit var mpet:EditText
    private lateinit var mdb:MyDatabase
    private lateinit var mun:String

    @Before
    fun setUp() {
        mun = "someUn"
        scenario = launchFragmentInContainer<SettingsFragment>()
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        scenario?.withFragment {
            mlet = this.view?.findViewById<EditText>(R.id.loginText)!!
            mpet = this.view?.findViewById<EditText>(R.id.passText)!!
            mnet = this.view?.findViewById<EditText>(R.id.nameText)!!
            mset = this.view?.findViewById<EditText>(R.id.surnameText)!!
            mdb = this.getDb()

        }

    }
    @Test
    fun dbAndActualInfo() {
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (mdb.isInfoGot){
            mdb.extractInfo()
        }
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val dbName:String? = mdb.settingsMap["name"]
        val dbSurname:String? = mdb.settingsMap["surname"]
        val dbLogin:String? = mdb.settingsMap["login"]
        val dbPassword:String? = mdb.settingsMap["password"]
        Assert.assertEquals(dbName, mnet.text.toString())
        Assert.assertEquals(dbSurname, mset.text.toString())
        Assert.assertEquals(dbLogin, mlet.text.toString())
        Assert.assertEquals(dbPassword, mpet.text.toString())
    }

    @Test
    fun changePassword() {
        onView(withId(R.id.passText)).perform(clearText())
        onView(withId(R.id.passText)).perform(typeText("password"))
        onView(withId(R.id.saveSettingsBtn)).perform(click())
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (mdb.isInfoGot){
            mdb.extractInfo()
        }
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val dbName:String? = mdb.settingsMap["name"]
        val dbSurname:String? = mdb.settingsMap["surname"]
        val dbLogin:String? = mdb.settingsMap["login"]
        val dbPassword:String? = mdb.settingsMap["password"]
        Assert.assertEquals(dbName, mnet.text.toString())
        Assert.assertEquals(dbSurname, mset.text.toString())
        Assert.assertEquals(dbLogin, mlet.text.toString())
        Assert.assertEquals(dbPassword, mpet.text.toString())
    }

}