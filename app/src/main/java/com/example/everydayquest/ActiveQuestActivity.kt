package com.example.everydayquest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.sqrt

class ActiveQuestActivity : AppCompatActivity(),
    LocationListener,
    SensorEventListener {

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    private lateinit var progressText: TextView
    private lateinit var questProgressView: QuestProgressView
    private lateinit var finishButton: Button

    private var questTitle = ""
    private var questXp = 0
    private var questType = ""
    private var questTarget = 0

    private var questCompleted = false

    // GPS
    private var previousLocation: Location? = null
    private var totalDistance = 0f

    // Accelerometer
    private var movementDetected = false
    private var movementSeconds = 0

    private val handler =
        Handler(Looper.getMainLooper())

    private val movementTimer =
        object : Runnable {

            override fun run() {

                if (questType != "MOVE") {
                    return
                }

                if (movementDetected) {

                    movementSeconds++
                    movementDetected = false

                    progressText.text =
                        "Moving...\nTime: $movementSeconds / $questTarget seconds"

                    questProgressView.setProgress(
                        movementSeconds.toFloat() /
                                questTarget.toFloat()
                    )

                    if (movementSeconds >= questTarget) {

                        completeQuest()

                        progressText.text =
                            "Quest Complete!\n$questTarget seconds reached."

                        questProgressView.setProgress(
                            1f
                        )

                        finishButton.text =
                            "Back to Home"

                        stopSensors()

                        return
                    }

                } else {

                    progressText.text =
                        "Move your phone/body\nTime: $movementSeconds / $questTarget seconds"
                }

                handler.postDelayed(
                    this,
                    1000
                )
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_active_quest
        )

        questTitle =
            intent.getStringExtra(
                "QUEST_TITLE"
            ) ?: "Quest"

        questXp =
            intent.getIntExtra(
                "QUEST_XP",
                0
            )

        questType =
            intent.getStringExtra(
                "QUEST_TYPE"
            ) ?: ""

        questTarget =
            intent.getIntExtra(
                "QUEST_TARGET",
                0
            )

        progressText =
            findViewById(
                R.id.progressText
            )

        questProgressView =
            findViewById(
                R.id.questProgressView
            )

        val questTitleText =
            findViewById<TextView>(
                R.id.activeQuestTitle
            )

        finishButton =
            findViewById(
                R.id.finishQuestButton
            )

        questTitleText.text =
            questTitle

        locationManager =
            getSystemService(
                LOCATION_SERVICE
            ) as LocationManager

        sensorManager =
            getSystemService(
                SENSOR_SERVICE
            ) as SensorManager

        accelerometer =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            )!!

        questProgressView.setProgress(
            0f
        )

        finishButton.setOnClickListener {

            stopSensors()

            finish()
        }

        if (questType == "MOVE") {

            startMovementQuest()

        } else {

            startLocationQuest()
        }
    }

    // =========================
    // GPS
    // =========================

    private fun startLocationQuest() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            progressText.text =
                "Please grant location permission."

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )

            return
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {

        val gpsEnabled =
            locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        val networkEnabled =
            locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )

        if (!gpsEnabled && !networkEnabled) {

            progressText.text =
                "Location is disabled.\nPlease enable GPS."

            return
        }

        progressText.text =
            "Getting your location...\nDistance: 0 m"

        try {

            if (gpsEnabled) {

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    500L,
                    0f,
                    this
                )
            }

            if (networkEnabled) {

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    500L,
                    0f,
                    this
                )
            }

        } catch (e: SecurityException) {

            progressText.text =
                "Location permission required."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                progressText.text =
                    "Permission granted!\nGetting location..."

                startLocationUpdates()

            } else {

                progressText.text =
                    "Location permission denied."
            }
        }
    }

    override fun onLocationChanged(
        location: Location
    ) {


        if (previousLocation == null) {

            previousLocation =
                Location(location)

            progressText.text =
                "GPS connected!\nDistance: 0 m"

            return
        }


        val movement =
            previousLocation!!.distanceTo(
                location
            )


        if (movement >= 1f) {

            totalDistance += movement
        }


        previousLocation =
            Location(location)

        val distance =
            totalDistance.toInt()

        progressText.text =
            "Distance: $distance m"

        if (
            questType == "WALK" ||
            questType == "EXPLORE"
        ) {

            questProgressView.setProgress(
                totalDistance /
                        questTarget.toFloat()
            )

            if (
                totalDistance >=
                questTarget
            ) {

                completeQuest()

                progressText.text =
                    "Quest Complete!\n$questTarget m reached!"

                questProgressView.setProgress(
                    1f
                )

                finishButton.text =
                    "Back to Home"

                stopSensors()
            }
        }
    }


    // ACCELEROMETER


    private fun startMovementQuest() {

        progressText.text =
            "Move your phone/body\nTime: 0 / $questTarget seconds"

        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        handler.postDelayed(
            movementTimer,
            1000
        )
    }

    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        if (event == null) {
            return
        }

        val x =
            event.values[0]

        val y =
            event.values[1]

        val z =
            event.values[2]

        val acceleration =
            sqrt(
                x * x +
                        y * y +
                        z * z
            )

        if (acceleration > 12) {

            movementDetected = true
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }


    // COMPLETE QUEST


    private fun completeQuest() {

        if (questCompleted) {
            return
        }

        questCompleted =
            true

        QuestStorage.addCompletedQuest(
            this,
            questTitle,
            questXp
        )

        val resultIntent =
            Intent()

        resultIntent.putExtra(
            "QUEST_COMPLETED",
            true
        )

        setResult(
            RESULT_OK,
            resultIntent
        )
    }


    // CLEANUP


    private fun stopSensors() {

        locationManager.removeUpdates(
            this
        )

        sensorManager.unregisterListener(
            this
        )

        handler.removeCallbacks(
            movementTimer
        )
    }

    override fun onDestroy() {

        stopSensors()

        super.onDestroy()
    }

    override fun onProviderEnabled(
        provider: String
    ) {
    }

    override fun onProviderDisabled(
        provider: String
    ) {
    }

    override fun onStatusChanged(
        provider: String?,
        status: Int,
        extras: Bundle?
    ) {
    }
}