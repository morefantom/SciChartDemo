package com.adrosonic.adrocafe.scichartdemo

import android.app.Application
import android.util.Log
import com.scichart.charting.visuals.SciChartSurface

class SciChartDemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // On Android set this code once in MainActivity or application startup
        try {
            SciChartSurface.setRuntimeLicenseKey("Ai3D8nQsmJfDG8P7CAwDnNlFFzTxVNeNneYstNTA7Ey0w5yUtWtJUn6QOs0OGKX8FTNUXoZxMzUwPM7AvlUElleOJyT7wuqwInNHc996EAPafIec20u+cX4xl4VcMb7HJqHZJ/6W15Htyv+Xwu3Z5kJv1IuDfXU0ZYudXhdRXH7d0drqQU/ZoCN2adYm7AltTcjln8TDUe1rUw6t9/0pfcSDAN2QnhIqTjSAm/df+bkKt4qyhe92M1A8itlYoQHuKLgVdA9Ye7Ofmz1B3JYkI91G/FBrELGe9bVjUtChWa1FR0FbQElwQqNazuSrhfRNp66wOn+neZ8KuF4BwaLPIqIW6zV4YOmHC5z11IzIDnBoXY/EcZT5oM8b6tnaoMZakUfn+yVyobIEi/Lf9vyL04Ko2x7ums5I2+gw4/2hu/Z3j85dQzgBgYpAIsIMQlU9ngs22/nWZI2LBk3DLWUsUMtBO46BRrzAo/31RMevd0A7Eo/fErEXMRNIRdPyUS5LD4zifEqI7+kAXcZu/pCA291i3eklRTC2xeQPmTA=")
        } catch (e: Exception) {
            Log.e("SciChart", "Error when setting the license", e)
        }
    }
}