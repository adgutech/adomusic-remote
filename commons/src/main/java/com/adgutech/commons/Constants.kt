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

package com.adgutech.commons

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

const val GOOGLE_PLAY_DETAILS = "https://play.google.com/store/apps/details"
const val PAYPAL_URL = "https://paypal.me/adgutech"
const val PRIVACY_POLICY_URL = "https://sites.google.com/view/adgutech-privacy-policy"

//API 23 | Marshmallow | Android 6.0
val hasVersionMarshmallow: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.M)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

//API 24 | Nougat | Android 7.0
val hasVersionNougat: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.N)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

//API 25 | Nougat(MR1) | Android Android 7.1
val hasVersionNougatMR1: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.N_MR1)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

//API 26 | Oreo | Android 8
val hasVersionOreo: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.O)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

//API 27 | Oreo(MR1) | Android 8.1
val hasVersionOreoMR1: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.O_MR1)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

//API 28 | Pie | Android 9
val hasVersionPie: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.P)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

//API 29 | Q | Android 10
val hasVersionQ: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.Q)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

//API 30 | R | Android 11
val hasVersionR: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.R)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

//API 31 | S | Android 12
val hasVersionS: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.S)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

//API 32 | R | Android 12.2
val hasVersionSV2: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.S_V2)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2

//API 33 | Tiramisu | Android 13
val hasVersionTiramisu: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.TIRAMISU)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

//API 34 | Upside Down Cake | Android 14
val hasVersionUpsideDownCake: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

//API 35 | Vanilla Ice Cream | Android 15
val hasVersionVanillaIceCream: Boolean
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    get() =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

//API 36 | ??? | Android 16
//Coming soon...