package com.stepcountmodule

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.facebook.react.bridge.*
import java.util.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class StepCountModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var totalSteps = 0f
    private var previousSteps = 0f

    init {
        sensorManager = reactContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun getName(): String {
        return "StepCountModule"
    }

    @ReactMethod
    fun isStepCountingAvailable(promise: Promise) {
        if (stepCounterSensor != null) {
            promise.resolve(true)
        } else {
            promise.resolve(false)
        }
    }

    @ReactMethod
    fun getStepCount(promise: Promise) {
        if (stepCounterSensor == null) {
            promise.reject("SENSOR_NOT_AVAILABLE", "Step counter sensor is not available on this device.")
            return
        }
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        sensorManager?.let {
            val currentSteps = totalSteps - previousSteps
            promise.resolve(mapOf("steps" to currentSteps.toInt()))
        } ?: run {
            promise.reject("SENSOR_ERROR", "Failed to fetch step count.")
        }
    }

    @ReactMethod
    fun startStepUpdates(promise: Promise) {
        if (stepCounterSensor == null) {
            promise.reject("SENSOR_NOT_AVAILABLE", "Step counter sensor is not available on this device.")
            return
        }

        // 이미 리스너가 등록되어 있으면 등록하지 않음
        if (sensorManager != null && stepCounterSensor != null) {
            sensorManager?.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            promise.resolve(mapOf("steps" to totalSteps.toInt()))
        } else {
            promise.reject("SENSOR_ERROR", "Failed to register sensor listener.")
        }
    }

    @ReactMethod
    fun stopStepUpdates() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (previousSteps == 0f) {
                previousSteps = event.values[0]
            }
            totalSteps = event.values[0] - previousSteps
            sendStepCountToJS(totalSteps.toInt())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 시 처리 (필요 시 구현)
    }

    private fun sendStepCountToJS(stepCount: Int) {
        val reactContext = reactApplicationContext
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("StepCountUpdated", stepCount)
    }
}

