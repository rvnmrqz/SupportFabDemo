package com.example.supportfab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.supportfab.service.DummySupportService

class EngagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engagement)
        supportActionBar?.hide()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.btnEnd).setOnClickListener {
            DummySupportService.instance?.endEngagement()
            finish()
        }
    }
}