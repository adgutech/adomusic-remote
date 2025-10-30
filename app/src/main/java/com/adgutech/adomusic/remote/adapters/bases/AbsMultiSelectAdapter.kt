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

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.NumberRollViewBinding
import com.adgutech.adomusic.remote.ui.views.NumberRollView
import com.adgutech.commons.hasVersionMarshmallow

abstract class AbsMultiSelectAdapter<V : RecyclerView.ViewHolder?, I>(
    open val activity: FragmentActivity, @MenuRes menuRes: Int
) : RecyclerView.Adapter<V>(), ActionMode.Callback {

    var actionMode: ActionMode? = null
    private val checked: MutableList<I>
    private var menuRes: Int

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (actionMode != null) {
                actionMode?.finish()
                remove()
            }
        }
    }

    init {
        checked = ArrayList()
        this.menuRes = menuRes
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater = mode?.menuInflater
        inflater?.inflate(menuRes, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_multi_select_adapter_check_all) {
            checkAll()
        } else {
            onMultipleItemAction(item!!, ArrayList(checked))
            actionMode?.finish()
            clearChecked()
        }
        return true
    }

    @Suppress("DEPRECATION")
    override fun onDestroyActionMode(mode: ActionMode?) {
        clearChecked()
        activity.window.statusBarColor = when {
            hasVersionMarshmallow -> Color.TRANSPARENT
            else -> Color.BLACK
        }
        actionMode = null
        onBackPressedCallback.remove()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkAll() {
        if (actionMode != null) {
            checked.clear()
            for (i in 0 until itemCount) {
                val identifier = getIdentifier(i)
                if (identifier != null) {
                    checked.add(identifier)
                }
            }
            notifyDataSetChanged()
            updateCab()
        }
    }

    protected abstract fun getIdentifier(position: Int): I?

    protected abstract fun getName(model: I): String?

    protected fun isChecked(identifier: I): Boolean {
        return checked.contains(identifier)
    }

    protected val isInQuickSelectMode: Boolean
        get() = actionMode != null

    protected abstract fun onMultipleItemAction(menuItem: MenuItem, selection: List<I>)
    protected fun setMultiSelectMenuRes(@MenuRes menuRes: Int) {
        this.menuRes = menuRes
    }

    protected fun toggleChecked(position: Int): Boolean {
        val identifier = getIdentifier(position) ?: return false
        if (!checked.remove(identifier)) {
            checked.add(identifier)
        }
        notifyItemChanged(position)
        updateCab()
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearChecked() {
        checked.clear()
        notifyDataSetChanged()
    }

    private fun updateCab() {
        if (actionMode == null) {
            actionMode = activity.startActionMode(this)?.apply {
                customView = NumberRollViewBinding.inflate(activity.layoutInflater).root
            }
            activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        }
        val size = checked.size
        when {
            size <= 0 -> {
                actionMode?.finish()
            }

            else -> {
                actionMode?.customView?.findViewById<NumberRollView>(R.id.selection_mode_number)
                    ?.setNumber(size, true)
            }
        }
    }
}