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

import java.io.FileInputStream
import java.util.Properties

/**
 * Start project: february 16th, 2025 09:46:06 p.m.
 * Finish project: August 23th, 2025
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.adgutech.adomusic.remote"
    compileSdk = 36

    defaultConfig {
        manifestPlaceholders += mapOf(
            "redirectSchemeName" to "spotify-sdk",
            "redirectHostName" to "auth"
        )
        applicationId = "com.adgutech.adomusic.remote"
        minSdk = 26
        targetSdk = 36
        versionCode = 11
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "GOOGLE_PLAY_LICENSING_KEY",
            "\"${getProperty(getProperties("public.properties"), "licensingKey")}\""
        )

        // I've created the "CURRENT_YEAR" and "NEXT_YEAR" attributes for Snowfall feature.
        // Each year, I'll modify the current and next years. If you use the old version,
        // it won't work because the method "LocalDate.of(int year, Month month, int
        // dayOfMonth)" requires you to add the year.
        buildConfigField("int", "CURRENT_YEAR", "2025")
        buildConfigField("int", "NEXT_YEAR", "2026")
    }
    signingConfigs {
        register("release") {
            keyAlias = getProperty(getProperties("keystore.properties"), "keyAlias")
            keyPassword = getProperty(getProperties("keystore.properties"), "keyPassword")
            storeFile = file(getProperty(getProperties("keystore.properties"), "storeFile"))
            storePassword = getProperty(getProperties("keystore.properties"), "storePassword")
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "AdoMusic Remote (Debug)")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = true
        warning += mutableSetOf("ImpliedQuantity", "Instantiatable", "MissingQuantity", "MissingTranslation")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dependenciesInfo {
        includeInBundle = false
        includeInApk = false
    }
}

fun getProperties(fileName: String): Properties {
    val properties = Properties()
    val file = rootProject.file(fileName)
    if (file.exists()) {
        properties.load(FileInputStream(file))
    }
    return properties
}

fun getProperty(properties: Properties?, name: String): String {
    return properties?.getProperty(name) ?: "$name missing"
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":appthemehelper"))
    implementation(project(":commons"))

    implementation(project(":spotify-app-remote"))
    implementation(project(":spotify-auth"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.mediarouter)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.github.jetradarmobile.androidSnowfall)

    implementation(libs.github.bumptech.glide)
    ksp(libs.github.bumptech.glide.ksp)
    implementation(libs.github.bumptech.glide.okhttp3.integration)

    implementation(libs.google.android.material)
    implementation(libs.google.android.play.featureDelivery)
    implementation(libs.google.android.play.review)
    implementation(libs.google.code.gson)

    implementation(libs.afollestad.material.dialogs.color)
    implementation(libs.afollestad.material.dialogs.core)
    implementation(libs.afollestad.material.dialogs.input)
    implementation(libs.afollestad.material.cab)
    implementation(libs.anjlab.android.iab.v3.library)
    implementation(libs.cat.ereza.customactivityoncrash)
    implementation(libs.heinrichreimersoftware.materialIntro)
    implementation(libs.h6ah4i.advrecyclerview)
    implementation(libs.insert.koin.android)
    implementation(libs.insert.koin.core)
    implementation(libs.squareup.retrofit)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}