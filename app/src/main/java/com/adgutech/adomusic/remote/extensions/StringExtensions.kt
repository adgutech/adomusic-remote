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

package com.adgutech.adomusic.remote.extensions

/**
 * Deleted the first character 'spotify:album:' from uri to get the id.
 */
fun String.toAlbumId(): String {
    val builder = StringBuilder()
    builder.append(this)
    builder.delete(0, 14)
    val id = builder.toString()
    return id
}

/**
 * Deleted the first character 'spotify:artist:' from uri to get the id.
 */
fun String.toArtistId(): String {
    val builder = StringBuilder()
    builder.append(this)
    builder.delete(0, 15)
    val id = builder.toString()
    return id
}

/**
 * Deleted the first character 'spotify:track:' from uri to get the id.
 */
fun String.toTrackId(): String {
    val builder = StringBuilder()
    builder.append(this)
    builder.delete(0, 14)
    val id = builder.toString()
    return id
}