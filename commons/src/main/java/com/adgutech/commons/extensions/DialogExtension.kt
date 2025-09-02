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

package com.adgutech.commons.extensions

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.adgutech.commons.R
//import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun AlertDialog.colorButtons(context: Context): AlertDialog {
    setOnShowListener {
        getButton(AlertDialog.BUTTON_POSITIVE).accentTextColor(context)
        getButton(AlertDialog.BUTTON_NEGATIVE).accentTextColor(context)
        getButton(AlertDialog.BUTTON_NEUTRAL).accentTextColor(context)
    }
    return this
}

fun Context.materialDialogDebug(
    @StringRes title: Int
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(
        this, R.style.materialAlertDialogTheme
    ).setTitle(title)
}

/*fun Fragment.materialDialog(): MaterialDialog {
    return MaterialDialog(requireContext())
        .cornerRadius(res = R.dimen.m3_dialog_corner_size)
}*/