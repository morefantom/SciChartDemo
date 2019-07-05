package com.adrosonic.adrocafe.scichartdemo

import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.adrosonic.adrocafe.scichartdemo.databinding.ActivityMainBinding
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.drawing.utility.ColorUtil
import com.scichart.extensions.builders.SciChartBuilder
import java.util.*
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPaletteProvider
import com.scichart.core.framework.UpdateSuspender
import org.json.JSONArray
import java.io.InputStream
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        var ohlcList = readJSONFromAsset()

        val sciChartSurfaceCandleStick = SciChartSurface(this)
        val sciChartSurfaceLine = SciChartSurface(this)
        val sciChartSurfaceBar = SciChartSurface(this)
        val sciChartSurfaceHeikinAshi = SciChartSurface(this)

        binding.llScichartCandlestick.addView(sciChartSurfaceCandleStick)
        binding.llScichartLine.addView(sciChartSurfaceLine)
        binding.llScichartBar.addView(sciChartSurfaceBar)
        binding.llScichartHeikin.addView(sciChartSurfaceHeikinAshi)

        SciChartBuilder.init(this)
        val sciChartBuilder = SciChartBuilder.instance()

        val xAxis_cs = sciChartBuilder.newCategoryDateAxis()
            .withVisibleRange(ohlcList.size.toDouble() - 30, ohlcList.size.toDouble())
            .withGrowBy(0.0, 0.1)
            .build()

        val xAxis_l = sciChartBuilder.newCategoryDateAxis()
            .withVisibleRange(ohlcList.size.toDouble() - 30, ohlcList.size.toDouble())
            .withGrowBy(0.0, 0.1)
            .build()

        val xAxis_b = sciChartBuilder.newCategoryDateAxis()
            .withVisibleRange(ohlcList.size.toDouble() - 30, ohlcList.size.toDouble())
            .withGrowBy(0.0, 0.1)
            .build()

        val xAxis_ha = sciChartBuilder.newCategoryDateAxis()
            .withVisibleRange(ohlcList.size.toDouble() - 30, ohlcList.size.toDouble())
            .withGrowBy(0.0, 0.1)
            .build()

        val yAxis_cs = sciChartBuilder.newNumericAxis()
            .withGrowBy(0.0, 0.1)
            .withAutoRangeMode(AutoRange.Always)
            .build()

        val yAxis_l = sciChartBuilder.newNumericAxis()
            .withGrowBy(0.0, 0.1)
            .withAutoRangeMode(AutoRange.Always)
            .build()

        val yAxis_b = sciChartBuilder.newNumericAxis()
            .withGrowBy(0.0, 0.1)
            .withAutoRangeMode(AutoRange.Always)
            .build()

        val yAxis_ha = sciChartBuilder.newNumericAxis()
            .withGrowBy(0.0, 0.1)
            .withAutoRangeMode(AutoRange.Always)
            .build()

        var ohlcDataSeries =  OhlcDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)
        var lineXYDataSeries = XyDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)
        var barXYDataSeries = XyDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)
        var heikinDataSeries = OhlcDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)

        var prevOpen = 0.0

        ohlcList.forEach {ohlc ->
            ohlcDataSeries.append(
                ohlc.date,
                ohlc.open,
                ohlc.high,
                ohlc.low,
                ohlc.close)
            lineXYDataSeries.append(
                ohlc.date,
                ohlc.open
            )
            barXYDataSeries.append(
                ohlc.date,
                ohlc.open
            )
            heikinDataSeries.append(
                ohlc.date,
                prevOpen,
                maxOf(ohlc.high, ohlc.open, ohlc.close),
                minOf(ohlc.low, ohlc.open, ohlc.close),
                (ohlc.open+ohlc.close+ohlc.low+ohlc.close)/4
            )
            prevOpen = (ohlc.open + ohlc.close)/2
        }

        val candlestickSeries = sciChartBuilder.newCandlestickSeries()
            .withStrokeUp(ColorUtil.DarkGreen)
            .withFillUpColor(ColorUtil.Green)
            .withStrokeDown(ColorUtil.DarkRed)
            .withFillDownColor(ColorUtil.Red)
            .withDataSeries(ohlcDataSeries)
            .build()

        val fastLineRenderableSeries = sciChartBuilder.newLineSeries()
            .withDataSeries(lineXYDataSeries)
            .withStrokeStyle(ColorUtil.DarkGreen, 1f, true)
            .build()

        val fastColumnRenderableSeries = sciChartBuilder.newColumnSeries()
            .withStrokeStyle(ColorUtil.LightBlue, 1f)
            .withDataPointWidth(0.5)
            .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
            .withDataSeries(barXYDataSeries)
            .build()

        val heikinashiSeries = sciChartBuilder.newCandlestickSeries()
            .withStrokeUp(ColorUtil.DarkGreen)
            .withFillUpColor(ColorUtil.Green)
            .withStrokeDown(ColorUtil.DarkRed)
            .withFillDownColor(ColorUtil.Red)
            .withDataSeries(heikinDataSeries)
            .build()

//        val additionalModifiers = sciChartBuilder.newModifierGroup()
//            .withPinchZoomModifier().build()
//            .withZoomPanModifier().withReceiveHandledEvents(true).build()
//            .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
//            .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Scale).withClipModex(ClipMode.None).build()
//            .withYAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).build()
//            .build()

        UpdateSuspender.using(sciChartSurfaceCandleStick) {
            //candlestick
            Collections.addAll(sciChartSurfaceCandleStick.yAxes, yAxis_cs)
            Collections.addAll(sciChartSurfaceCandleStick.xAxes, xAxis_cs)
            Collections.addAll(sciChartSurfaceCandleStick.renderableSeries, candlestickSeries)
            Collections.addAll(sciChartSurfaceCandleStick.chartModifiers, sciChartBuilder.newModifierGroupWithDefaultModifiers().build())
            //line
            Collections.addAll(sciChartSurfaceLine.yAxes, yAxis_l)
            Collections.addAll(sciChartSurfaceLine.xAxes, xAxis_l)
            Collections.addAll(sciChartSurfaceLine.renderableSeries, fastLineRenderableSeries)
            Collections.addAll(sciChartSurfaceLine.chartModifiers, sciChartBuilder.newModifierGroupWithDefaultModifiers().build())
            //bar
            Collections.addAll(sciChartSurfaceBar.yAxes, yAxis_b)
            Collections.addAll(sciChartSurfaceBar.xAxes, xAxis_b)
            Collections.addAll(sciChartSurfaceBar.renderableSeries, fastColumnRenderableSeries)
            Collections.addAll(sciChartSurfaceBar.chartModifiers, sciChartBuilder.newModifierGroupWithDefaultModifiers().build())
            //heikin ashi
            Collections.addAll(sciChartSurfaceHeikinAshi.yAxes, yAxis_ha)
            Collections.addAll(sciChartSurfaceHeikinAshi.xAxes, xAxis_ha)
            Collections.addAll(sciChartSurfaceHeikinAshi.renderableSeries, heikinashiSeries)
            Collections.addAll(sciChartSurfaceHeikinAshi.chartModifiers, sciChartBuilder.newModifierGroupWithDefaultModifiers().build())
        }

//        Collections.addAll(sciChartSurface.annotations, textAnnotation)
//        Collections.addAll(sciChartSurface.chartModifiers, additionalModifiers)

//        var lineData = sciChartBuilder.newXyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType).build()
//        var scatterData = sciChartBuilder.newXyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType).build()

//        for (i: Int in 0..10){
//            lineData.append(i, Math.sin(i*0.1))
//            scatterData.append(i, Math.cos(i*0.1))
//        }

//        val lineSeries = sciChartBuilder.newLineSeries()
//            .withDataSeries(lineData)
//            .withStrokeStyle(ColorUtil.LightBlue, 2f, true)
//            .build()
//
//        val pointMarker = sciChartBuilder.newPointMarker(EllipsePointMarker())
//            .withFill(ColorUtil.LightBlue)
//            .withStroke(ColorUtil.Green, 2f)
//            .withSize(10)
//            .build()
//
//        val scatterSeries = sciChartBuilder.newScatterSeries()
//            .withDataSeries(scatterData)
//            .withPointMarker(pointMarker)
//            .build()

//        sciChartSurface.renderableSeries.add(scatterSeries)
//        sciChartSurface.renderableSeries.add(lineSeries)
//        sciChartSurface.zoomExtents()

//        val legendModifier = sciChartBuilder.newModifierGroup()
//            .withLegendModifier()
//            .withOrientation(Orientation.HORIZONTAL)
//            .withPosition(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 10)
//            .build()
//            .build()

//        sciChartSurface.chartModifiers.add(legendModifier)

//        val cursorModifier = sciChartBuilder.newModifierGroup()
//            .withCursorModifier().withShowTooltip(true).build()
//            .build()
//
//        sciChartSurface.chartModifiers.add(cursorModifier)

    }

    fun readJSONFromAsset(): List<Ohlc>{
        var json: String ?= null
        var ohlcList: MutableList<Ohlc>  = mutableListOf()
        try {
            val inputStream: InputStream  =assets.open("tohlcv.json")
            json = inputStream.bufferedReader().use {
                it.readText()
            }
            val jsonArray = JSONArray(json)
            val insdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            for (i: Int in 0 until jsonArray.length()){
                val subJsonArray = JSONArray(jsonArray[i].toString())
                ohlcList.add(Ohlc(
                    insdf.parse(subJsonArray[0].toString()),
                    subJsonArray[1].toString().toDouble(),
                    subJsonArray[2].toString().toDouble(),
                    subJsonArray[3].toString().toDouble(),
                    subJsonArray[4].toString().toDouble()))
            }
            Log.i("JSON", json)
        }catch (e: Exception){
            e.stackTrace
        }
        return ohlcList
    }
}
