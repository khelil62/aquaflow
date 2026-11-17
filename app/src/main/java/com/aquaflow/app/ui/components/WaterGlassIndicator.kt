package com.aquaflow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aquaflow.app.ui.theme.GradientStart
import com.aquaflow.app.ui.theme.GradientEnd
import com.aquaflow.app.ui.theme.NeonCyan
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WaterGlassIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val bubbleAnimation = rememberInfiniteTransition(label = "bubbles")
    val bubbles = remember {
        List(8) {
            Bubble(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 8f + 4f,
                speed = Random.nextFloat() * 0.5f + 0.3f,
                phase = Random.nextFloat() * 6.28f
            )
        }
    }
    
    val time by bubbleAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier.aspectRatio(0.6f)) {
        val width = size.width
        val height = size.height
        
        val glassShape = androidx.compose.ui.graphics.Path().apply {
            moveTo(width * 0.1f, 0f)
            lineTo(width * 0.9f, 0f)
            lineTo(width * 0.85f, height)
            lineTo(width * 0.15f, height)
            close()
          }
        
        drawPath(path = glassShape, color = Color(0x1AFFFFFF), style = Fill)
        
        val waterHeight = height * progress
        val waterTop = height - waterHeight
        
        val waterPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(width * 0.1f, waterTop)
            var x = width * 0.1f
            while (x <= width * 0.9f) {
                  val waveY = waterTop + sin((x / width * 4 + time).toDouble()).toFloat() * 8f
                  lineTo(x, waveY)
                 x += 2f
            }
            lineTo(width * 0.9f, waterTop)
            lineTo(width * 0.85f, height)
            lineTo(width * 0.15f, height)
            close()
        }
        
        drawPath(
            path = waterPath,
            brush = Brush.verticalGradient(
                colors = listOf(GradientStart, GradientEnd),
                startY = waterTop,
                endY = height
            ),
            style = Fill
        )
        
        bubbles.forEach { bubble ->
            val bubbleY = waterTop + (waterHeight * ((bubble.y + time * bubble.speed / 6.28f) % 1f))
            val bubbleX = width * (0.15f + bubble.x * 0.7f) + sin(time + bubble.phase).toFloat() * 10f
            
            if (bubbleY > waterTop && bubbleY < height) {
                drawCircle(
                    color = NeonCyan.copy(alpha = 0.6f),
                    radius = bubble.radius,
                    center = Offset(bubbleX, bubbleY)
                )
            }
        }
        
        drawPath(path = glassShape, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 2f))
    }
}

private data class Bubble(val x: Float, val y: Float, val radius: Float, val speed: Float, val phase: Float)
