package com.example.supportfab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.supportfab.custom_fab.ChatSupportButton
import com.example.supportfab.custom_fab.DraggableFloatingActionButton
import com.example.supportfab.service.DummySupportService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //constructor supports activity/fragment, viewGroup or fab
//      ChatSupportButton(viewGroup)
//      ChatSupportButton(fab)

        ChatSupportButton(this)
            .listenToGlobalProperty(lifecycleScope)
            .show()

        findViewById<Button>(R.id.btnGoActivityB).setOnClickListener {
            startActivity(Intent(this, ActivityB::class.java))
        }
    }
}