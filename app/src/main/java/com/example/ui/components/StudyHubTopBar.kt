package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandDeepTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.TextPrimary

@Composable
fun StudyHubLogoMark(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Book base / wings
        val leftPage = Path().apply {
            moveTo(w * 0.12f, h * 0.72f)
            lineTo(w * 0.48f, h * 0.88f)
            lineTo(w * 0.48f, h * 0.42f)
            lineTo(w * 0.12f, h * 0.32f)
            close()
        }
        drawPath(leftPage, color = BrandTeal)

        val rightPage = Path().apply {
            moveTo(w * 0.88f, h * 0.72f)
            lineTo(w * 0.52f, h * 0.88f)
            lineTo(w * 0.52f, h * 0.42f)
            lineTo(w * 0.88f, h * 0.32f)
            close()
        }
        drawPath(rightPage, color = BrandDeepTeal)

        // Upward dynamic arrow
        val arrow = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.72f, h * 0.38f)
            lineTo(w * 0.58f, h * 0.38f)
            lineTo(w * 0.58f, h * 0.7f)
            lineTo(w * 0.42f, h * 0.7f)
            lineTo(w * 0.42f, h * 0.38f)
            lineTo(w * 0.28f, h * 0.38f)
            close()
        }
        drawPath(arrow, color = Color(0xFF00C6D7))
    }
}

@Composable
fun StudyHubTopBar(
    modifier: Modifier = Modifier,
    unreadNotificationCount: Int = 0,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudyHubLogoMark(size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "StudyHub",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Notification Bell with badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )

                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(9.dp)
                            .background(StatusDanger, CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun StudyHubDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            actions()
        }
    }
}
