package com.example.mounatinsput

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class StopwatchFragment : Fragment(), SensorEventListener {

    private lateinit var chronometer: Chronometer
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var stepCountTextView: TextView

    private lateinit var viewModel: StopwatchViewModel

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true // Zachowuje stan fragmentu po zmianie orientacji
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
        stepCountTextView = view.findViewById(R.id.stepCountTextView)

        sensorManager =
            requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Check for the permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            requestPermissions(
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                PERMISSION_REQUEST_ACTIVITY_RECOGNITION
            )
        } else {
            // Permission is already granted, initialize the sensor
            initializeStepSensor()
        }

        viewModel = ViewModelProvider(this).get(StopwatchViewModel::class.java)

        startButton.setOnClickListener { startChronometer() }
        stopButton.setOnClickListener { stopChronometer() }
        resetButton.setOnClickListener { resetChronometer() }

        updateUI()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("chronometerBaseTime", viewModel.chronometerBaseTime)
        outState.putBoolean("isChronometerRunning", viewModel.isChronometerRunning)
        outState.putInt("stepCount", viewModel.stepCount)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            viewModel.isChronometerRunning = savedInstanceState.getBoolean("isChronometerRunning", false)
            viewModel.chronometerBaseTime = savedInstanceState.getLong("chronometerBaseTime", 0)
            val elapsedTime = SystemClock.elapsedRealtime() - viewModel.chronometerBaseTime
            chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
            if (viewModel.isChronometerRunning) {
                chronometer.start()
            }
            viewModel.stepCount = savedInstanceState.getInt("stepCount", 0)
            stepCountTextView.text = viewModel.stepCount.toString()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_ACTIVITY_RECOGNITION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, initialize the step sensor
                    initializeStepSensor()
                } else {
                    // Permission denied, handle accordingly
                    stepCountTextView.text = "Permission denied for activity recognition"
                }
            }
        }
    }

    private fun initializeStepSensor() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            stepCountTextView.text = "Step sensor not available"
        } else {
            // Register sensor listener
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        val elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
        chronometer.base = SystemClock.elapsedRealtime() + viewModel.chronometerBaseTime - elapsedTime
        if (viewModel.isChronometerRunning) {
            chronometer.start()
        } else {
            chronometer.stop()
        }
        stepCountTextView.text = viewModel.stepCount.toString()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == stepSensor) {
            event?.values?.get(0)?.toInt()?.let {
                stepCountTextView.text = it.toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun startChronometer() {
        if (!viewModel.isChronometerRunning) {
            val elapsedTimeSincePause = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.base = SystemClock.elapsedRealtime() - elapsedTimeSincePause
            chronometer.start()
            viewModel.isChronometerRunning = true
        }
    }

    private fun stopChronometer() {
        if (viewModel.isChronometerRunning) {
            chronometer.stop()
            viewModel.chronometerBaseTime = SystemClock.elapsedRealtime() - chronometer.base
            viewModel.isChronometerRunning = false
        }
    }

    private fun resetChronometer() {
        if (!viewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            viewModel.chronometerBaseTime = 0
            viewModel.stepCount = 0
            stepCountTextView.text = "0"
        } else {
            chronometer.base = SystemClock.elapsedRealtime()
            viewModel.chronometerBaseTime = 0
        }
    }

    private fun updateUI() {
        if (viewModel.isChronometerRunning) {
            chronometer.start()
        } else {
            chronometer.stop()
        }
    }
}
