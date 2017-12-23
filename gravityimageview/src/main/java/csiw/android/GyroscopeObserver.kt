package csiw.android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*

/**
 * Created by PatelMilan on 23/12/2017.
 * Email : patelmilan2692@gmail.com
 * Mo : 8306306809
 */
class GyroscopeObserver : SensorEventListener {
    private var mSensorManager: SensorManager? = null
    // The time in nanosecond when last sensor event happened.
    private var mLastTimestamp: Long = 0

    // The radian the device already rotate along y-axis.
    private var mRotateRadianY: Double = 0.toDouble()

    // The radian the device already rotate along x-axis.
    private var mRotateRadianX: Double = 0.toDouble()
    // The maximum radian that the device should rotate along x-axis and y-axis to show image's bounds
    // The value must between (0, π/2].
    private var mMaxRotateRadian = Math.PI / 9

    // The ImageViews to be notified when the device rotate.
    private val mViews = LinkedList<ImageViews>()

    fun register(context: Context) {
        if (mSensorManager == null) {
            mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        val mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager!!.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST)

        mLastTimestamp = 0
        mRotateRadianX = 0.0
        mRotateRadianY = mRotateRadianX
    }

    fun unregister() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this)
            mSensorManager = null
        }
    }

    internal fun addImageViews(view: ImageViews?) {
        if (view != null && !mViews.contains(view)) {
            mViews.addFirst(view)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (mLastTimestamp == 0L) {
            mLastTimestamp = event.timestamp
            return
        }

        val rotateX = Math.abs(event.values[0])
        val rotateY = Math.abs(event.values[1])
        val rotateZ = Math.abs(event.values[2])

        if (rotateY > rotateX + rotateZ) {
            val dT = (event.timestamp - mLastTimestamp) * NS2S
            mRotateRadianY += (event.values[1] * dT).toDouble()
            when {
                mRotateRadianY > mMaxRotateRadian -> mRotateRadianY = mMaxRotateRadian
                mRotateRadianY < -mMaxRotateRadian -> mRotateRadianY = -mMaxRotateRadian
                else -> mViews
                        .filter { it.orientation == ImageViews.ORIENTATION_HORIZONTAL }
                        .forEach { it.updateProgress((mRotateRadianY / mMaxRotateRadian).toFloat()) }
            }
        } else if (rotateX > rotateY + rotateZ) {
            val dT = (event.timestamp - mLastTimestamp) * NS2S
            mRotateRadianX += (event.values[0] * dT).toDouble()
            when {
                mRotateRadianX > mMaxRotateRadian -> mRotateRadianX = mMaxRotateRadian
                mRotateRadianX < -mMaxRotateRadian -> mRotateRadianX = -mMaxRotateRadian
                else -> mViews
                        .filter { it.orientation == ImageViews.ORIENTATION_VERTICAL }
                        .forEach { it.updateProgress((mRotateRadianX / mMaxRotateRadian).toFloat()) }
            }
        }

        mLastTimestamp = event.timestamp
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    fun setMaxRotateRadian(maxRotateRadian: Double) {
        if (maxRotateRadian <= 0 || maxRotateRadian > Math.PI / 2) {
            throw IllegalArgumentException("The maxRotateRadian must be between (0, π/2].")
        }
        this.mMaxRotateRadian = maxRotateRadian
    }

    companion object {
        // For translate nanosecond to second.
        private val NS2S = 1.0f / 1000000000.0f
    }
}
