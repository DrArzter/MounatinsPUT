package com.example.mounatinsput

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView


class StepCounter(
    private val sensorManager: SensorManager,
    private val stepCountTextView: TextView // Передаем TextView в конструктор
) : SensorEventListener {

    private var steps: Int = 0

    private var stepSensor: Sensor? = null

    init {
        // Получаем сенсор шагов
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // Регистрируем слушателя событий
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Увеличиваем счетчик шагов при обнаружении шага
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            steps++
            updateStepCounter()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не используется
    }

    fun resetSteps() {
        steps = 0
        updateStepCounter()
    }

    fun getSteps(): Int {
        return steps
    }

    private fun updateStepCounter() {
        stepCountTextView.text = "Steps: $steps"
    }
}

