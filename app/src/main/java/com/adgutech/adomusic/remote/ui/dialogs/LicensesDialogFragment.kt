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

package com.adgutech.adomusic.remote.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.adgutech.commons.extensions.setLicensesView
import com.adgutech.adomusic.remote.interfaces.ParcelableState
import com.adgutech.adomusic.remote.interfaces.getState
import com.adgutech.adomusic.remote.interfaces.putState
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.commons.extensions.colorButtons
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.NoticesXmlParser
import de.psdev.licensesdialog.model.Notices
import kotlinx.parcelize.Parcelize

class LicensesDialogFragment : DialogFragment() {

    private lateinit var notices: Notices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notices = savedInstanceState?.getState<State>()?.notices
            ?: NoticesXmlParser.parse(resources.openRawResource(R.raw.licenses))
                .apply { addNotice(LicensesDialog.LICENSES_DIALOG_NOTICE) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putState(State(notices))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.title_licenses)
            .apply { setLicensesView(notices) }
            .setNegativeButton(R.string.action_ok, null)
            .create()
            .colorButtons(requireContext())
    }

    @Parcelize
    private class State(val notices: Notices) : ParcelableState
}