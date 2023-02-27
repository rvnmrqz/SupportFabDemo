package com.example.supportfab.service

import com.example.supportfab.App
import com.example.supportfab.R

class DummySupportService {

    companion object {
        var instance: DummySupportService? = null
            private set
            get() {
                if (field == null) {
                    instance = DummySupportService()
                }
                return field
            }
    }

    private var onGoingEngagement: Engagement? = null

    fun hasEngagement(): Boolean {
        return onGoingEngagement != null
    }

    fun getEngagement(): Engagement? {
        return onGoingEngagement
    }

    fun startEngagement(supportType: SupportType) {
        when (supportType) {
            SupportType.VIDEO_CALL -> App.instance.updateFabIcon(R.drawable.ic_video)
            SupportType.VOICE_CALL -> App.instance.updateFabIcon(R.drawable.ic_call)
            SupportType.TEXT_CHAT -> App.instance.updateFabIcon(R.drawable.ic_chat)
        }

        onGoingEngagement = Engagement(supportType)

    }

    fun endEngagement() {
        App.instance.updateFabIcon(R.drawable.ic_question)
        onGoingEngagement = null
    }
}

enum class SupportType {
    VIDEO_CALL,
    VOICE_CALL,
    TEXT_CHAT
}

data class Engagement(
    val type: SupportType
)