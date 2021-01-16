package com.homework.ninedt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.homework.ninedt.ui.main.view.BoardFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, BoardFragment.newInstance())
                    .commitNow()
        }
    }
}