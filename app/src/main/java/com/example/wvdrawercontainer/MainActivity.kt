package com.example.wvdrawercontainer

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.iwakeup.drawer.WvDrawer
import com.example.wvdrawercontainer.demo.list.VerticalAdapter

class MainActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.app)

        val drawer = findViewById<WvDrawer>(R.id.drawer)
        drawer.setupDrawer(getDrawerContent(), getMainContent())


    }


    fun getDrawerContent(): View {

        return LayoutInflater.from(this).inflate(R.layout.layout_drawer_content, null).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }


    fun getMainContent(): View {

        val mainContentView = LayoutInflater.from(this).inflate(R.layout.layout_main_content, null).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val data = List(10) { row ->

            List(20) { column ->
                "R${row + 1} - ${column + 1}"
            }
        }
        val list = mainContentView.findViewById<RecyclerView>(R.id.list).apply {
            adapter = VerticalAdapter(data)
            layoutManager = LinearLayoutManager(context)
        }

        return mainContentView
    }
}



