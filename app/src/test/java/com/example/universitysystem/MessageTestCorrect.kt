package com.example.universitysystem

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.stubbing.Answer

@RunWith(AndroidJUnit4::class)
class MessageTestCorrect {

    private val mdb = Message()

    @Test
    fun sendMessage() {

        val result = mdb.sendMessage("1", "1", "1", "text")
        Thread.sleep(3000)

        assertEquals(true,result)
    }

    @Test
    fun getMessage() {
        val arrayCorrectionInfo = mutableMapOf("text" to "1", "type" to "2", "username" to "3")

        val arrayMockito = mutableMapOf("text" to "", "type" to "", "username" to "", "username" to "")
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