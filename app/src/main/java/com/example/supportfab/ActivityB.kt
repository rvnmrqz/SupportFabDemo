package com.example.supportfab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.supportfab.custom_fab.ChatSupportButton

class ActivityB : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)

        ChatSupportButton(this)
            .listenToGlobalProperty(lifecycleScope)
            .show()

    }
}