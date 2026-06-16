package com.example.birukelompok2.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.birukelompok2.ui.theme.*

@Composable
fun BiruCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = ShapeCard,
        colors = CardDefaults.cardColors(containerColor = BiruSurface),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
fun BiruButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !loading,
        shape = ShapeButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = BiruPrimary,
            contentColor = BiruSurface,
            disabledContainerColor = BiruPrimaryLight.copy(alpha = 0.5f),
            disabledContentColor = BiruSurface.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = BiruSurface,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun BiruOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = ShapeButton,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruPrimary),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(BiruPrimary)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun BiruTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        shape = ShapeTextField,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BiruPrimary,
            unfocusedBorderColor = BiruBorder,
            focusedLabelColor = BiruPrimary,
            cursorColor = BiruPrimary
        ),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "approved", "disetujui" -> Triple(BiruSuccessLight, BiruSuccess, "Disetujui")
        "rejected", "ditolak" -> Triple(BiruErrorLight, BiruError, "Ditolak")
        "cancelled", "dibatalkan" -> Triple(BiruWarningLight, BiruWarning, "Dibatalkan")
        "pending", "menunggu" -> Triple(BiruWarningLight, BiruWarning, "Menunggu")
        "tersedia" -> Triple(BiruSuccessLight, BiruSuccess, "Tersedia")
        "maintenance" -> Triple(BiruErrorLight, BiruError, "Maintenance")
        else -> Triple(BiruSurfaceVariant, BiruTextSecondary, status)
    }

    Box(
        modifier = modifier
            .clip(ShapeChip)
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BiruAvatar(
    photoUrl: String,
    name: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val initials = name.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "B" }

    val showInitials = photoUrl.isBlank()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BiruPrimaryLight.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        if (showInitials) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = BiruPrimary,
                fontWeight = FontWeight.Bold
            )
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Foto $name",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size / 3),
                            strokeWidth = 2.dp,
                            color = BiruPrimary
                        )
                    }
                },
                error = {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium,
                        color = BiruPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}

@Composable
fun BiruRemoteImage(
    url: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BiruSurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (url.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = description,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = BiruPrimary
                    )
                },
                error = {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = BiruTextTertiary
                    )
                }
            )
        } else {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = BiruTextTertiary
            )
        }
    }
}

@Composable
fun EmptyState(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeLarge)
            .background(BiruSurfaceVariant)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = BiruTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoadingOverlay(visible: Boolean) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BiruSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = BiruPrimary)
                    Text("Memuat...", style = MaterialTheme.typography.bodyMedium, color = BiruTextSecondary)
                }
            }
        }
    }
}

@Composable
fun MessageBar(
    message: String,
    type: MessageType = MessageType.INFO,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (message.isNotBlank()) {
        val (bgColor, textColor) = when (type) {
            MessageType.SUCCESS -> BiruSuccessLight to BiruSuccess
            MessageType.ERROR -> BiruErrorLight to BiruError
            MessageType.WARNING -> BiruWarningLight to BiruWarning
            MessageType.INFO -> BiruInfoLight to BiruInfo
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(ShapeLarge)
                .background(bgColor)
                .clickable { onDismiss() }
                .padding(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

enum class MessageType { SUCCESS, ERROR, WARNING, INFO }

@Composable
fun SectionHeader(
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BiruTextPrimary
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = BiruTextSecondary
            )
        }
    }
}

@Composable
fun LoadingShimmer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(ShapeLarge)
            .background(BiruSurfaceVariant)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = BiruPrimaryLight
        )
    }
}
