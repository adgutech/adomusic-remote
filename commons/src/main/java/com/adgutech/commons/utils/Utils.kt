/*
 * Copyright (C) 2022-2025 Adolfo Gutiérrez <adgutech@gmail.com>
 * and Contributors.
 *
 * This file is part of Adgutech.
 *
 *  Adgutech is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.adgutech.commons.utils

import java.text.DecimalFormat
import java.util.Locale

/**
 * Created by Adolfo Gutierrez on 03/14/25.
 */

object Utils {

    fun buildInfoString(string1: String?, string2: String?): String {
        if (string1.isNullOrEmpty()) {
            return if (string2.isNullOrEmpty()) "" else string2
        }
        return if (string2.isNullOrEmpty()) if (string1.isNullOrEmpty()) "" else string1 else "$string1  •  $string2"
    }

    fun formatValue(numValue: Float): String {
        var value = numValue
        val arr = arrayOf("", "K", "M", "B", "T", "P", "E")
        var index = 0
        while (value / 1000 >= 1) {
            value /= 1000
            index++
        }
        val decimalFormat = DecimalFormat("#.##")
        return String.format("%s %s", decimalFormat.format(value.toDouble()), arr[index])
    }

    fun getReadableDurationString(songDurationMillis: Long): String {
        var minutes = songDurationMillis / 1000 / 60
        val seconds = songDurationMillis / 1000 % 60
        return if (minutes < 60) {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
            )
        } else {
            val hours = minutes / 60
            minutes %= 60
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            )
        }
    }

    fun getReadableDurationStringNew(songDurationMillis: Long): String {
        var minutes = songDurationMillis / 1000 / 60
        return if (minutes < 60) {
            String.format(Locale.getDefault(), "%s min", minutes)
        } else {
            val hours = minutes / 60
            minutes %= 60
            String.format(Locale.getDefault(), "%s h %s min", hours, minutes)
        }
    }

    fun getSectionName(title: String?, stripPrefix: Boolean = false): String {
        var itemTitle = title
        return try {
            if (itemTitle.isNullOrEmpty()) {
                return "-"
            }
            itemTitle = itemTitle.trim { it <= ' ' }.lowercase()
            if (stripPrefix) {
                if (itemTitle.startsWith("the ")) {
                    itemTitle = itemTitle.substring(4)
                } else if (itemTitle.startsWith("a ")) {
                    itemTitle = itemTitle.substring(2)
                }
            }
            if (itemTitle.isEmpty()) {
                ""
            } else {
                itemTitle.substring(0, 1).uppercase()
            }
        } catch (e: Exception) {
            ""
        }
    }
}