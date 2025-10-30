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

package com.adgutech.adomusic.remote.service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.service.MusicService
import com.adgutech.adomusic.remote.service.MusicService.Companion.ACTION_QUIT
import com.adgutech.adomusic.remote.ui.activities.MainActivity
import com.adgutech.commons.hasVersionMarshmallow
import com.adgutech.commons.hasVersionOreo

@Suppress("RestrictedApi")
class AppRemoteNotificationImpl24(val context: MusicService) : AppRemoteNotification(context) {

    init {
        val action = Intent(context, MainActivity::class.java)
        action.putExtra(MainActivity.EXPAND_PANEL, context.preference.isExpandPanel)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val clickIntent =
            PendingIntent.getActivity(
                context,
                0,
                action,
                PendingIntent.FLAG_UPDATE_CURRENT or if (hasVersionMarshmallow)
                    PendingIntent.FLAG_IMMUTABLE
                else 0
            )
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (hasVersionMarshmallow)
                PendingIntent.FLAG_IMMUTABLE
            else 0)
        )
        val dismissAction = NotificationCompat.Action(
            0,
            context.getString(R.string.action_close),
            retrieveRemoteAction()
        )
        setSmallIcon(R.drawable.ic_notification)
        setContentIntent(clickIntent)
        setDeleteIntent(deleteIntent)
        addAction(dismissAction)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    override fun updateStatusRemote(isConnected: Boolean) {
        if (isConnected) {
            setContentTitle(context.getString(R.string.title_notification_app_remote_connected))
            setContentText(context.getString(R.string.summary_notification_app_remote_connected))
        } else {
            setContentTitle(context.getString(R.string.title_notification_app_remote_disconnected))
            setContentText(context.getString(R.string.summary_notification_app_remote_disconnected))
        }
    }

    private fun retrieveRemoteAction(): PendingIntent {
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        return PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or
                    if (hasVersionMarshmallow) PendingIntent.FLAG_IMMUTABLE
                    else 0
        )
    }

    companion object {

        fun from(
            context: MusicService,
            notificationManager: NotificationManager
        ): AppRemoteNotification {
            if (hasVersionOreo) {
                createNotificationChannel(context, notificationManager)
            }
            return AppRemoteNotificationImpl24(context)
        }
    }
}