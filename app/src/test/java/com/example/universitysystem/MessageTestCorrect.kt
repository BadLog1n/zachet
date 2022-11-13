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

        val result = mdb.sendMessage("1", "text", "1", "text")
        Thread.sleep(3000)

        assertEquals(true,result)
    }



}