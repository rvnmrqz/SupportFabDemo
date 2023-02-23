package com.example.supportfab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.supportfab.custom_fab.ChatSupportButton
import com.example.supportfab.custom_fab.DraggableFloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewGroup = findViewById<ConstraintLayout>(R.id.main_container)
        val fab = findViewById<DraggableFloatingActionButton>(R.id.fab)

        //constructor supports activity/fragment, viewGroup or fab
//      ChatSupportButton(viewGroup)
//      ChatSupportButton(fab)
        ChatSupportButton(this)
            .lifeCycleOwner(this)
            .show()


        findViewById<Button>(R.id.btnGoActivityB).setOnClickListener {
            startActivity(Intent(this, ActivityB::class.java))
        }
    }
}