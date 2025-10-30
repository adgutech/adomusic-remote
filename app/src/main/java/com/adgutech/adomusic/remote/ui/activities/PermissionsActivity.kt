package com.adgutech.adomusic.remote.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.databinding.ActivityPermissionsBinding
import com.adgutech.adomusic.remote.ui.activities.bases.AbsSpotifyServiceActivity
import com.adgutech.commons.extensions.accentBackgroundColor
import com.adgutech.commons.extensions.setStatusBarColorAuto
import com.adgutech.commons.extensions.setTaskDescriptionColorAuto
import com.adgutech.commons.hasVersionTiramisu

class PermissionsActivity : AbsSpotifyServiceActivity() {

    private lateinit var binding: ActivityPermissionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColorAuto()
        setTaskDescriptionColorAuto()

        if (hasVersionTiramisu) {
            binding.notificationsPermission.setOnClickPermissionListener {
                requestPermissionsNotifications()
            }
        }

        binding.btnFinish.accentBackgroundColor(this)
        binding.btnFinish.setOnClickListener {
            if (hasVersionTiramisu) {
                if (hasPermissionsNotifications()) {
                    startActivity(
                        Intent(this, MainActivity::class.java).addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    )
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                remove()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (hasVersionTiramisu) {
            binding.btnFinish.isEnabled = hasNotificationPermission()
            binding.notificationsPermission.isEnabledButton = !hasNotificationPermission()
            if (hasNotificationPermission()) {
                binding.notificationsPermission.buttonText = getString(R.string.permission_granted)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun hasNotificationPermission(): Boolean {
        return hasPermissionsNotifications()
    }
}