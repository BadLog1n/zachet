package com.example.universitysystem.espresso

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.universitysystem.ChatPreview
import com.example.universitysystem.ChatsAdapter
import com.example.universitysystem.ChatsFragment
import com.example.universitysystem.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//import org.robolectric.RobolectricTestRunner

@RunWith(AndroidJUnit4::class)
//@RunWith(RobolectricTestRunner::class)
@LargeTest
class ChatsAdapterViewTest {

    private var chA: ChatsAdapter? = null
    private var fragment: ChatsFragment? = null
    private val mMainActivity: MainActivity? = null
    private val mRecyclerView: RecyclerView? = null
    private var holder: ChatsAdapter.ChatsHolder? = null
    //@get:Rule
    //val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // @get:Rule
    //var activityRule = ActivityTestRule(
    //    MainActivity::class.java
    //)

    @Before
    fun setUp() {
        // chA = ChatsAdapter()
        //chA?.chatsList?.add(ChatPreview("", 18, "", true, ""))
        /*val scenario = launchFragmentInContainer<ChatsFragment>()
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        scenario.withFragment {
            val rView = this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!
            for (rView.)
            rcAdapter.removeObject(item)
            list.remove(item)
            list.add(chat)
            rcAdapter.addChatPreview(chat)
            //попробовать добавить один элемент в тот же адаптер
        }*/

    }
    @Test
    fun list() {
        // onView(withId(com.example.universitysystem.R.id.nav_host_fragment)).perform(na))

        //onView(withId(com.example.universitysystem.R.id.chatsFragment))
        //  .check(matches(withItemCount(100)))
        /* fun startVoiceFragment(): ChatsFragment? {
                val activity: MainActivity = activityRule.getActivity()
                val transaction: FragmentTransaction =
                    activity.getSupportFragmentManager().beginTransaction()
                val voiceFragment = ChatsFragment()
                transaction.add(voiceFragment, "chatsFragment")
                transaction.commit()
                return voiceFragment
            }*/

        /*activityRule.activity.runOnUiThread {
            val activity: MainActivity = activityRule.getActivity()
            activity.findNavController(com.example.universitysystem.R.id.nav_host_fragment)
                .navigate(
                    com.example.universitysystem.R.id.chatsFragment
                )
           fragment = activity.supportFragmentManager.findFragmentById( com.example.universitysystem.R.id.chatsFragment) as ChatsFragment?
            var rView: RecyclerView? = fragment?.view?.findViewById(com.example.universitysystem.R.id.chatsRcView)
             chA= ChatsAdapter()
            chA?.addChatPreview(ChatPreview("2",19,"",true,""))
            rView?.adapter = chA*/
        val scenario = launchFragmentInContainer<ChatsFragment>()
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        chA= ChatsAdapter()
        chA!!.clearRecords()
        chA?.chatsList = ArrayList()
        chA!!.notifyDataSetChanged()
        chA?.addChatPreview(ChatPreview("2",19,"",true,""))
        chA!!.notifyDataSetChanged()

        scenario.withFragment {
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
            linearLayoutManager.stackFromEnd = true
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.swapAdapter(chA,true)
            val rView = this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!
            //попробовать добавить один элемент в тот же адаптер
            rView.layoutManager = linearLayoutManager

            rView.adapter = chA
            rView.adapter?.notifyDataSetChanged()
            rView.layoutManager = linearLayoutManager
            rView.adapter?.notifyDataSetChanged()
            this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.swapAdapter(chA,true)
            rView.swapAdapter(chA,true)
            rView.adapter?.notifyDataSetChanged()
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.adapter = chA
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.layoutManager = linearLayoutManager
            try {
                Thread.sleep(4000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            onView(withId(com.example.universitysystem.R.id.chatsRcView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1,
                        ViewActions.click()
                    )
                )

            val itemElementText = "2"

            onView(withText(itemElementText)).check(ViewAssertions.matches(isDisplayed()))
        }
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        onView(withId(com.example.universitysystem.R.id.chatsRcView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )



        //onView(withId(com.example.universitysystem.R.id.chatsRcView)).perform()

        // Then use Espresso to test the Fragment
        /*onView(withId(com.example.universitysystem.R.id.chatsRcView))
            //matches(
                //withItemCount(100)
            //withChild()
            withText("middleElementText")
        .check(
        withText("middleElementText").
            matches(isDisplayed())
            )
        )*/

        // First scroll to the position that needs to be matched and click on it.


        // Match the text in an item below the fold and check that it's displayed.

        //onData()

    }



    /*@Test
    fun onCreateViewHolder() {
        activityRule.activity.runOnUiThread {
            val activity: MainActivity = activityRule.getActivity()
            activity.findNavController(com.example.universitysystem.R.id.nav_host_fragment)
                .navigate(
                    com.example.universitysystem.R.id.chatsFragment
                )
            var rView: RecyclerView? =
                fragment?.view?.findViewById(com.example.universitysystem.R.id.chatsRcView)
            //onView(withId(com.example.universitysystem.R.id.chatsRcView))
              //  .perform(Assert.assertNotNull(chA?.onCreateViewHolder(rView, 0)))
            rView = RecyclerView(fragment?.context!!)
            if (rView != null) {
                Assert.assertNotNull(chA?.onCreateViewHolder(rView, 0))
            }
        }
    }*/
    @After
    fun tearDown() {
        chA = null
        fragment = null
    }

    class CustomMatchers {
        companion object {
            fun withItemCount(count: Int): Matcher<View> {
                return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                    override fun describeTo(description: Description?) {
                        description?.appendText("RecyclerView with item count: $count")
                    }

                    override fun matchesSafely(item: RecyclerView?): Boolean {
                        //item?.adapter?.onCreateViewHolder()
                        item?.adapter?.onBindViewHolder(item.adapter?.createViewHolder(item,0)!!,2)
                        // onView()
                        return item?.adapter?.itemCount == count
                    }
                }
            }
        }
    }
}