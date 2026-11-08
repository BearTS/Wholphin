package com.github.damontecres.wholphin.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.tv.material3.Typography
import com.github.damontecres.wholphin.R

/** Inter, Free My TV's font (variable: one file for every weight). */
@OptIn(ExperimentalTextApi::class)
val Inter =
    FontFamily(
        listOf(400, 500, 600, 700, 800).map { w ->
            Font(
                resId = R.font.inter_variable,
                weight = FontWeight(w),
                variationSettings = FontVariation.Settings(FontVariation.weight(w)),
            )
        },
    )

private fun TextStyle.inter() = copy(fontFamily = Inter)

private val tvBase = Typography()

val AppTypography =
    Typography(
        displayLarge = tvBase.displayLarge.inter(),
        displayMedium = tvBase.displayMedium.inter(),
        displaySmall = tvBase.displaySmall.inter(),
        headlineLarge = tvBase.headlineLarge.inter(),
        headlineMedium = tvBase.headlineMedium.inter(),
        headlineSmall = tvBase.headlineSmall.inter(),
        titleLarge = tvBase.titleLarge.inter(),
        titleMedium = tvBase.titleMedium.inter(),
        titleSmall = tvBase.titleSmall.inter(),
        bodyLarge = tvBase.bodyLarge.inter(),
        bodyMedium = tvBase.bodyMedium.inter(),
        bodySmall = tvBase.bodySmall.inter(),
        labelLarge = tvBase.labelLarge.inter(),
        labelMedium = tvBase.labelMedium.inter(),
        labelSmall = tvBase.labelSmall.inter(),
    )

private val materialBase = androidx.compose.material3.Typography()

val MaterialTypography =
    androidx.compose.material3.Typography(
        displayLarge = materialBase.displayLarge.inter(),
        displayMedium = materialBase.displayMedium.inter(),
        displaySmall = materialBase.displaySmall.inter(),
        headlineLarge = materialBase.headlineLarge.inter(),
        headlineMedium = materialBase.headlineMedium.inter(),
        headlineSmall = materialBase.headlineSmall.inter(),
        titleLarge = materialBase.titleLarge.inter(),
        titleMedium = materialBase.titleMedium.inter(),
        titleSmall = materialBase.titleSmall.inter(),
        bodyLarge = materialBase.bodyLarge.inter(),
        bodyMedium = materialBase.bodyMedium.inter(),
        bodySmall = materialBase.bodySmall.inter(),
        labelLarge = materialBase.labelLarge.inter(),
        labelMedium = materialBase.labelMedium.inter(),
        labelSmall = materialBase.labelSmall.inter(),
    )
