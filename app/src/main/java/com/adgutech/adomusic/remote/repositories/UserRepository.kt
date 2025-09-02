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

package com.adgutech.adomusic.remote.repositories

import com.adgutech.adomusic.remote.api.spotify.SpotifyService
import com.adgutech.adomusic.remote.api.spotify.models.UserPrivate
import com.adgutech.adomusic.remote.api.spotify.models.UserPublic

/**
 * Created by Adolfo Gutierrez on 04/22/25.
 */

interface UserRepository {
    fun getMe(): UserPrivate
    fun getUser(userId: String): UserPublic
}

class RealUserRepository(private val spotifyService: SpotifyService) : UserRepository {

    override fun getMe(): UserPrivate {
        return spotifyService.me
    }

    override fun getUser(userId: String): UserPublic {
        return spotifyService.getUser(userId)
    }
}