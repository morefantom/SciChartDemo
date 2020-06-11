package com.adrosonic.adrocafe.scichartdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adrosonic.adrocafe.scichartdemo.databinding.ActivityMainBinding
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.LineAnnotation
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.renderableSeries.data.OhlcRenderPassData
import com.scichart.core.framework.UpdateSuspender
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.extensions.builders.SciChartBuilder
import org.json.JSONArray
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var sciChartBuilder: SciChartBuilder? = null

    private var ohlcList = listOf<Ohlc>()

    private var ohlcDataSeries =  OhlcDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)
    private var lineXYDataSeries = XyDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)
    private var barXYDataSeries = XyDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)
    private var heikinDataSeries = OhlcDataSeries(Date::class.javaObjectType, Double::class.javaObjectType)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ohlcList = readJSONFromAsset()
        SciChartBuilder.init(this)
        sciChartBuilder = SciChartBuilder.instance()

        val sciChartSurfaceHeikinAshi = SciChartSurface(this)
        val sciChartSurfaceBar = SciChartSurface(this)
        val sciChartSurfaceLine = SciChartSurface(this)
        val sciChartSurfaceCandleStick = SciChartSurface(this)
        val sciChartSurfaceLineAnnotation = SciChartSurface(this)

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

        build(sciChartSurfaceCandleStick, sciChartSurfaceLine,
            sciChartSurfaceBar, sciChartSurfaceHeikinAshi, sciChartSurfaceLineAnnotation)

    }

    private fun build(sciChartSurfaceCandleStick: SciChartSurface, sciChartSurfaceLine: SciChartSurface,
                      sciChartSurfaceBar: SciChartSurface, sciChartSurfaceHeikinAshi: SciChartSurface,
                      sciChartSurfaceLineAnnotation: SciChartSurface
    ) {
        buildCandleStick(sciChartSurfaceCandleStick)
        buildLine(sciChartSurfaceLine)
        buildBar(sciChartSurfaceBar)
        buildHeikenAshi(sciChartSurfaceHeikinAshi)
        buildLineAnnotation(sciChartSurfaceLineAnnotation)
    }

    private fun buildCandleStick(sciChartSurfaceCandleStick: SciChartSurface) {
        binding?.llScichartCandlestick?.addView(sciChartSurfaceCandleStick)
        val xAxis_cs = sciChartBuilder?.buildDateAxis()
        val yAxis_cs = sciChartBuilder?.buildNumericAxis()

        val candlestickSeries = sciChartBuilder?.newCandlestickSeries()
            ?.withStrokeUp(ColorUtil.DarkGreen)
            ?.withFillUpColor(ColorUtil.Green)
            ?.withStrokeDown(ColorUtil.DarkRed)
            ?.withFillDownColor(ColorUtil.Red)
            ?.withDataSeries(ohlcDataSeries)
            ?.build()

        //listener which will print hello if there's no data on the screen
        xAxis_cs?.setVisibleRangeChangeListener { iAxisCore, iRange, iRange2, b ->
            val size = (candlestickSeries?.currentRenderPassData as OhlcRenderPassData).indices.size()
            if (size == 0) {
                Log.i("hello", "hello")
            }
        }

        UpdateSuspender.using(sciChartSurfaceCandleStick) {
            Collections.addAll(sciChartSurfaceCandleStick.yAxes, yAxis_cs)
            Collections.addAll(sciChartSurfaceCandleStick.xAxes, xAxis_cs)
            Collections.addAll(sciChartSurfaceCandleStick.renderableSeries, candlestickSeries)
            Collections.addAll(sciChartSurfaceCandleStick.chartModifiers, sciChartBuilder?.newModifierGroupWithDefaultModifiers()?.build())
        }
    }

    private fun buildLine(sciChartSurfaceLine: SciChartSurface) {
        binding?.llScichartLine?.addView(sciChartSurfaceLine)

        val xAxis_l = sciChartBuilder?.buildDateAxis()
        val yAxis_l = sciChartBuilder?.buildNumericAxis()
        val fastLineRenderableSeries = sciChartBuilder?.newLineSeries()
            ?.withDataSeries(lineXYDataSeries)
            ?.withStrokeStyle(ColorUtil.DarkGreen, 1f, true)
            ?.build()
        UpdateSuspender.using(sciChartSurfaceLine) {
            Collections.addAll(sciChartSurfaceLine.yAxes, yAxis_l)
            Collections.addAll(sciChartSurfaceLine.xAxes, xAxis_l)
            Collections.addAll(sciChartSurfaceLine.renderableSeries, fastLineRenderableSeries)
            Collections.addAll(sciChartSurfaceLine.chartModifiers, sciChartBuilder?.newModifierGroupWithDefaultModifiers()?.build())
        }
    }

    private fun buildBar(sciChartSurfaceBar: SciChartSurface) {
        binding?.llScichartBar?.addView(sciChartSurfaceBar)

        val xAxis_b = sciChartBuilder?.buildDateAxis()
        val yAxis_b = sciChartBuilder?.buildNumericAxis()
        val fastColumnRenderableSeries = sciChartBuilder?.newColumnSeries()
            ?.withStrokeStyle(ColorUtil.LightBlue, 1f)
            ?.withDataPointWidth(0.5)
            ?.withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
            ?.withDataSeries(barXYDataSeries)
            ?.build()
        UpdateSuspender.using(sciChartSurfaceBar) {
            Collections.addAll(sciChartSurfaceBar.yAxes, yAxis_b)
            Collections.addAll(sciChartSurfaceBar.xAxes, xAxis_b)
            Collections.addAll(sciChartSurfaceBar.renderableSeries, fastColumnRenderableSeries)
            Collections.addAll(sciChartSurfaceBar.chartModifiers, sciChartBuilder?.newModifierGroupWithDefaultModifiers()?.build())
        }
    }

    private fun buildHeikenAshi(sciChartSurfaceHeikinAshi: SciChartSurface) {
        binding?.llScichartHeikin?.addView(sciChartSurfaceHeikinAshi)
        val xAxis_ha = sciChartBuilder?.buildDateAxis()
        val yAxis_ha = sciChartBuilder?.buildNumericAxis()
        val heikinashiSeries = sciChartBuilder?.newCandlestickSeries()
            ?.withStrokeUp(ColorUtil.DarkGreen)
            ?.withFillUpColor(ColorUtil.Green)
            ?.withStrokeDown(ColorUtil.DarkRed)
            ?.withFillDownColor(ColorUtil.Red)
            ?.withDataSeries(heikinDataSeries)
            ?.build()
        UpdateSuspender.using(sciChartSurfaceHeikinAshi) {
            Collections.addAll(sciChartSurfaceHeikinAshi.yAxes, yAxis_ha)
            Collections.addAll(sciChartSurfaceHeikinAshi.xAxes, xAxis_ha)
            Collections.addAll(sciChartSurfaceHeikinAshi.renderableSeries, heikinashiSeries)
            Collections.addAll(sciChartSurfaceHeikinAshi.chartModifiers, sciChartBuilder?.newModifierGroupWithDefaultModifiers()?.build())
        }
    }

    private fun buildLineAnnotation(sciChartSurfaceLineAnnotation: SciChartSurface) {
        binding?.llScichartLnAnt?.addView(sciChartSurfaceLineAnnotation)
        val xAxis_lant = sciChartBuilder?.buildNumericAxis()
        val yAxis_lant = sciChartBuilder?.buildNumericAxis()
        var lineAnnotation = LineAnnotation(this)
        lineAnnotation.stroke = SolidPenStyle(-0xffff01, true, 4f, null)
        lineAnnotation.setIsEditable(true)
        lineAnnotation.x1 = 1.0
        lineAnnotation.y1 = 4.6
        lineAnnotation.x2 = 10.0
        lineAnnotation.y2 = 9.1

        UpdateSuspender.using(sciChartSurfaceLineAnnotation) {
            Collections.addAll(sciChartSurfaceLineAnnotation.yAxes, yAxis_lant)
            Collections.addAll(sciChartSurfaceLineAnnotation.xAxes, xAxis_lant)
            Collections.addAll(sciChartSurfaceLineAnnotation.annotations, lineAnnotation)
            Collections.addAll(sciChartSurfaceLineAnnotation.chartModifiers, sciChartBuilder?.newModifierGroupWithDefaultModifiers()?.build())
        }
    }

    private fun readJSONFromAsset(): List<Ohlc>{
        var json: String ?= null
        var ohlcList: MutableList<Ohlc>  = mutableListOf()
        try {
            val inputStream: InputStream  =assets.open("json/tohlcv.json")
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

    private fun SciChartBuilder.buildDateAxis()  = this.newCategoryDateAxis()
        .withVisibleRange(ohlcList.size.toDouble() - 30, ohlcList.size.toDouble())
        .withGrowBy(0.0, 0.1)
        .build()

    private fun SciChartBuilder.buildNumericAxis() = this.newNumericAxis()
        .withGrowBy(0.0, 0.1)
        .withAutoRangeMode(AutoRange.Always)
        .build()
}
