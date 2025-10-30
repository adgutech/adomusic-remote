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

package com.adgutech.adomusic.remote.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.adgutech.adomusic.remote.BuildConfig
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.logE
import com.adgutech.adomusic.remote.extensions.toAlbumId
import com.adgutech.adomusic.remote.extensions.toTrackId
import com.adgutech.adomusic.remote.models.TrackParcelable
import com.adgutech.adomusic.remote.api.spotify.models.ArtistSimple
import com.adgutech.adomusic.remote.api.spotify.models.Image
import com.adgutech.commons.utils.Utils
import com.spotify.protocol.types.Artist
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Created by Adolfo Gutiérrez on 05/08/2025.
 */

object Utils {

    @Suppress("DEPRECATION")
    fun getImageDataFromBase64Encode(context: Context, uri: Uri): String {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            val bytes = stream.toByteArray()
            val data = Base64.encodeToString(bytes, Base64.DEFAULT)
            data
        } catch (e: IOException) {
            logE("Error to get Base64 image data: $e")
            ""
        }
    }

    fun getReadableDurationString(trackDurationMillis: Long): String {
        return Utils.getReadableDurationString(trackDurationMillis)
    }

    fun formatDate(inputDateString: String): String {
        val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val date = try {
            LocalDate.parse(inputDateString, inputFormatter)
        } catch (e: DateTimeParseException) {
            logE(e)
            return inputDateString
        }
        val outputFormatter = DateTimeFormatter.ofPattern(
            "MMMM dd, yyyy", Locale.getDefault()
        )
        return date.format(outputFormatter)
    }

    fun formatYear(inputDateString: String): String {
        val builder = StringBuilder()
        builder.append(inputDateString)
        builder.delete(4, 10)
        val year = builder.toString()
        return year
    }

    fun seeDetailsOnSpotify(link: String, isPlayer: Boolean): Intent {
        val url = if (isPlayer) "https://open.spotify.com/album/${link.toAlbumId()}" else link
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        return intent
    }

    fun shareContentOfSpotify(link: String, isPlayer: Boolean): Intent {
        val url = if (isPlayer) "https://open.spotify.com/track/${link.toTrackId()}" else link
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, url)
        intent.setType("text/plain")
        return intent
    }

    fun getSectionName(title: String?, stripPrefix: Boolean = false): String {
        return Utils.getSectionName(title, stripPrefix)
    }

    fun getFollowersFormat(context: Context, followers: Int): String {
        val followersCount = Utils.formatValue(followers.toFloat())
        return String.format("%s %s", followersCount, context.getString(R.string.followers))
    }

    fun formatValue(numValue: Float): String {
        return Utils.formatValue(numValue)
    }

    fun getReadableDurationStringNew(songDurationMillis: Long): String {
        return Utils.getReadableDurationStringNew(songDurationMillis)
    }

    fun getInfoString(
        context: Context,
        tracks: List<TrackParcelable>,
        trackTotal: Int
    ): String {
        val duration = getTotalDuration(tracks)
        return Utils.buildInfoString(
            getTrackCountString(context, trackTotal),
            Utils.getReadableDurationStringNew(duration)
        )
    }

    fun getPlaylistInfoString(context: Context, displayName: String?, trackTotal: Int): String {
        return Utils.buildInfoString(displayName ?: "", getTrackCountString(context, trackTotal))
    }

    fun getTrackCountString(context: Context, trackCount: Int): String {
        val trackString =
            if (trackCount == 1) context.resources.getString(R.string.count_song) else context.resources.getString(
                R.string.count_songs
            )
        return "$trackCount $trackString"
    }

    fun getTotalDuration(tracks: List<TrackParcelable>): Long {
        var duration: Long = 0
        for (i in tracks.indices) {
            duration += tracks[i].duration
        }
        return duration
    }

    fun getImageUrl(image: List<Image>): String {
        return try {
            image[0].url
        } catch (e: IndexOutOfBoundsException) {
            logE(e)
            ""
        }
    }

    @JvmName("getArtistsFromArtistSimpleList")
    fun getArtists(artists: List<ArtistSimple>): String {
        val builder = StringBuilder()
        for (i in artists.indices) {
            builder.append(artists[i].name)
            if (i != artists.size - 1) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    @JvmName("getArtistsFromArtistList")
    fun getArtists(artists: List<Artist>): String {
        val builder = StringBuilder()
        for (i in artists.indices) {
            builder.append(artists[i].name)
            if (i != artists.size - 1) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    /**
     * You can enjoy the Snowfall feature during the christmas season with favorite christmas
     * songs. This feature is available exclusively for the christmas season.
     */
    fun isSnowfallAvailable(): Boolean {
        val featureStartDate = LocalDate
            .of(BuildConfig.CURRENT_YEAR, Month.NOVEMBER, 15)
        val featureEndDate = LocalDate
            .of(BuildConfig.NEXT_YEAR, Month.JANUARY, 7)
        val currentDate = LocalDate.now()
        return !currentDate.isBefore(featureStartDate) && currentDate.isBefore(featureEndDate)
    }
}