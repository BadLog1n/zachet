package com.example.universitysystem.espresso

import androidx.fragment.app.testing.*
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.universitysystem.ChatsFragment
import com.example.universitysystem.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IndividualActScrollTest {
    private var scenario: FragmentScenario<ChatsFragment>? = null
    private var rcView: RecyclerView? = null

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<ChatsFragment>()

        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    @Test
    fun ScrollTest() {
        onView(withText("Немченко Степан")).perform(click())
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.indChatLayout)).perform(
            ViewActions.closeSoftKeyboard()
        )

        onView(withId(R.id.messagesRcView)).perform(
            // листает вверх до сообщения в first text - второе сверху
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                scrollTo()
            )
        )


        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(R.id.messageEditText)).perform(click())
        onView(withId(R.id.indChatLayout)).perform(
            ViewActions.closeSoftKeyboard()
        )
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        // делаем еще раз. При правильном поведении ничего не должно измениться потому что мы не листаем.
        // делаем еще раз потому что "листание" происходит после выполнения листенера
        // и мы не можем в отладке увидеть значение позиции после скролла
        onView(withId(R.id.messageEditText)).perform(click())
        onView(withText("first text")).check(matches(isDisplayed()))
    }

}