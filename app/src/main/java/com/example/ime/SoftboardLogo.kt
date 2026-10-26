package com.example.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SoftboardKeyLogo(
    size: Dp = 54.dp,
    modifier: Modifier = Modifier
) {
    // 3D-styled Keyboard Key Cap with neon purple/blue glow
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(size * 0.28f),
                ambientColor = Color(0xFF9333EA),
                spotColor = Color(0xFF38BDF8)
            )
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2E1B5B),
                        Color(0xFF140D2B),
                        Color(0xFF090614)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFC084FC),
                        Color(0xFF38BDF8),
                        Color(0xFF9333EA)
                    )
                ),
                shape = RoundedCornerShape(size * 0.28f)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner key surface top bevel
        Box(
            modifier = Modifier
                .size(size * 0.8f)
                .clip(RoundedCornerShape(size * 0.22f))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x33A855F7),
                            Color(0x1106B6D4),
                            Color(0x00000000)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                fontSize = (size.value * 0.52f).sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFFF8FAFC)
            )
        }
    }
}

@Composable
fun SoftboardHeaderBranding(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SoftboardKeyLogo(size = 48.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Box {
            Text(
                text = "SOFTBOARD",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = Color.White
            )
        }
    }
}
