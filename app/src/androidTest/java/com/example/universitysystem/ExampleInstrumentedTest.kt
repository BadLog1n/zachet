package com.example.universitysystem

import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.sun.org.slf4j.internal.LoggerFactory
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.universitysystem", appContext.packageName)
    }


    @Test
    fun isSdkBiggerThen23() {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= 21)
    }


    @Test
    fun isVersionActual() {
        Assume.assumeTrue(Build.VERSION.RELEASE_OR_CODENAME == "1.2")// 11
    }

    @Test
    fun textSubEmpty() {
        val textCheck = "Один      два      три"
        val textTrue = "Один два три"
        val textAfter = textCheck.replace("\\s+".toRegex(), " ")
        assertEquals(textTrue, textAfter)
    }


    @Test
    fun textSubstringAfter200() {
        val text =
            "vcxywzdsocomdkhoikenvzplzycqrafctzwjfyyskjmqdazciudhzelfykcrjucbeiiklbqwsdvkprjeigmhgnxphmxexeghjnkcheiofcrgylqjbncqhlfuhlsuxzxxkxzkvufmdcaobzreeekfoykkrlbvldedububycpujwnrchtfrkedhjewdeggoarajqvyxwmk200"
        assertEquals("200", text.substring(200))
    }

    @Test
    fun isTextBlankOrEmpty() {
        val text = ""
        val isBlankOrEmpty = text.isBlank() or text.isEmpty()
        assertEquals(true, isBlankOrEmpty)
    }

    @Test
    fun isExternalStorageDocument() {
        val uri: Uri = "com.android.externalstorage.documents/0".toUri()
        assertEquals("com.android.externalstorage.documents",uri.authority)
    }


    @Test
    fun isDownloadsDocument() {
        val uri: Uri = "content://com.android.providers.downloads.documents/0".toUri()
        assertEquals("com.android.providers.downloads.documents", uri.authority)
    }

    @Test
    fun isMediaDocument() {
        val uri: Uri = "content://com.android.providers.media.documents/0".toUri()
        assertEquals("com.android.providers.media.documents", uri.authority)
    }

    @Test
    fun isGooglePhotosUri() {
        val uri: Uri = "content://com.google.android.apps.photos.content/0".toUri()
        assertEquals("com.google.android.apps.photos.content", uri.authority)
    }

}


class TestFirebase : AndroidTestCase() {
    private var authSignal: CountDownLatch? = null
    private var auth: FirebaseAuth? = null
    @Throws(InterruptedException::class)
    fun setUp() {
        authSignal = CountDownLatch(1)
        setAndroidContext(mContext) //initializeFireBase(context);
        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser == null) {
            auth!!.signInWithEmailAndPassword("urbi@orbi.it", "12345678")
                .addOnCompleteListener { task ->
                    val result = task.result
                    val user = result.user
                    authSignal.countDown()
                }
        } else {
            authSignal.countDown()
        }
        authSignal.await(10, TimeUnit.SECONDS)
    }

    @Throws(Exception::class)
    fun tearDown() {
        super.tearDown()
        if (auth != null) {
            auth!!.signOut()
            auth = null
        }
    }

    @Test
    @Throws(InterruptedException::class)
    fun testWrite() {
        val writeSignal = CountDownLatch(1)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        myRef.setValue("Do you have data? You'll love Firebase. - 3")
            .addOnCompleteListener { writeSignal.countDown() }
        writeSignal.await(10, TimeUnit.SECONDS)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TestFirebase::class.java)
    }
}

/*

    private fun sendMessage(text: String) {
        var textSub = ""
        val lenghtTooBig = text.length > 200
        textSub = if (lenghtTooBig) {
            text.substring(0, 200)
        } else text
        chatsPackage.sendMessage(
            sendName,
            getName,
            textSub.replace("\\s+".toRegex(), " "),
            "text",
            chatsPackage.getChatName(sendName, getName)
        )
        if (lenghtTooBig) {
            sendMessage(text.substring(200))
        }
    }
*/





