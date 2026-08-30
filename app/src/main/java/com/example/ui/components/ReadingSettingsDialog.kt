package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReaderSettings
import com.example.model.ReaderTheme
import com.example.ui.theme.BookThemeColors
import com.example.ui.theme.getThemeColors

@Composable
fun ReadingSettingsDialog(
    settings: ReaderSettings,
    colors: BookThemeColors,
    onUpdateSettings: (ReaderSettings) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    tint = colors.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "خيارات وتنسيق القراءة",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Font Size Section
                Text(
                    text = "حجم الخط: ${settings.fontSizeSp.toInt()} نقطة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (settings.fontSizeSp > 14f) {
                                onUpdateSettings(settings.copy(fontSizeSp = settings.fontSizeSp - 1f))
                            }
                        },
                        modifier = Modifier.testTag("btn_decrease_font")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "تصغير الخط", tint = colors.primary)
                    }

                    Slider(
                        value = settings.fontSizeSp,
                        onValueChange = { onUpdateSettings(settings.copy(fontSizeSp = it)) },
                        valueRange = 14f..32f,
                        steps = 17,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary,
                            inactiveTrackColor = colors.border
                        )
                    )

                    IconButton(
                        onClick = {
                            if (settings.fontSizeSp < 32f) {
                                onUpdateSettings(settings.copy(fontSizeSp = settings.fontSizeSp + 1f))
                            }
                        },
                        modifier = Modifier.testTag("btn_increase_font")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "تكبير الخط", tint = colors.primary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colors.border)

                // Themes Selector
                Text(
                    text = "مظهر القراءة والخلفية",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ReaderTheme.values().forEach { themeOption ->
                        val themeCol = getThemeColors(themeOption)
                        val isSelected = settings.readerTheme == themeOption

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onUpdateSettings(settings.copy(readerTheme = themeOption)) }
                                .padding(4.dp)
                                .testTag("theme_btn_${themeOption.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(themeCol.background)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) colors.accent else themeCol.border,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "أ",
                                    color = themeCol.text,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = themeOption.title.split(" ").first(),
                                fontSize = 11.sp,
                                color = if (isSelected) colors.primary else colors.subtext,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colors.border)

                // Continuous Scroll Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "التمرير المستمر",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.text
                        )
                        Text(
                            text = "قراءة كل الصفحات في قائمة متصلة",
                            fontSize = 12.sp,
                            color = colors.subtext
                        )
                    }

                    Switch(
                        checked = settings.continuousScroll,
                        onCheckedChange = { onUpdateSettings(settings.copy(continuousScroll = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Keep Screen On Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "إبقاء الشاشة مضاءة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.text
                        )
                        Text(
                            text = "عدم إطفاء الشاشة أثناء القراءة",
                            fontSize = 12.sp,
                            color = colors.subtext
                        )
                    }

                    Switch(
                        checked = settings.keepScreenOn,
                        onCheckedChange = { onUpdateSettings(settings.copy(keepScreenOn = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.primary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                modifier = Modifier.testTag("btn_confirm_settings")
            ) {
                Text("حفظ وإغلاق", color = Color.White)
            }
        },
        containerColor = colors.surface
    )
}
