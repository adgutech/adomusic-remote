package com.adgutech.adomusic.remote.ui.dialogs

import android.app.Dialog
import android.media.audiofx.Equalizer
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.materialDialog
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.EqualizerHelper
import com.adgutech.adomusic.remote.models.EqualizerPreset
import com.adgutech.commons.extensions.colorButtons

class EqualizerPresetDialog(
    private val equalizer: Equalizer,
    private val callback: (presetId: Any) -> Unit
) : DialogFragment() {

    companion object {
        val TAG: String = EqualizerPresetDialog::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val presetList = arrayListOf<EqualizerPreset>()
        (0 until equalizer.numberOfPresets).mapTo(presetList) {
            EqualizerPreset(it, equalizer.getPresetName(it.toShort()))
        }
        presetList.add(EqualizerPreset(EqualizerHelper.EQUALIZER_PRESET_CUSTOM, getString(R.string.action_equalizer_custom)))

        val presetName = mutableListOf<String>()
        for (equalizerPreset: EqualizerPreset in presetList) {
            presetName.add(equalizerPreset.title)
        }

        return materialDialog(R.string.action_equalizer)
            .setSingleChoiceItems(presetName.toTypedArray(), preference.equalizerPreset) { dialog, witch ->
                callback(presetList[witch].value)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons(requireContext())
    }
}