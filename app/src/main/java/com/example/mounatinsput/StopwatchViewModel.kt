package com.example.mounatinsput
import androidx.lifecycle.ViewModel

class StopwatchViewModel : ViewModel() {
    var chronometerBaseTime: Long = 0
    var isChronometerRunning: Boolean = false
}
