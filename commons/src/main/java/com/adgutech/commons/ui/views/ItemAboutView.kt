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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.adgutech.commons.R
import com.adgutech.commons.databinding.ViewAboutBinding
import com.adgutech.commons.extensions.isGone

class ItemAboutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        val binding = ViewAboutBinding.inflate(LayoutInflater.from(context), this, true)
        context.withStyledAttributes(attrs, R.styleable.ItemAboutView) {
            if (hasValue(R.styleable.ItemAboutView_aboutIcon)) {
                binding.aboutIcon.setImageDrawable(getDrawable(R.styleable.ItemAboutView_aboutIcon))
            }
            binding.aboutTitle.text = getText(R.styleable.ItemAboutView_aboutTitle)
            binding.aboutText.text = getText(R.styleable.ItemAboutView_aboutText)
            if (!isInEditMode) {
                if (binding.aboutText.text.isEmpty()) {
                    binding.aboutText.isGone()
                }
            }
        }
    }
}