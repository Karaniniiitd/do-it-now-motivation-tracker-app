package com.karan.do_it_now_motivation_tracker.ui.theme

import androidx.compose.ui.graphics.Color

// ── Core Pixel Palette ────────────────────────────────────────
val PixelBlack  = Color(0xFF000000)
val PixelWhite  = Color(0xFFFFFFFF)
val PixelGray1  = Color(0xFFCCCCCC)   // light
val PixelGray2  = Color(0xFF888888)   // mid
val PixelGray3  = Color(0xFF444444)   // dark
val PixelGray4  = Color(0xFF1A1A1A)   // near-black card
val PixelGray5  = Color(0xFF2A2A2A)   // card border

// ── Heatmap intensity scale ───────────────────────────────────
val HeatLevel0  = Color(0xFF000000)   // empty
val HeatLevel1  = Color(0xFF2A2A2A)   // low
val HeatLevel2  = Color(0xFF555555)   // medium-low
val HeatLevel3  = Color(0xFF999999)   // medium-high
val HeatLevel4  = Color(0xFFFFFFFF)   // full

// ── Backward-compat aliases (all B&W now) ─────────────────────
val AtmosphericBlack  = PixelBlack
val DeepNavy          = Color(0xFF080808)
val DarkBlueSurface   = PixelGray4
val MidnightCard      = PixelGray4
val CardBorder        = PixelGray5
val CardBorderBright  = PixelGray3

val CyanGlow     = PixelWhite
val SoftIndigo   = PixelWhite
val MutedPurple  = PixelGray1
val ElectricBlue = PixelWhite
val WarmAmber    = PixelGray1
val GlowOrange   = PixelGray1
val SandPixel    = PixelWhite

val SoftWhite    = PixelWhite
val MutedText    = PixelGray2
val DimText      = PixelGray3

val SoftGreen    = PixelWhite
val SoftRed      = PixelGray2
val SoftYellow   = PixelGray1

// ── Category colors (all B&W shades) ──────────────────────────
val CatHealth    = PixelWhite
val CatStudy     = PixelWhite
val CatWork      = PixelGray1
val CatFitness   = PixelWhite
val CatFinance   = PixelGray1
val CatPersonal  = PixelGray1
val CatCreative  = PixelGray1
val CatGeneral   = PixelGray2