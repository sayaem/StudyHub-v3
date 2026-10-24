package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrandTeal

data class AvatarInfo(
    val id: String,
    val name: String,
    val quote: String,
    val bgCircleColor: Color,
    val skinTone: Color,
    val hairColor: Color,
    val clothingColor: Color,
    val accessoryType: AccessoryType = AccessoryType.NONE
)

enum class AccessoryType {
    NONE, GLASSES, HIJAB, CAP, HEADPHONES, HOODIE
}

val AVATAR_LIST: List<AvatarInfo> = listOf(
    AvatarInfo(
        id = "avatar_1",
        name = "Rafi Rahman",
        quote = "Keep going, future is bright!",
        bgCircleColor = Color(0xFFE0F2FE),
        skinTone = Color(0xFFFBD7B5),
        hairColor = Color(0xFF2C2422),
        clothingColor = Color(0xFF0284C7),
        accessoryType = AccessoryType.HOODIE
    ),
    AvatarInfo(
        id = "avatar_2",
        name = "Nabila Islam",
        quote = "Small steps every single day.",
        bgCircleColor = Color(0xFFFCE7F3),
        skinTone = Color(0xFFFFDFC4),
        hairColor = Color(0xFF1F2937),
        clothingColor = Color(0xFFDB2777),
        accessoryType = AccessoryType.HIJAB
    ),
    AvatarInfo(
        id = "avatar_3",
        name = "Tanvir Ahmed",
        quote = "Master one chapter at a time.",
        bgCircleColor = Color(0xFFDCFCE7),
        skinTone = Color(0xFFE8B896),
        hairColor = Color(0xFF1E1B18),
        clothingColor = Color(0xFF16A34A),
        accessoryType = AccessoryType.GLASSES
    ),
    AvatarInfo(
        id = "avatar_4",
        name = "Sumaiya Akter",
        quote = "Focus on the goal, not the obstacle.",
        bgCircleColor = Color(0xFFEDE9FE),
        skinTone = Color(0xFFFADBC8),
        hairColor = Color(0xFF372E29),
        clothingColor = Color(0xFF7C3AED),
        accessoryType = AccessoryType.NONE
    ),
    AvatarInfo(
        id = "avatar_5",
        name = "Samiul Karim",
        quote = "Consistency beats intensity.",
        bgCircleColor = Color(0xFFFEF3C7),
        skinTone = Color(0xFFDF9B6E),
        hairColor = Color(0xFF1E293B),
        clothingColor = Color(0xFFD97706),
        accessoryType = AccessoryType.CAP
    ),
    AvatarInfo(
        id = "avatar_6",
        name = "Farhana Yasmin",
        quote = "Knowledge is the ultimate superpower.",
        bgCircleColor = Color(0xFFCCFBF1),
        skinTone = Color(0xFFFEE2D5),
        hairColor = Color(0xFF2E1065),
        clothingColor = Color(0xFF0D9488),
        accessoryType = AccessoryType.HIJAB
    ),
    AvatarInfo(
        id = "avatar_7",
        name = "Ayman Sadiq",
        quote = "Hard work never goes to waste.",
        bgCircleColor = Color(0xFFE2E8F0),
        skinTone = Color(0xFFF3C299),
        hairColor = Color(0xFF18181B),
        clothingColor = Color(0xFF2563EB),
        accessoryType = AccessoryType.HEADPHONES
    ),
    AvatarInfo(
        id = "avatar_8",
        name = "Tasnim Zara",
        quote = "Believe in yourself and take action.",
        bgCircleColor = Color(0xFFFFEDD5),
        skinTone = Color(0xFFFFDFC4),
        hairColor = Color(0xFF451A03),
        clothingColor = Color(0xFFEA580C),
        accessoryType = AccessoryType.GLASSES
    ),
    AvatarInfo(
        id = "avatar_9",
        name = "Sadman Sadik",
        quote = "Plan your work, work your plan.",
        bgCircleColor = Color(0xFFCFFAFE),
        skinTone = Color(0xFFE7B99A),
        hairColor = Color(0xFF09090B),
        clothingColor = Color(0xFF0891B2),
        accessoryType = AccessoryType.NONE
    ),
    AvatarInfo(
        id = "avatar_10",
        name = "Anika Tabassum",
        quote = "Today's preparation is tomorrow's triumph.",
        bgCircleColor = Color(0xFFFEE2E2),
        skinTone = Color(0xFFFBE4D8),
        hairColor = Color(0xFF292524),
        clothingColor = Color(0xFFE11D48),
        accessoryType = AccessoryType.NONE
    ),
    AvatarInfo(
        id = "avatar_11",
        name = "Zubair Hasan",
        quote = "Focus sharply and dream big.",
        bgCircleColor = Color(0xFFE0E7FF),
        skinTone = Color(0xFFDF9E77),
        hairColor = Color(0xFF1E293B),
        clothingColor = Color(0xFF4F46E5),
        accessoryType = AccessoryType.CAP
    ),
    AvatarInfo(
        id = "avatar_12",
        name = "Mehnaz Chowdhury",
        quote = "Every study session brings you closer.",
        bgCircleColor = Color(0xFFF3E8FF),
        skinTone = Color(0xFFF8D7C2),
        hairColor = Color(0xFF4A044E),
        clothingColor = Color(0xFF9333EA),
        accessoryType = AccessoryType.HIJAB
    )
)

fun getAvatarInfo(avatarId: String): AvatarInfo {
    return AVATAR_LIST.find { it.id == avatarId } ?: AVATAR_LIST.first()
}

@Composable
fun StudentAvatar(
    avatarId: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val info = getAvatarInfo(avatarId)

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, BrandTeal, CircleShape)
                } else Modifier
            )
            .padding(if (isSelected) 3.dp else 0.dp)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .semantics { contentDescription = "Avatar ${info.name}" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height

            // Background circle
            drawCircle(
                color = info.bgCircleColor,
                radius = canvasW / 2f,
                center = Offset(canvasW / 2f, canvasH / 2f)
            )

            // Shoulders / Shirt
            val shoulderTop = canvasH * 0.65f
            val shoulderPath = Path().apply {
                moveTo(canvasW * 0.15f, canvasH)
                quadraticTo(
                    canvasW * 0.2f, shoulderTop,
                    canvasW * 0.5f, shoulderTop + canvasH * 0.05f
                )
                quadraticTo(
                    canvasW * 0.8f, shoulderTop,
                    canvasW * 0.85f, canvasH
                )
                close()
            }
            drawPath(shoulderPath, color = info.clothingColor)

            // Neck
            val neckW = canvasW * 0.22f
            val neckH = canvasH * 0.18f
            drawRect(
                color = info.skinTone,
                topLeft = Offset((canvasW - neckW) / 2f, canvasH * 0.56f),
                size = Size(neckW, neckH)
            )

            // Hijab background drape if hijab
            if (info.accessoryType == AccessoryType.HIJAB) {
                val hijabPath = Path().apply {
                    moveTo(canvasW * 0.2f, canvasH * 0.45f)
                    quadraticTo(canvasW * 0.15f, canvasH * 0.8f, canvasW * 0.5f, canvasH * 0.88f)
                    quadraticTo(canvasW * 0.85f, canvasH * 0.8f, canvasW * 0.8f, canvasH * 0.45f)
                    close()
                }
                drawPath(hijabPath, color = info.clothingColor)
            }

            // Head / Face
            val headRadius = canvasW * 0.24f
            val headCenter = Offset(canvasW / 2f, canvasH * 0.46f)
            drawCircle(
                color = info.skinTone,
                radius = headRadius,
                center = headCenter
            )

            // Hair / Hijab Wrap
            when (info.accessoryType) {
                AccessoryType.HIJAB -> {
                    val hijabWrap = Path().apply {
                        moveTo(headCenter.x - headRadius * 1.05f, headCenter.y + headRadius * 0.3f)
                        quadraticTo(
                            headCenter.x - headRadius, headCenter.y - headRadius * 1.25f,
                            headCenter.x, headCenter.y - headRadius * 1.3f
                        )
                        quadraticTo(
                            headCenter.x + headRadius, headCenter.y - headRadius * 1.25f,
                            headCenter.x + headRadius * 1.05f, headCenter.y + headRadius * 0.3f
                        )
                        quadraticTo(
                            headCenter.x, headCenter.y - headRadius * 0.6f,
                            headCenter.x - headRadius * 1.05f, headCenter.y + headRadius * 0.3f
                        )
                        close()
                    }
                    drawPath(hijabWrap, color = info.clothingColor)
                }
                AccessoryType.CAP -> {
                    // Hair sides
                    drawCircle(
                        color = info.hairColor,
                        radius = headRadius * 0.95f,
                        center = Offset(headCenter.x, headCenter.y - headRadius * 0.2f)
                    )
                    // Cap dome
                    val capPath = Path().apply {
                        moveTo(headCenter.x - headRadius * 1.1f, headCenter.y - headRadius * 0.2f)
                        quadraticTo(
                            headCenter.x, headCenter.y - headRadius * 1.4f,
                            headCenter.x + headRadius * 1.1f, headCenter.y - headRadius * 0.2f
                        )
                        close()
                    }
                    drawPath(capPath, color = info.clothingColor)
                    // Cap visor
                    val visorPath = Path().apply {
                        moveTo(headCenter.x - headRadius * 1.1f, headCenter.y - headRadius * 0.2f)
                        quadraticTo(
                            headCenter.x + headRadius * 0.2f, headCenter.y - headRadius * 0.45f,
                            headCenter.x + headRadius * 1.35f, headCenter.y - headRadius * 0.1f
                        )
                        quadraticTo(
                            headCenter.x + headRadius * 0.3f, headCenter.y - headRadius * 0.15f,
                            headCenter.x - headRadius * 1.1f, headCenter.y - headRadius * 0.2f
                        )
                        close()
                    }
                    drawPath(visorPath, color = info.clothingColor.copy(alpha = 0.85f))
                }
                else -> {
                    // Standard stylish hair
                    val hairPath = Path().apply {
                        moveTo(headCenter.x - headRadius * 1.05f, headCenter.y)
                        quadraticTo(
                            headCenter.x - headRadius * 1.1f, headCenter.y - headRadius * 1.35f,
                            headCenter.x, headCenter.y - headRadius * 1.3f
                        )
                        quadraticTo(
                            headCenter.x + headRadius * 1.1f, headCenter.y - headRadius * 1.35f,
                            headCenter.x + headRadius * 1.05f, headCenter.y
                        )
                        quadraticTo(
                            headCenter.x + headRadius * 0.5f, headCenter.y - headRadius * 0.5f,
                            headCenter.x, headCenter.y - headRadius * 0.7f
                        )
                        quadraticTo(
                            headCenter.x - headRadius * 0.5f, headCenter.y - headRadius * 0.5f,
                            headCenter.x - headRadius * 1.05f, headCenter.y
                        )
                        close()
                    }
                    drawPath(hairPath, color = info.hairColor)
                }
            }

            // Eyes
            val eyeOffsetY = headCenter.y
            val eyeDist = headRadius * 0.42f
            val eyeRadius = headRadius * 0.13f

            drawCircle(
                color = Color(0xFF1E293B),
                radius = eyeRadius,
                center = Offset(headCenter.x - eyeDist, eyeOffsetY)
            )
            drawCircle(
                color = Color(0xFF1E293B),
                radius = eyeRadius,
                center = Offset(headCenter.x + eyeDist, eyeOffsetY)
            )

            // Eye sparkle
            drawCircle(
                color = Color.White,
                radius = eyeRadius * 0.35f,
                center = Offset(headCenter.x - eyeDist - eyeRadius * 0.25f, eyeOffsetY - eyeRadius * 0.25f)
            )
            drawCircle(
                color = Color.White,
                radius = eyeRadius * 0.35f,
                center = Offset(headCenter.x + eyeDist - eyeRadius * 0.25f, eyeOffsetY - eyeRadius * 0.25f)
            )

            // Glasses
            if (info.accessoryType == AccessoryType.GLASSES) {
                val glassRadius = eyeRadius * 2.1f
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = glassRadius,
                    center = Offset(headCenter.x - eyeDist, eyeOffsetY),
                    style = Stroke(width = canvasW * 0.035f)
                )
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = glassRadius,
                    center = Offset(headCenter.x + eyeDist, eyeOffsetY),
                    style = Stroke(width = canvasW * 0.035f)
                )
                // Bridge
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(headCenter.x - eyeDist + glassRadius, eyeOffsetY),
                    end = Offset(headCenter.x + eyeDist - glassRadius, eyeOffsetY),
                    strokeWidth = canvasW * 0.035f
                )
            }

            // Smile
            val smilePath = Path().apply {
                moveTo(headCenter.x - headRadius * 0.3f, headCenter.y + headRadius * 0.42f)
                quadraticTo(
                    headCenter.x, headCenter.y + headRadius * 0.7f,
                    headCenter.x + headRadius * 0.3f, headCenter.y + headRadius * 0.42f
                )
            }
            drawPath(
                smilePath,
                color = Color(0xFFB45309),
                style = Stroke(width = canvasW * 0.04f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // Blush cheeks
            drawCircle(
                color = Color(0xFFFF9494).copy(alpha = 0.4f),
                radius = headRadius * 0.18f,
                center = Offset(headCenter.x - headRadius * 0.65f, headCenter.y + headRadius * 0.28f)
            )
            drawCircle(
                color = Color(0xFFFF9494).copy(alpha = 0.4f),
                radius = headRadius * 0.18f,
                center = Offset(headCenter.x + headRadius * 0.65f, headCenter.y + headRadius * 0.28f)
            )

            // Headphones band & cups
            if (info.accessoryType == AccessoryType.HEADPHONES) {
                val hpColor = Color(0xFF334155)
                // Arch
                val hpArch = Path().apply {
                    moveTo(headCenter.x - headRadius * 1.15f, headCenter.y)
                    quadraticTo(
                        headCenter.x, headCenter.y - headRadius * 1.55f,
                        headCenter.x + headRadius * 1.15f, headCenter.y
                    )
                }
                drawPath(hpArch, color = hpColor, style = Stroke(width = canvasW * 0.05f))
                // Ear cups
                drawRoundRect(
                    color = hpColor,
                    topLeft = Offset(headCenter.x - headRadius * 1.25f, headCenter.y - headRadius * 0.3f),
                    size = Size(canvasW * 0.09f, canvasH * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = hpColor,
                    topLeft = Offset(headCenter.x + headRadius * 1.16f, headCenter.y - headRadius * 0.3f),
                    size = Size(canvasW * 0.09f, canvasH * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }

        // Selection badge
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.32f)
                    .background(BrandTeal, CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.22f)
                )
            }
        }
    }
}
