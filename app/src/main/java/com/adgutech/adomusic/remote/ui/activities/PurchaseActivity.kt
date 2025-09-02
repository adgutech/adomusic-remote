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

package com.adgutech.adomusic.remote.ui.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.text.parseAsHtml
import com.adgutech.adomusic.remote.ADOMUSIC_REMOTE_PRO_PRODUCT_ID
import com.adgutech.adomusic.remote.BuildConfig
import com.adgutech.adomusic.remote.ORDER_HISTORY_GOOGLE_PLAY
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.application.App
import com.adgutech.adomusic.remote.databinding.ActivityPurchaseBinding
import com.adgutech.adomusic.remote.ui.activities.bases.AbsThemeActivity
import com.adgutech.commons.extensions.accentColor
import com.adgutech.commons.extensions.createViewIntent
import com.adgutech.commons.extensions.getColorByAttr
import com.adgutech.commons.extensions.setLightStatusBar
import com.adgutech.commons.extensions.setStatusBarColor
import com.adgutech.commons.extensions.setStatusBarColorAuto
import com.adgutech.commons.extensions.setTaskDescriptionColorAuto
import com.adgutech.commons.extensions.showToast
import com.adgutech.commons.extensions.startActivitySafe
import com.adgutech.commons.extensions.viewBinding
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo

class PurchaseActivity : AbsThemeActivity(), BillingProcessor.IBillingHandler {

    private val binding by viewBinding(ActivityPurchaseBinding::inflate)
    private lateinit var billingProcessor: BillingProcessor

    companion object {
        val TAG: String = PurchaseActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(Color.TRANSPARENT)
        setLightStatusBar(false)
        setStatusBarColorAuto()
        setTaskDescriptionColorAuto()
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupTitle()

        binding.apply {
            manageButton.isEnabled = false
            restoreButton.isEnabled = false
        }

        billingProcessor = BillingProcessor(this, BuildConfig.GOOGLE_PLAY_LICENSING_KEY, this)

        binding.purchaseButton.setOnClickListener {
            billingProcessor.purchase(this@PurchaseActivity, ADOMUSIC_REMOTE_PRO_PRODUCT_ID)
        }

        binding.restoreButton.setOnClickListener {
            restorePurchase()
        }
        binding.manageButton.setOnClickListener {
            startActivitySafe(Uri.parse(ORDER_HISTORY_GOOGLE_PLAY).createViewIntent())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        showToast(R.string.text_purchased)
        setResult(RESULT_OK)
    }

    override fun onPurchaseHistoryRestored() {
        if (App.isProVersion()) {
            showToast(R.string.text_restored_previous_purchase_please_restart)
            setResult(RESULT_OK)
        } else {
            showToast(R.string.text_no_purchase_found)
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e(TAG, "Billing error: code = $errorCode", error)
    }

    override fun onBillingInitialized() {
        binding.apply {
            manageButton.isEnabled = true
            restoreButton.isEnabled = true
        }
    }

    private fun setupTitle() {
        val hexColor = String.format("#%06X", 0xFFFFFF and accentColor())
        val hexColorControlNormal = String.format("#%06X", getColorByAttr(android.R.attr.textColorPrimary))
        val appName = "<font color=$hexColor>Ado<b>Music</b></font><font color=gray> Remote</font><font color=$hexColorControlNormal> PRO</font>".parseAsHtml()
        binding.toolbar.title = appName
    }

    private fun restorePurchase() {
        showToast(R.string.text_restoring_purchase)
        billingProcessor.loadOwnedPurchasesFromGoogleAsync(object :
            BillingProcessor.IPurchasesResponseListener {
            override fun onPurchasesSuccess() {
                onPurchaseHistoryRestored()
            }

            override fun onPurchasesError() {
                showToast(R.string.text_could_not_restore_purchase)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        billingProcessor.release()
    }
}