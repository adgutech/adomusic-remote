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

package com.adgutech.commons.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.withStyledAttributes
import com.adgutech.commons.R
import com.adgutech.commons.databinding.ViewPermissionBinding
import com.adgutech.commons.extensions.accentColor
import com.google.android.material.card.MaterialCardView

class PermissionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var binding: ViewPermissionBinding =
        ViewPermissionBinding.inflate(LayoutInflater.from(context), this, true)

    var buttonText
        get() = binding.button.text.toString()
        set(value) {
            binding.button.text = value
        }

    var isEnabledButton: Boolean
        get() = binding.button.isEnabled
        set(value) {
            binding.button.isEnabled = value
        }

    init {

        context.withStyledAttributes(attrs, R.styleable.PermissionView, 0, 0) {
            binding.title.text = getText(R.styleable.PermissionView_permissionTitle)
            binding.text.text = getText(R.styleable.PermissionView_permissionSummary)
            binding.button.text = getText(R.styleable.PermissionView_permissionButtonText)

            if (hasValue(R.styleable.PermissionView_permissionIcon)) {
                binding.icon.setImageDrawable(getDrawable(R.styleable.PermissionView_permissionIcon))
            }

            if (!isInEditMode) {
                binding.button.accentColor(context)
            }
        }
    }

    fun setOnClickPermissionListener(callback: (view: View) -> Unit) {
        binding.button.setOnClickListener { callback(it) }
    }
}