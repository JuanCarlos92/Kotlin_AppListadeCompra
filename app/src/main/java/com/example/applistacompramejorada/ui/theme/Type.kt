package com.example.applistacompramejorada.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.applistacompramejorada.R

//Set of Material typography styles to start with
val itimFamily = FontFamily(
    Font(R.font.itim_regular, FontWeight.Light),
    Font(R.font.itim_regular, FontWeight.Normal),
    Font(R.font.itim_regular, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.itim_regular, FontWeight.Medium),
    Font(R.font.itim_regular, FontWeight.Bold)

)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = itimFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = itimFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    /*
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)