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

package com.adgutech.adomusic.remote.billing

import android.content.Context
import com.adgutech.adomusic.remote.ADOMUSIC_REMOTE_PRO_PRODUCT_ID
import com.adgutech.adomusic.remote.BuildConfig
import com.adgutech.adomusic.remote.R
import com.adgutech.commons.extensions.showToast
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo

/**
 * Automatically restores purchases.
 */
class BillingManager(context: Context) {

    private val billingProcessor: BillingProcessor

    init {
        billingProcessor = BillingProcessor(
            context, BuildConfig.GOOGLE_PLAY_LICENSING_KEY,
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(productId: String, details: PurchaseInfo?) {}

                override fun onPurchaseHistoryRestored() {
                    context.showToast(R.string.text_restored_previous_purchase_please_restart)
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {}

                override fun onBillingInitialized() {}
            }
        )
    }

    fun release() {
        billingProcessor.release()
    }

    val isProVersion: Boolean
        get() = billingProcessor.isPurchased(ADOMUSIC_REMOTE_PRO_PRODUCT_ID)
}