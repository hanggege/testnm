package com.mei.wood

import android.os.Bundle
import android.view.View
import com.mei.wood.ui.MeiCustomBarActivity
import kotlinx.coroutines.*
import kotlin.coroutines.resume

/**
 *
 * @author Created by lenna on 2020/9/22
 */
class TestFragmentActivity : MeiCustomBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_fragment_activity)

//        startActivityForResult()

        val scope = MainScope()

        val job1 = scope.launch {  }
        val job2 = scope.launch {  }




    }
}