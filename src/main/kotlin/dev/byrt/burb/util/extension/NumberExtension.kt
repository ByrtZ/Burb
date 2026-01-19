package dev.byrt.burb.util.extension

import java.text.DecimalFormat

fun Double.fullDecimal(): String {
    return DecimalFormat("#.######").format(this)
}