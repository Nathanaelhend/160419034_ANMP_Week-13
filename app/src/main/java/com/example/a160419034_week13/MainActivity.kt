package com.example.a160419034_week13

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometerReadings = FloatArray(3)
    private var accelometerSensor: Sensor? = null

    //Untuk menghitung langkah
    private var previousMagnitude: Float? = null
    private var stepCount = 0

    //Untuk Sensor gyroscope
    private var magneticReading = FloatArray(3)
    private var geomagneticSensor: Sensor? = null

    //Untuk Sensor cahaya
    private var lightSensor: Sensor? = null
    private var lightReading = 0f

    //Untuk sensor proximity
    private var proximitySensor: Sensor? = null
    private  var proximityReading = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inisialisasi sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //Dapatkan sensor accelometer (null = tidak ada)
        accelometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        geomagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    override fun onResume() {
        super.onResume()
        //Cek apakah ada sensor accelometer
        if(accelometerSensor == null)
        {
            Toast.makeText(this, "No accelometer Sensor detected.", Toast.LENGTH_SHORT).show()
            return
        }
        //Kalau ada, daftarkan listener-nya
        Toast.makeText(this, "Accelometer Sensor detected", Toast.LENGTH_SHORT).show()
        sensorManager.registerListener(this, accelometerSensor, SensorManager.SENSOR_DELAY_FASTEST)

        //Cek apakah ada sensor geomagnetic
        if(geomagneticSensor == null)
        {
            Toast.makeText(this, "No Geomagnetic Sensor detected.", Toast.LENGTH_SHORT).show()
        }
        else {
            //Kalau ada, daftarkan listener-nya
            Toast.makeText(this, "Geomagnetic Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(
                this,
                geomagneticSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        //Cek apakah ada sensor cahaya
        if(lightSensor == null)
        {
            Toast.makeText(this, "No Light Sensor detected.", Toast.LENGTH_SHORT).show()
        }
        else {
            //Kalau ada, daftarkan listener-nya
            Toast.makeText(this, "Light Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }

        //Cek apakah ada sensor proximity
        if(proximitySensor == null)
        {
            Toast.makeText(this, "No proximity Sensor detected.", Toast.LENGTH_SHORT).show()
        }
        else {
            //Kalau ada, daftarkan listener-nya
            Toast.makeText(this, "Proximity Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let {
            when (p0.sensor.type)
            {
                Sensor.TYPE_ACCELEROMETER -> {
                    //Masukkan data ke reading
                    accelerometerReadings = it.values
                    var x = it.values[0]
                    var y = it.values[1]
                    var z = it.values[2]
                    textAccelometer.text = "X: $x Y: $y Z: $z"
                    //Untuk menghitung langkah
                    var magnitude = sqrt(x.pow(2) + y.pow(2) + z.pow(2))
                    previousMagnitude?.let {
                        //Hitunng perbedaan magnitude
                        val diff = magnitude - it
                        if(diff > 6)
                        {
                            stepCount++
                            textStep.text = "Step: $stepCount"
                        }
                    }
                    previousMagnitude = magnitude
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magneticReading = it.values
                }
                Sensor.TYPE_LIGHT -> {
                    lightReading = it.values[0]
                    textLight.text = "Light: $lightReading"
                }
                Sensor.TYPE_PROXIMITY -> {
                    proximityReading = it.values[0]
                    textProximity.text = "Proximity: $proximityReading"
                    //Jika ada objek yang sangat dekat (jaraknya 0), aktifkan dark mode
                    if(proximityReading <= 0f)
                    {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }
        //Cari orientasi gawai
        var rotationMatrix = FloatArray(9)
        var orientationAngles = FloatArray(3)

        SensorManager.getRotationMatrix(rotationMatrix,null, accelerometerReadings, magneticReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        var azimuth = Math.toDegrees(orientationAngles[0].toDouble())
        var pitch = Math.toDegrees(orientationAngles[1].toDouble())
        var roll = Math.toDegrees(orientationAngles[2].toDouble())

        textGyro.text = "Azimut: ${(azimuth.toInt())}\nPitch: ${(pitch.toInt())}\nRoll: " +
                "${(roll.toInt())}"
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}