package com.example.universitysystem

import androidx.test.runner.AndroidJUnit4
import com.google.firebase.database.DatabaseReference
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.stubbing.Answer

@RunWith(AndroidJUnit4::class)
class MessageTest {
    private lateinit var mdb: Message
    private lateinit var mdbref: DatabaseReference

    @Before
    fun setUp() {
        mdb = Mockito.mock(Message::class.java)
        mdbref = Mockito.mock(DatabaseReference::class.java)
        mdb.dbref = mdbref

    }

    @Test
    fun sendMessage() {
        Mockito.`when`(mdb.sendMessage("1", "1", "1", "text")).thenReturn(true)
        assertEquals(true, mdb.sendMessage("1", "1", "1", "text"))
    }

    @Test
    fun getMessage() {
        val arrayCorrectionInfo = mutableMapOf("text" to "1", "type" to "2", "username" to "3")

        val arrayMockito =
            mutableMapOf("text" to "", "type" to "", "username" to "", "username" to "")
        Mockito.`when`(mdb.getMessage("1", "1", "1"))
            .thenAnswer(Answer { invocation ->
                arrayMockito["text"] = "1"
                arrayMockito["type"] = "2"
                arrayMockito["username"] = "3"
            })

        mdb.getMessage("1", "1", "1")
        assertEquals(arrayCorrectionInfo["text"], arrayMockito["text"])
        assertEquals(arrayCorrectionInfo["type"], arrayMockito["type"])
        assertEquals(arrayCorrectionInfo["username"], arrayMockito["username"])


    }

}