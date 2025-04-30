package com.example.pinchtozoomgesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.example.pinchtozoomgesture.ui.theme.PinchToZoomGestureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinchToZoomGestureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PinchToZoom(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PinchToZoomPreview() {
    PinchToZoom()
}

@Composable
fun PinchToZoom(modifier: Modifier = Modifier) {
    ImageWithPinchZoom(modifier)
}

@Composable
fun ImageWithPinchZoom(modifier: Modifier = Modifier) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }

    Image(painter = painterResource(id = R.drawable.sample_image_dog),
        contentDescription = stringResource(R.string.sample_content_description),
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .clipToBounds()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures(onGesture = { centroid, _, gestureZoom, _ ->
                    val newZoom = (zoom * gestureZoom).coerceAtLeast(1f)
                    offset = offset.calculatePinchOffset(centroid, zoom, newZoom, size)
                    zoom = newZoom

                })
            }

            .graphicsLayer {
                translationX = -offset.x * zoom
                translationY = -offset.y * zoom
                transformOrigin = TransformOrigin(0f, 0f)
                scaleX = zoom; scaleY = zoom
            })
}

fun Offset.calculatePinchOffset(
    centroid: Offset, oldZoom: Float, newZoom: Float, size: IntSize
): Offset {
    // calculates the difference between old zoom and new zoom
    val zoomOffset = centroid / oldZoom - centroid / newZoom
    // adds zoom offset to current offset
    val newOffset = this + zoomOffset
    // calculates maximum x offset
    val maxOffsetX = (size.width / oldZoom) * (oldZoom - 1f)
    // calculates maximum y offset
    val maxOffsetY = (size.height / oldZoom) * (oldZoom - 1f)
    return Offset(
        newOffset.x.coerceIn(0f, maxOffsetX), newOffset.y.coerceIn(0f, maxOffsetY)
    )
}
