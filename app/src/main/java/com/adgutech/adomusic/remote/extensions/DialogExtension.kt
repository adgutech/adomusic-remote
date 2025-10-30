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

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.adgutech.adomusic.remote.BuildConfig
import com.adgutech.commons.extensions.materialDialogDebug
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.materialDialog(@StringRes title: Int): MaterialAlertDialogBuilder {
    return if (BuildConfig.DEBUG) {
        materialDialogDebug(title)
    } else {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
    }
}

fun Fragment.materialDialog(@StringRes title: Int): MaterialAlertDialogBuilder {
    return requireContext().materialDialog(title)
}

fun Fragment.materialDialog(): MaterialDialog {
    return MaterialDialog(requireContext())
        .cornerRadius(res = com.adgutech.commons.R.dimen.m3_dialog_corner_size)
}