package com.example.mounatinsput

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

private lateinit var stepCounter: StepCounter

class StopwatchFragment : Fragment(), SensorEventListener {

    private lateinit var chronometer: Chronometer
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var stepCountTextView: TextView

    private lateinit var viewModel: StopwatchViewModel

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true // Retain instance state on orientation change
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stopwatch, container, false)

        chronometer = view.findViewById(R.id.chronometer)
        startButton = view.findViewById(R.id.startButton)
        stopButton = view.findViewById(R.id.stopButton)
        resetButton = view.findViewById(R.id.resetButton)
        stepCountTextView = view.findViewById(R.id.stepCounterTextView)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepCounter = StepCounter(sensorManager, stepCountTextView)

        viewModel = ViewModelProvider(this).get(StopwatchViewModel::class.java)

        startButton.setOnClickListener { startChronometer() }
        stopButton.setOnClickListener { stopChronometer() }
        resetButton.setOnClickListener { resetChronometer() }

        updateUI()

        return view
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Реализация метода onSensorChanged

        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            stepCounter.onSensorChanged(event)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("chronometerBaseTime", viewModel.chronometerBaseTime)
        outState.putBoolean("isChronometerRunning", viewModel.isChronometerRunning)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            viewModel.isChronometerRunning = savedInstanceState.getBoolean("isChronometerRunning", false)
            viewModel.chronometerBaseTime = savedInstanceState.getLong("chronometerBaseTime", 0)

            chronometer.base = viewModel.chronometerBaseTime
            if (viewModel.isChronometerRunning) {
                chronometer.start()
            } else {
                chronometer.stop()
            }
        } else {
            // Initialize the chronometer to zero on the first run
            resetChronometer()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        if (viewModel.isChronometerRunning) {
            viewModel.chronometerBaseTime = SystemClock.elapsedRealtime() - chronometer.base
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (viewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - viewModel.chronometerBaseTime
            chronometer.start()
        } else {
            chronometer.base = SystemClock.elapsedRealtime() - viewModel.chronometerBaseTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun startChronometer() {
        if (!viewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - viewModel.chronometerBaseTime
            chronometer.start()
            viewModel.isChronometerRunning = true
        }
    }

    private fun stopChronometer() {
        if (viewModel.isChronometerRunning) {
            viewModel.chronometerBaseTime = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            viewModel.isChronometerRunning = false
        }
    }

    private fun resetChronometer() {
        viewModel.chronometerBaseTime = 0
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
        viewModel.isChronometerRunning = false

        stepCounter.resetSteps()
        updateStepCounter()
    }

    private fun updateStepCounter() {
        stepCountTextView.text = "Steps: ${stepCounter.getSteps()}"
    }

    private fun updateUI() {
        if (viewModel.isChronometerRunning) {
            chronometer.start()
        } else {
            chronometer.stop()
        }
    }
}
