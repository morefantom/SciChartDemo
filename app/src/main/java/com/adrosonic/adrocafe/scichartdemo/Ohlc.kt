package com.adrosonic.adrocafe.scichartdemo

import java.util.*

data class Ohlc(
    var date: Date,
    var open: Double,
    var high: Double,
    var low: Double,
    var close: Double
)