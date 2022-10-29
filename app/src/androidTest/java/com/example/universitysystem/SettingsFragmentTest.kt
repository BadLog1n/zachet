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
    var fr: SettingsFragment = SettingsFragment()
    private lateinit var mnet:EditText
    private lateinit var mset:EditText
    private lateinit var mlet:EditText
    private lateinit var mpet:EditText
    private lateinit var mdb:MyDatabase
    private lateinit var mun:String
    private var scenario: FragmentScenario<SettingsFragment>? = null
    private lateinit var mdbref:DatabaseReference
    @Before
    fun setUp() {


        mun = "someUn"
        //val marr:Array<String?> = arrayOf("name","surname","login","password")
        //mnet = EditText(Mockito.mock(Context::class.java))
       // mset = EditText(Mockito.mock(Context::class.java))
        //mlet = EditText(Mockito.mock(Context::class.java))
       // mpet = EditText(Mockito.mock(Context::class.java))
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
        }
        /*mnet = Mockito.mock(EditText::class.java)
        mnet.text = Mockito.mock(Editable::class.java)
        mnet.setText("")
        mset= Mockito.mock(EditText::class.java)
        mlet= Mockito.mock(EditText::class.java)
        mpet= Mockito.mock(EditText::class.java)*/
        mdb = Mockito.mock(MyDatabase::class.java)
       mdbref = Mockito.mock(DatabaseReference::class.java)
        Mockito.`when`(mdb.getSettingsInfo(mdbref,mun,mnet,mset,mlet,mpet)).thenAnswer(Answer { invocation ->
        /*Mockito.`when`(mdb.getSettingsInfo(mun,mnet,mset,mlet,mpet)).thenAnswer(Answer { invocation ->
            invocation.getArgument<EditText>(1).setText("name")
            invocation.getArgument<EditText>(2).setText("surname")
            invocation.getArgument<EditText>(3).setText("login")
            invocation.getArgument<EditText>(4).setText("password")*/
            mset.setText("surname")
            mpet.setText("password")
            mnet.setText("name")
            mlet.setText("login")
        })
            /*.thenAnswer(it ->
            mset.setText("surname")
            mpet.setText("password")
            mnet.setText("name")
            mlet.setText("login")
        ))*/
       fr.getDbInfo(mdbref,mdb,mun,mnet,mset,mlet,mpet)
       // fr.getDbInfo(mdb,mun,mnet,mset,mlet,mpet)
    }
    @Test
    fun changePassword(){
        fr.changePassword()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getDbInfo() {
        /*val mdb = Mockito.mock(MyDatabase::class.java)
        val mun:String = "someUn"
        //val marr:Array<String?> = arrayOf("name","surname","login","password")
        val mnet:EditText = Mockito.mock(EditText::class.java)
        val mset:EditText = Mockito.mock(EditText::class.java)
        val mlet:EditText = Mockito.mock(EditText::class.java)
        val mpet:EditText = Mockito.mock(EditText::class.java)
        Mockito.`when`(mdb.getSettingsInfo(mun,mnet,mset,mlet,mpet)).then {
            mset.setText("surname")
            mpet.setText("password")
            mnet.setText("name")
            mlet.setText("login")
        }
        fr.getDbInfo(mdb,mun,mnet,mset,mlet,mpet)*/
       /* mset.setText("surname")
        mpet.setText("password")
        mnet.setText("name")
        mlet.setText("login")*/
        /*Mockito.`when`(mdb.getSettingsInfo(mun,mnet,mset,mlet,mpet)).then {
            mset.setText("surname")
            mpet.setText("password")
            mnet.setText("name")
            mlet.setText("login")
        }
        fr.getDbInfo(mdb,mun,mnet,mset,mlet,mpet)*/
        Mockito.`when`(mdb.getSettingsInfo(mdbref,mun,mnet,mset,mlet,mpet)).thenAnswer(Answer { invocation ->
        //Mockito.`when`(mdb.getSettingsInfo(mun,mnet,mset,mlet,mpet)).thenAnswer(Answer { invocation ->
            /*invocation.getArgument<EditText>(1).setText("name")
            invocation.getArgument<EditText>(2).setText("surname")
            invocation.getArgument<EditText>(3).setText("login")
            invocation.getArgument<EditText>(4).setText("password")*/
            mset.setText("surname")
            mpet.setText("password")
            mnet.setText("name")
            mlet.setText("login")
        })
       fr.getDbInfo(mdbref,mdb,mun,mnet,mset,mlet,mpet)
       // fr.getDbInfo(mdb,mun,mnet,mset,mlet,mpet)
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assertEquals("name",mnet.text.toString())
        assertEquals("surname",mset.text.toString())
        assertEquals("login",mlet.text.toString())
        assertEquals("password",mpet.text.toString())
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun saveSettings() {
    }
}