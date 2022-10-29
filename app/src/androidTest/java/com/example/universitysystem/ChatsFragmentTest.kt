package com.example.universitysystem

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)

class ChatsAdapterTest {
    private var chA:ChatsAdapter? = null
    private var fragment: ChatsFragment? = null
    private var holder:ChatsAdapter.ChatsHolder?= null
    private var li:LayoutInflater?= null
    private var m:MainActivity?=null
    private var a: Application? =null
    @Before
    fun setUp() {
        a = Application()

        // m = MainActivity()
//        val intent = Intent(a?.applicationContext, MainActivity::class.java)
        //a!!.applicationContext.startActivity(intent)
        //a?.applicationContext?.mainLooper?.isCurrentThread
        fragment = ChatsFragment()
        chA = ChatsAdapter()
        chA?.chatsList?.add(ChatPreview("",18,"",true,""))
        //fragment!!.onStart()
        //fragment!!.onCreateView(li!!,fragment!!.view)
        /* val fragmentManager:FragmentManager = m!!.getSupportFragmentManager()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(fragment!!, null)
        fragmentTransaction.commit()*/
        /* fragment!!.view = View.inflate(fragment!!.context,R.layout.fragment_chats,FragmentContainerView(
             fragment!!.context!!
         ))*/
        //li= fragment!!.onGetLayoutInflater(null)
        /* fragment!!.onCreateView(li!!,FragmentContainerView(
             fragment!!.context!!
         ), null)*/
    }


    @After
    fun tearDown() {
        chA = null
        fragment = null
    }



    @Test
    fun onCreateViewHolder() {
        fragment = Mockito.mock(ChatsFragment::class.java)
        fragment!!.onCreateView(Mockito.mock(LayoutInflater::class.java),Mockito.mock(ViewGroup::class.java),Mockito.mock(Bundle::class.java))
        //var rView: RecyclerView? = fragment?.view?.findViewById(com.example.universitysystem.R.id.chatsRcView)
        //rView = RecyclerView(fragment?.context!!)
        //if (rView!=null){
        val rView = Mockito.mock(RecyclerView::class.java)
        if (rView != null) {
            rView.adapter = ChatsAdapter()
        }
        val vh = rView?.adapter?.onCreateViewHolder(rView,Mockito.anyInt())
        assertNotNull(vh)
       //}
    }

    /* @Test
     fun onBindViewHolder() {
         val rView: RecyclerView? = fragment?.view?.findViewById(com.example.universitysystem.R.id.chatsRcView)
         if (rView!=null ){
             chA?.onBindViewHolder(chA?.onCreateViewHolder(rView,0)!!,1)
             val item = chA?.chatsList?.single { it == ChatPreview("",18,"",true,"")}
             val index = chA?.chatsList?.indexOf(item)
             val vh = rView.findViewHolderForAdapterPosition(index!!)
             if (vh != null) {
                 assertEquals("",vh.itemView.findViewById<TextView>(com.example.universitysystem.R.id.receiverName).text)
                 assertEquals(18,vh.itemView.findViewById<TextView>(com.example.universitysystem.R.id.latestMsgTime_tv).text)
                 assertEquals("",vh.itemView.findViewById<TextView>(com.example.universitysystem.R.id.latestMsg_tv).text)
                 assertEquals(Typeface.DEFAULT_BOLD,vh.itemView.findViewById<TextView>(com.example.universitysystem.R.id.latestMsg_tv).typeface)
                 assertEquals("",vh.itemView.findViewById<TextView>(com.example.universitysystem.R.id.getUser).text)
             }

         }

    }*/
    /*@Test
    fun onBindViewHolder2() {


    }*/

    @Test
    fun getItemCount() {
        assertEquals(1, chA?.getItemCount())
    }

    @Test
    fun addChatPreview() {
        chA?.addChatPreview(ChatPreview("2",19,"",true,""))
        assertEquals(ChatPreview("2",19,"",true,""),chA?.chatsList?.last())
    }

    @Test
    fun chatChange() {
        val item:ChatPreview?
        if (chA!=null){
            item = chA?.chatsList?.single { it == ChatPreview("",18,"",true,"")}
            val index = chA?.chatsList?.indexOf(item)
            chA?.chatChange(item!!,ChatPreview("",18,"",false,""))
            assertEquals(ChatPreview("",18,"",false,""),chA!!.chatsList[index!!])
        }

    }

    @Test
    fun clearRecords() {
        chA?.clearRecords()
        assertEquals(0, chA?.chatsList?.size)
    }

    @Test
    fun removeObject() {
        chA?.removeObject(ChatPreview("",18,"",true,""))
        assertEquals(false,chA?.chatsList?.contains(ChatPreview("",18,"",true,"")))
    }
}