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

package com.adgutech.adomusic.remote.adapters.bases

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.adgutech.adomusic.remote.R
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder

abstract class MediaEntryViewHolder(
    itemView: View
) : AbstractDraggableSwipeableItemViewHolder(itemView), View.OnClickListener,
    View.OnLongClickListener {

    var image: ImageView? = null
    var menu: ImageView? = null

    var icon: ImageView? = null

    var title: TextView? = null
    var text: TextView? = null
    var text2: TextView? = null
    var imageText: TextView? = null
    var time: TextView? = null

    init {
        image = itemView.findViewById(R.id.image)
        menu = itemView.findViewById(R.id.menu)

        icon = itemView.findViewById(R.id.icon)

        title = itemView.findViewById(R.id.title)
        text = itemView.findViewById(R.id.text)
        text2 = itemView.findViewById(R.id.text2)
        imageText = itemView.findViewById(R.id.imageText)
        time = itemView.findViewById(R.id.time)

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    override fun getSwipeableContainerView(): View {
        return viewNullable()!!
    }

    private fun viewNullable(): View? {
        return null
    }

    override fun onClick(v: View?) {}

    override fun onLongClick(v: View?): Boolean {
        return false
    }

    fun setImageTransitionName(transitionName: String) {
        itemView.transitionName = transitionName
        /*if (image != null) {
            image!!.transitionName = transitionName
        }*/
    }
}