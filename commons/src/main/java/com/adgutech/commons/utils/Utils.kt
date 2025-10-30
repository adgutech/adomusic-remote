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

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
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
        } catch (_: Exception) {
            ""
        }
    }

    /**
     * creates a new file in storage in app specific directory.
     *
     * @return the file
     * @throws IOException
     */
    fun createFile(
        context: Context,
        directoryName: String,
        fileName: String,
        body: String,
        fileType: String
    ): File {
        val root = createDirectory(context, directoryName)
        val filePath = "$root/$fileName$fileType"
        val file = File(filePath)

        // create file if not exist
        if (!file.exists()) {
            try {
                // create a new file and write text in it.
                file.createNewFile()
                file.writeText(body)
                Log.d(Utils::class.java.name, "File has been created and saved")
            } catch (e: IOException) {
                Log.d(Utils::class.java.name, e.message.toString())
            }
        }
        return file
    }

    /**
     * creates a new directory in storage in app specific directory.
     *
     * @return the file
     */
    private fun createDirectory(context: Context, directoryName: String): File {
        val file = File(
            context.getExternalFilesDir(directoryName)
                .toString()
        )
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }

    fun shareFile(context: Context, file: File, mimeType: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName,
                file
            ))
            context.startActivity(Intent.createChooser(this, null))
        }
    }
}