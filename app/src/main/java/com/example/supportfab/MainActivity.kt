package com.example.supportfab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewGroup = findViewById<ConstraintLayout>(R.id.main_container)
        val fab = findViewById<DraggableFloatingActionButton>(R.id.fab)

        //constructor supports activity/fragment, viewGroup or fab
//      ChatSupportButton(this)
//      ChatSupportButton(viewGroup)z
        ChatSupportButton(this)
            .show()
    }
}