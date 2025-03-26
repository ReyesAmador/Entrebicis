package cat.copernic.p3grup1.entrebicis.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cat.copernic.p3grup1.entrebicis.R

val Boogaloo = FontFamily(
    Font(R.font.boogaloo_regular)
)

val Avenir = FontFamily(
    Font(R.font.avenir_lt_std_35_light)
)

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge  = TextStyle( //H1
        fontFamily = Boogaloo,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    ),
    headlineMedium = TextStyle( // H2
        fontFamily = Boogaloo,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    headlineSmall = TextStyle( // H3
        fontFamily = Boogaloo,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    bodyMedium = TextStyle( // Paragraph
        fontFamily = Avenir,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle( // Botón
        fontFamily = Avenir,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
)