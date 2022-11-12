package com.example.universitysystem

import android.widget.EditText
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.database.DatabaseReference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.stubbing.Answer

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {
    private var fr: SettingsFragment = SettingsFragment()
    private lateinit var mnet: EditText
    private lateinit var mset: EditText
    private lateinit var mlet: EditText
    private lateinit var mpet: EditText
    private lateinit var mdb: MyDatabase
    private lateinit var mun: String
    private var scenario: FragmentScenario<SettingsFragment>? = null
    private lateinit var mdbref: DatabaseReference

    @Before
    fun setUp() {
        mun = "someUn"
        scenario = launchFragmentInContainer()
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        scenario?.withFragment {
            mlet = this.view?.findViewById(R.id.loginText)!!
            mpet = this.view?.findViewById(R.id.passText)!!
            mnet = this.view?.findViewById(R.id.nameText)!!
            mset = this.view?.findViewById(R.id.surnameText)!!
            /*mdb = Mockito.mock(MyDatabase::class.java)
            Mockito.`when`(mdb.getSettingsInfo(mnet,mset,mlet,mpet)).thenAnswer(Answer { invocation ->
                mset.setText("surname")
                mpet.setText("password")
                mnet.setText("name")
                mlet.setText("login")
                mdb.settingsMap = mutableMapOf("name" to "","surname" to "","login" to "","password" to "")
                mdb.settingsMap["name"] = "name"
                mdb.settingsMap["surname"] = "surname"
                mdb.settingsMap["password"] = "password"
            })*/
        }
        mdb = Mockito.mock(MyDatabase::class.java)
        mdbref = Mockito.mock(DatabaseReference::class.java)
        mdb.dbref = mdbref
        Mockito.`when`(mdb.getSettingsInfo(mnet, mset, mlet, mpet))
            .thenAnswer(Answer { invocation ->
                /*mset.setText("surname")
                mpet.setText("password")
                mnet.setText("name")
                mlet.setText("login")*/
                mdb.settingsMap =
                    mutableMapOf("name" to "", "surname" to "", "login" to "", "password" to "")
                mdb.settingsMap["name"] = "Степан"
                mdb.settingsMap["surname"] = "Немченко"
                mdb.settingsMap["password"] = "900071909"
            })
        Mockito.`when`(mdb.saveName("name")).thenAnswer(Answer { invocation ->
            mdb.settingsMap["name"] = "name"
        })
        Mockito.`when`(mdb.saveSurname("surname")).thenAnswer(Answer { invocation ->
            mdb.settingsMap["surname"] = "surname"
        })
        Mockito.`when`(mdb.savePassword("password")).thenAnswer(Answer { invocation ->
            mdb.settingsMap["password"] = "password"
        })
        Mockito.`when`(mdb.changePassword("password")).thenReturn(true)
        //fr.getDbInfo(mdbref,mdb,mun,mnet,mset,mlet,mpet)
    }

    @Test
    fun changePassword() {
        Mockito.`when`(mdb.changePassword("password")).thenReturn(true)
        fr.changePassword(mpet)
        assertEquals(true, mdb.changePassword("password"))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getDbInfo() {

        /* Mockito.`when`(mdb.getSettingsInfo(mdbref,mun,mnet,mset,mlet,mpet)).thenAnswer(Answer { invocation ->
             mset.setText("surname")
             mpet.setText("password")
             mnet.setText("name")
             mlet.setText("login")
         })
        fr.getDbInfo(mdbref,mdb,mun,mnet,mset,mlet,mpet)*/
        Mockito.`when`(mdb.getSettingsInfo(mnet, mset, mlet, mpet))
            .thenAnswer(Answer { invocation ->
                mset.setText("surname")
                mpet.setText("password")
                mnet.setText("name")
                mlet.setText("login")
                mdb.settingsMap =
                    mutableMapOf("name" to "", "surname" to "", "login" to "", "password" to "")
                mdb.settingsMap["name"] = "name"
                mdb.settingsMap["surname"] = "surname"
                mdb.settingsMap["password"] = "password"
            })
        // fr.getDbInfo(mdb,mnet,mset,mlet,mpet)

        assertEquals("Степан", mnet.text.toString())
        assertEquals("Немченко", mset.text.toString())
        assertEquals("19-06-0245", mlet.text.toString())
        assertEquals("9000719091", mpet.text.toString())

    }

    @Test
    fun saveSettings() {

        //getDbInfo()

        //Log.d("saveSettingsTest",mdb.settingsMap.toString())
        mdb.settingsMap =
            mutableMapOf("name" to "", "surname" to "", "login" to "", "password" to "")
        assertEquals("", mdb.settingsMap["password"])
        assertEquals("", mdb.settingsMap["name"])
        assertEquals("", mdb.settingsMap["surname"])
        Mockito.`when`(mdb.saveName("name")).thenAnswer(Answer { invocation ->
            mdb.settingsMap["name"] = "name"
        })
        Mockito.`when`(mdb.saveSurname("surname")).thenAnswer(Answer { invocation ->
            mdb.settingsMap["surname"] = "surname"
        })
        Mockito.`when`(mdb.savePassword("password")).thenAnswer(Answer { invocation ->
            mdb.settingsMap["password"] = "password"
        })
        fr.saveSettings(mpet, mnet, mset)
    }
}