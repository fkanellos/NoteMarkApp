package gr.pkcoding.notemarkapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteMarkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Hilt initialization happens automatically
    }
}