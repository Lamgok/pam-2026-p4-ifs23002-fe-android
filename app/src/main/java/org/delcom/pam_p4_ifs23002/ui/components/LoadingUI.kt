package org.delcom.pam_p4_ifs23002.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RippleLoading(
    modifier: Modifier = Modifier,
    // Menggunakan warna primary dari tema agar sinkron
    color: Color = MaterialTheme.colorScheme.primary, 
    iconSize: Dp = 48.dp,
    circleSize: Dp = 40.dp,
    maxSize: Dp = 120.dp,
    animationDuration: Int = 1200
) {
    val density = LocalDensity.current
    val circleSizePx = with(density) { circleSize.toPx() }
    val maxSizePx = with(density) { maxSize.toPx() }

    Box(
        modifier = modifier.size(maxSize + 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center Icon - Melambangkan Keragaman
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(2.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Diversity3,
                contentDescription = "Loading",
                modifier = Modifier.size(iconSize),
                tint = color
            )
        }

        // Ripple Effect
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "ripple")

            val size by infiniteTransition.animateFloat(
                initialValue = circleSizePx,
                targetValue = maxSizePx,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        easing = LinearEasing
                    ),
                    initialStartOffset = StartOffset((animationDuration / 3 * index))
                ), label = "size"
            )

            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        easing = LinearEasing
                    ),
                    initialStartOffset = StartOffset((animationDuration / 3 * index))
                ), label = "alpha"
            )

            Box(
                modifier = Modifier
                    .size(size.toDp())
                    .border(
                        width = 2.dp,
                        color = color.copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun LoadingUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Menggunakan background dari tema agar konsisten (Warm Cream)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)), 
        contentAlignment = Alignment.Center
    ) {
        RippleLoading()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingUI() {
    MaterialTheme {
        LoadingUI()
    }
}
