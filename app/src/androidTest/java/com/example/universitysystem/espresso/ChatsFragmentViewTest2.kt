package com.example.universitysystem.espresso

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.universitysystem.ChatsAdapter
import com.example.universitysystem.ChatsFragment
import com.example.universitysystem.MainActivity
import com.example.universitysystem.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ChatsAdapterViewTest2 {

    private var chA: ChatsAdapter? = null
    private var mFragment: ChatsFragment? = null
    private val mMainActivity: MainActivity? = null
    private val mRecyclerView: RecyclerView? = null
    private var holder: ChatsAdapter.ChatsHolder? = null
    private var scenario:FragmentScenario<ChatsFragment>? = null
    //@get:Rule
    //val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // @get:Rule
    //var activityRule = ActivityTestRule(
    //    MainActivity::class.java
    //)

    @Before
    fun setUp() {


           /* // chA = ChatsAdapter()
            //chA?.chatsList?.add(ChatPreview("", 18, "", true, ""))
            mFragment = mock(ChatsFragment::class.java)
            //var view: View = mock(View::class.java)
            //var view = View(mFragment!!.context)
            var bundle: Bundle = mock(Bundle::class.java)
            //`when`(mFragment).getMock<ChatsFragment>()
            `when`(mFragment!!.onViewCreated(any(), any())).thenAnswer(Answer {
                val linearLayoutManager =
                    LinearLayoutManager(mFragment!!.context, LinearLayoutManager.VERTICAL, true)
                linearLayoutManager.stackFromEnd = true
                val recyclerView: RecyclerView =
                    mFragment!!.view?.findViewById(com.example.universitysystem.R.id.chatsRcView)!!

                recyclerView.layoutManager = linearLayoutManager
                val rcAdapter = ChatsAdapter()
                rcAdapter.clearRecords()
                rcAdapter.chatsList = ArrayList()
                rcAdapter.addChatPreview(ChatPreview("2", 19, "", true, ""))
                rcAdapter.notifyDataSetChanged()
                recyclerView.adapter = rcAdapter
                recyclerView.layoutManager = linearLayoutManager
            }) */
        scenario = launchFragmentInContainer<ChatsFragment>()
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        scenario!!.withFragment {
            val linearLayoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
            linearLayoutManager.stackFromEnd = true
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.swapAdapter(chA,true)
            val rView =
                this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!
            //попробовать добавить один элемент в тот же адаптер
            rView.layoutManager = linearLayoutManager
            /*var mRc= mock(RecyclerView::class.java)
            mRc.*/
            var rAd = rView.adapter
            rAd = ChatsAdapter()
            //`when`(rAd.add)
            //val mAdapter = mock(ChatsAdapter::class.java)
            /*`when`(mAdapter.addChatPreview(ChatPreview(
                anyString(), anyLong(), anyString(), anyBoolean(),
                anyString()))).then(
               //mAdapter.chatsList.add(ChatPreview("2", 19, "", true, ""))
                //mAdapter.chatsList.add(ChatPreview("2",19,"",true,""))
               //Answer { mAdapter.chatsList = arrayListOf(ChatPreview("2",19,"",true,"")) }
            Answer(doNothing())
            )*/
           // rView.adapter = mAdapter
            rView.adapter?.notifyDataSetChanged()
            rView.layoutManager = linearLayoutManager
            rView.adapter?.notifyDataSetChanged()!!
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.swapAdapter(chA,true)
            //rView.swapAdapter(chA,true)
            //rView.adapter?.notifyDataSetChanged()
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.adapter = chA
            //this.view?.findViewById<RecyclerView>(com.example.universitysystem.R.id.chatsRcView)!!.layoutManager = linearLayoutManager
            /* try {
                Thread.sleep(4000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Espresso.onView(ViewMatchers.withId(R.id.chatsRcView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1,
                        ViewActions.click()
                    )
                )

            val itemElementText = "2"

            Espresso.onView(ViewMatchers.withText(itemElementText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }*/
        }
    }
    @Test
    fun list() {
        scenario?.withFragment {
            Espresso.onView(ViewMatchers.withId(R.id.chatsRcView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        1,
                        ViewActions.click()
                    )
                )

            val itemElementText = "2"

            Espresso.onView(ViewMatchers.withText(itemElementText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
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
        mFragment = null
    }


}