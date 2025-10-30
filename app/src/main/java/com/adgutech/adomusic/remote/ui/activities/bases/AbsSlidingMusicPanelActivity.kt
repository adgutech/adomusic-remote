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

package com.adgutech.adomusic.remote.ui.activities.bases

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import com.adgutech.adomusic.remote.ui.fragments.players.material.MaterialFragment
import com.adgutech.adomusic.remote.BLACK
import com.adgutech.adomusic.remote.R
import com.adgutech.adomusic.remote.WHITE
import com.adgutech.adomusic.remote.databinding.SlidingMusicPanelLayoutBinding
import com.adgutech.adomusic.remote.extensions.logD
import com.adgutech.adomusic.remote.extensions.preference
import com.adgutech.adomusic.remote.helpers.AppRemoteHelper
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.ADAPTIVE_COLOR
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.CIRCLE_PLAY_BUTTON
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.EXTRA_CONTROLS
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.NOW_PLAYING_SCREEN
import com.adgutech.adomusic.remote.preferences.Preferences.Companion.VOLUME_VISIBILITY_MODE
import com.adgutech.adomusic.remote.ui.activities.PermissionsActivity
import com.adgutech.adomusic.remote.ui.fragments.MiniPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.NowPlayingScreen
import com.adgutech.adomusic.remote.ui.fragments.NowPlayingScreen.*
import com.adgutech.adomusic.remote.ui.fragments.bases.AbsPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.blur.BlurPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.color.ColorFragment
import com.adgutech.adomusic.remote.ui.fragments.players.md3.MD3PlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.normal.PlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.peek.PeekPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.plain.PlainPlayerFragment
import com.adgutech.adomusic.remote.ui.fragments.players.simple.SimplePlayerFragment
import com.adgutech.commons.extensions.darkAccentColor
import com.adgutech.commons.extensions.dip
import com.adgutech.commons.extensions.getBottomInsets
import com.adgutech.commons.extensions.hide
import com.adgutech.commons.extensions.isColorLight
import com.adgutech.commons.extensions.isLandscape
import com.adgutech.commons.extensions.keepScreenOn
import com.adgutech.commons.extensions.maybeSetScreenOn
import com.adgutech.commons.extensions.peekHeightAnimate
import com.adgutech.commons.extensions.setLightNavigationBar
import com.adgutech.commons.extensions.setLightNavigationBarAuto
import com.adgutech.commons.extensions.setLightStatusBar
import com.adgutech.commons.extensions.setLightStatusBarAuto
import com.adgutech.commons.extensions.setNavigationBarColorPreOreo
import com.adgutech.commons.extensions.setTaskDescriptionColor
import com.adgutech.commons.extensions.show
import com.adgutech.commons.extensions.surfaceColor
import com.adgutech.commons.extensions.viewBinding
import com.adgutech.commons.extensions.whichFragment
import com.adgutech.commons.hasVersionOreo
import com.adgutech.commons.hasVersionTiramisu
import com.adgutech.commons.preference.PreferenceBase
import com.adgutech.commons.preference.PreferenceBase.Companion.TAB_TITLES_MODE
import com.adgutech.commons.utils.ViewUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.google.android.material.navigation.NavigationBarView

/**
 * Created by Adolfo Gutiérrez on 02/18/2025.
 */

abstract class AbsSlidingMusicPanelActivity : AbsSpotifyServiceActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val TAG: String = AbsSlidingMusicPanelActivity::class.java.simpleName
    }

    val binding by viewBinding(SlidingMusicPanelLayoutBinding::inflate)

    val navigationView: NavigationBarView
        get() = binding.navigationView

    val slidingPanel: FrameLayout
        get() = binding.slidingPanel

    val isBottomNavVisible: Boolean
        get() = navigationView.isVisible && navigationView is BottomNavigationView

    var fromNotification = false
    private var windowInsets: WindowInsetsCompat? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var playerFragment: AbsPlayerFragment
    private var miniPlayerFragment: MiniPlayerFragment? = null
    private var nowPlayingScreen: NowPlayingScreen? = null
    private var taskColor: Int = 0
    private var paletteColor: Int = WHITE
    private var navigationBarColor = 0

    private val panelState: Int
        get() = bottomSheetBehavior.state
    private var panelStateBefore: Int? = null
    private var panelStateCurrent: Int? = null
    private var isInOneTabMode = false

    private var navigationBarColorAnimator: ValueAnimator? = null
    private val argbEvaluator: ArgbEvaluator = ArgbEvaluator()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (handleBackPress()) {
                return
            }
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_container) as NavHostFragment
            if (!navHostFragment.navController.navigateUp()) {
                finish()
            }
        }
    }

    private val bottomSheetCallbackList by lazy {
        object : BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                setMiniPlayerAlphaProgress(slideOffset)
                navigationBarColorAnimator?.cancel()
                setNavigationBarColorPreOreo(
                    argbEvaluator.evaluate(
                        slideOffset,
                        surfaceColor(),
                        navigationBarColor
                    ) as Int
                )
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (panelStateCurrent != null) {
                    panelStateBefore = panelStateCurrent
                }
                panelStateCurrent = newState
                when (newState) {
                    STATE_EXPANDED -> {
                        onPanelExpanded()
                    }

                    STATE_COLLAPSED -> {
                        onPanelCollapsed()
                        if (!preference.isKeepScreenOn) {
                            keepScreenOn(false)
                        }
                    }

                    STATE_SETTLING, STATE_DRAGGING -> {
                        if (fromNotification) {
                            navigationView.bringToFront()
                            fromNotification = false
                        }
                    }

                    else -> {
                        logD("Do a flip")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasVersionTiramisu) {
            if (!hasPermissionsNotifications()) {
                startActivity(Intent(this, PermissionsActivity::class.java))
                finish()
            }
        }
        setContentView(binding.root)
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            windowInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
            insets
        }
        chooseFragmentForTheme()
        setupSlidingUpPanel()
        setupBottomSheet()
        updateColor()
        if (!preference.isMaterialYou) {
            slidingPanel.backgroundTintList = ColorStateList.valueOf(darkAccentColor())
            navigationView.backgroundTintList = ColorStateList.valueOf(darkAccentColor())
        }

        navigationBarColor = surfaceColor()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    fun getBottomSheetBehavior() = bottomSheetBehavior

    private fun setupBottomSheet() {
        bottomSheetBehavior = from(binding.slidingPanel)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallbackList)
        bottomSheetBehavior.significantVelocityThreshold = 300
        setMiniPlayerAlphaProgress(0F)
    }

    override fun onResume() {
        super.onResume()
        preference.registerOnSharedPreferenceChangedListener(this)
        if (nowPlayingScreen != preference.nowPlayingScreen) {
            postRecreate()
        }
        if (bottomSheetBehavior.state == STATE_EXPANDED) {
            setMiniPlayerAlphaProgress(1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preference.unregisterOnSharedPreferenceChangedListener(this)
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallbackList)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            EXTRA_CONTROLS -> {
                miniPlayerFragment?.setUpButtons()
            }

            NOW_PLAYING_SCREEN -> {
                chooseFragmentForTheme()
                binding.slidingPanel.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = if (nowPlayingScreen != Peek) {
                        ViewGroup.LayoutParams.MATCH_PARENT
                    } else {
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    onServiceConnected()
                }
            }

            VOLUME_VISIBILITY_MODE, CIRCLE_PLAY_BUTTON -> {
                chooseFragmentForTheme()
                onServiceConnected()
            }

            ADAPTIVE_COLOR -> {
                if (preference.nowPlayingScreen in listOf(Normal, Material)) {
                    chooseFragmentForTheme()
                    onServiceConnected()
                }
            }

            TAB_TITLES_MODE -> {
                navigationView.labelVisibilityMode = preference.tabTitleMode
            }

            PreferenceBase.FULL_SCREEN_MODE -> {
                recreate()
            }

            PreferenceBase.KEEP_SCREEN_ON -> {
                maybeSetScreenOn()
            }
        }
    }

    fun collapsePanel() {
        bottomSheetBehavior.state = STATE_COLLAPSED
    }

    fun expandPanel() {
        bottomSheetBehavior.state = STATE_EXPANDED
    }

    private fun setMiniPlayerAlphaProgress(progress: Float) {
        if (progress < 0) return
        val alpha = 1 - progress
        miniPlayerFragment?.view?.alpha = 1 - (progress / 0.2F)
        miniPlayerFragment?.view?.isGone = alpha == 0f
        if (!isLandscape) {
            navigationView.translationY = progress * 500
            navigationView.alpha = alpha
        }
        binding.playerFragmentContainer.alpha = (progress - 0.2F) / 0.2F
    }

    @Suppress("DEPRECATION")
    private fun animateNavigationBarColor(color: Int) {
        if (hasVersionOreo) return
        navigationBarColorAnimator?.cancel()
        navigationBarColorAnimator = ValueAnimator
            .ofArgb(window.navigationBarColor, color).apply {
                duration = ViewUtil.ANIM_TIME.toLong()
                interpolator = PathInterpolator(0.4f, 0f, 1f, 1f)
                addUpdateListener { animation: ValueAnimator ->
                    setNavigationBarColorPreOreo(
                        animation.animatedValue as Int
                    )
                }
                start()
            }
    }

    open fun onPanelCollapsed() {
        setMiniPlayerAlphaProgress(0F)
        // restore values
        animateNavigationBarColor(surfaceColor())
        setLightStatusBarAuto()
        setLightNavigationBarAuto()
        setTaskDescriptionColor(taskColor)
        //playerFragment?.onHide()
    }

    open fun onPanelExpanded() {
        setMiniPlayerAlphaProgress(1F)
        onPaletteColorChanged()
        //playerFragment?.onShow()
    }

    private fun setupSlidingUpPanel() {
        slidingPanel.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                slidingPanel.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (nowPlayingScreen != Peek) {
                    slidingPanel.updateLayoutParams<ViewGroup.LayoutParams> {
                        height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
                when (panelState) {
                    STATE_EXPANDED -> onPanelExpanded()
                    STATE_COLLAPSED -> onPanelCollapsed()
                    else -> {
                        //ss playerFragment!!.onHide()
                    }
                }
            }
        })
    }

    override fun onServiceConnected() {
        hideBottomSheet(false)
        super.onServiceConnected()
    }

    private fun handleBackPress(): Boolean {
        if (panelState == STATE_EXPANDED || (panelState == STATE_SETTLING && panelStateBefore != STATE_EXPANDED)) {
            collapsePanel()
            return true
        }
        return false
    }

    private fun onPaletteColorChanged() {
        if (panelState == STATE_EXPANDED) {
            navigationBarColor = surfaceColor()
            setTaskDescColor(paletteColor)
            val isColorLight = paletteColor.isColorLight
            if (preference.isAdaptiveColor && (nowPlayingScreen == Normal || nowPlayingScreen == Material)) {
                setLightNavigationBar(true)
                setLightStatusBar(isColorLight)
            } else if (nowPlayingScreen == Blur) {
                animateNavigationBarColor(BLACK)
                navigationBarColor = BLACK
                setLightStatusBar(false)
                setLightNavigationBar(true)
            } else if (nowPlayingScreen == Color) {
                animateNavigationBarColor(paletteColor)
                navigationBarColor = paletteColor
                setLightNavigationBar(isColorLight)
                setLightStatusBar(isColorLight)
            }
        }
    }

    private fun updateColor() {
        libraryViewModel.paletteColor.observe(this) { color ->
            this.paletteColor = color
            onPaletteColorChanged()
        }
    }

    private fun setTaskDescColor(color: Int) {
        taskColor = color
        if (panelState == STATE_COLLAPSED) {
            setTaskDescriptionColor(color)
        }
    }

    fun setBottomNavVisibility(
        visible: Boolean,
        animate: Boolean = false,
        hideBottomSheet: Boolean = AppRemoteHelper.spotifyAppRemote?.isConnected == false
    ) {
        if (isInOneTabMode) {
            hideBottomSheet(
                hide = hideBottomSheet,
                animate = animate,
                isBottomNavVisible = false
            )
            return
        }
        if (visible xor navigationView.isVisible) {
            val mAnimate = animate && bottomSheetBehavior.state == STATE_COLLAPSED
            if (mAnimate) {
                if (visible) {
                    navigationView.bringToFront()
                    navigationView.show()
                } else {
                    navigationView.hide()
                }
            } else {
                navigationView.isVisible = visible
                if (visible && bottomSheetBehavior.state != STATE_EXPANDED) {
                    navigationView.bringToFront()
                }
            }
        }
        hideBottomSheet(
            hide = hideBottomSheet,
            animate = animate,
            isBottomNavVisible = visible && navigationView is BottomNavigationView
        )
    }

    private fun hideBottomSheet(
        hide: Boolean,
        animate: Boolean = false,
        isBottomNavVisible: Boolean = navigationView.isVisible && navigationView is BottomNavigationView,
    ) {
        val heightOfBar =
            windowInsets.getBottomInsets(this) + dip(com.adgutech.commons.R.dimen.mini_player_height)
        val heightOfBarWithTabs = heightOfBar + dip(com.adgutech.commons.R.dimen.bottom_nav_height)
        if (hide) {
            bottomSheetBehavior.peekHeight = (-windowInsets.getBottomInsets(this)).coerceAtLeast(0)
            bottomSheetBehavior.state = STATE_COLLAPSED
            libraryViewModel.setFabMargin(
                this,
                if (isBottomNavVisible) dip(com.adgutech.commons.R.dimen.bottom_nav_height) else 0
            )
        } else {
            AppRemoteHelper.spotifyAppRemote?.let {
                if (it.isConnected) {
                    binding.slidingPanel.elevation = 0F
                    binding.navigationView.elevation = 5F
                    if (isBottomNavVisible) {
                        logD("List")
                        if (animate) {
                            bottomSheetBehavior.peekHeightAnimate(heightOfBarWithTabs)
                        } else {
                            bottomSheetBehavior.peekHeight = heightOfBarWithTabs
                        }
                        libraryViewModel.setFabMargin(
                            this,
                            dip(com.adgutech.commons.R.dimen.bottom_nav_mini_player_height)
                        )
                    } else {
                        logD("Details")
                        if (animate) {
                            bottomSheetBehavior.peekHeightAnimate(heightOfBar).doOnEnd {
                                binding.slidingPanel.bringToFront()
                            }
                        } else {
                            bottomSheetBehavior.peekHeight = heightOfBar
                            binding.slidingPanel.bringToFront()
                        }
                        libraryViewModel.setFabMargin(
                            this,
                            dip(com.adgutech.commons.R.dimen.mini_player_height)
                        )
                    }
                }
            }
        }
    }

    fun setAllowDragging(allowDragging: Boolean) {
        bottomSheetBehavior.isDraggable = allowDragging
        hideBottomSheet(false)
    }

    private fun chooseFragmentForTheme() {
        nowPlayingScreen = preference.nowPlayingScreen

        val fragment: AbsPlayerFragment = when (nowPlayingScreen) {
            Blur -> BlurPlayerFragment()
            Color -> ColorFragment()
            Material -> MaterialFragment()
            MD3 -> MD3PlayerFragment()
            Normal -> PlayerFragment()
            Peek -> PeekPlayerFragment()
            Plain -> PlainPlayerFragment()
            Simple -> SimplePlayerFragment()
            else -> PlayerFragment()
        } // must extend AbsPlayerFragment
        supportFragmentManager.commit {
            replace(R.id.playerFragmentContainer, fragment)
        }
        supportFragmentManager.executePendingTransactions()
        playerFragment = whichFragment(R.id.playerFragmentContainer)
        miniPlayerFragment = whichFragment<MiniPlayerFragment>(R.id.miniPlayerFragment)
        miniPlayerFragment?.view?.setOnClickListener { expandPanel() }
    }
}