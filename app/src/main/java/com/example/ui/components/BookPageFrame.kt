package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookRepository
import com.example.model.Chapter
import com.example.model.ReaderSettings
import com.example.tts.TtsState
import com.example.ui.theme.BookThemeColors

@Composable
fun BookPageFrame(
    chapter: Chapter,
    settings: ReaderSettings,
    colors: BookThemeColors,
    isBookmarked: Boolean,
    ttsState: TtsState,
    isCurrentTtsChapter: Boolean,
    onToggleBookmark: () -> Unit,
    onToggleTts: () -> Unit,
    onOpenChapter: (Int) -> Unit = {},
    onShowAuthorInfo: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Decorative Outer Page Border Frame (matching the book's ornate dashed inner frame)
        Card(
            modifier = Modifier
                .fillMaxSize()
                .testTag("page_card_${chapter.pageNumber}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = BorderStroke(1.5.dp, colors.border),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Book Title & Author)
                BookPageHeader(colors = colors)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = colors.border.copy(alpha = 0.6f),
                    thickness = 1.dp
                )

                // Main Scrollable Page Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (chapter.isIntroOrCover) {
                        CoverPageContent(
                            chapter = chapter,
                            settings = settings,
                            colors = colors,
                            onShowAuthorInfo = onShowAuthorInfo
                        )
                    } else if (chapter.isDedication) {
                        DedicationPageContent(
                            chapter = chapter,
                            settings = settings,
                            colors = colors
                        )
                    } else if (chapter.isIndex) {
                        IndexPageContent(
                            chapter = chapter,
                            settings = settings,
                            colors = colors,
                            onOpenChapter = onOpenChapter
                        )
                    } else {
                        StandardChapterContent(
                            chapter = chapter,
                            settings = settings,
                            colors = colors
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Page Action Toolbar (Bookmark, Listen, Copy, Share)
                    PageActionButtons(
                        chapter = chapter,
                        colors = colors,
                        isBookmarked = isBookmarked,
                        ttsState = ttsState,
                        isCurrentTtsChapter = isCurrentTtsChapter,
                        onToggleBookmark = onToggleBookmark,
                        onToggleTts = onToggleTts,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(chapter.content))
                            Toast.makeText(context, "تم نسخ محتوى الصفحة", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            shareChapter(context, chapter)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Page Number Footer Badge
                PageNumberBadge(
                    pageNumber = chapter.pageNumber,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun BookPageHeader(colors: BookThemeColors) {
    Surface(
        color = colors.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "كتاب ظلم الشعوب المسلمة  |  إعداد وتأليف الدكتور مالك الرميمة",
            color = colors.subtext,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun CoverPageContent(
    chapter: Chapter,
    settings: ReaderSettings,
    colors: BookThemeColors,
    onShowAuthorInfo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Decorated Title Banner
        Surface(
            color = colors.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, colors.accent),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "كـتـاب",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accent,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ظلم الشعوب المسلمة",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "تحت شعار الوطنية",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFC0392B),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "إعداد وتأليف",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.subtext
        )
        Text(
            text = BookRepository.AUTHOR_NAME,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Book Idea Box
        DashedContentBox(
            title = "فكرة الكتاب",
            colors = colors
        ) {
            Text(
                text = BookRepository.BOOK_SUMMARY,
                fontSize = settings.fontSizeSp.sp,
                lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
                color = colors.text,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = colors.surfaceVariant.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "البداية كانت في يوم: ${BookRepository.START_DATE}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "اتصال",
                        tint = colors.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "هاتف المؤلف: ${BookRepository.AUTHOR_PHONE}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                    )
                }
            }
        }
    }
}

@Composable
private fun DedicationPageContent(
    chapter: Chapter,
    settings: ReaderSettings,
    colors: BookThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative Title Frame
        DashedTitleFrame(
            title = "الإهـداء",
            colors = colors
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val lines = listOf(
                    "أهدي كتابي هذا لبحر مداد أقلامي",
                    "أبي الغالي رحمة الله تغشاه",
                    "• • •",
                    "خير من ربّى وعلم وخير من حبّب وعمّم",
                    "سعادتي في قربه وصف لكل خير ومغنم",
                    "• • •",
                    "رحيله عني أوجاع في القلب معظل",
                    "أسكنه الله الفردوس الأعلى بإذنه"
                )

                lines.forEach { line ->
                    if (line == "• • •") {
                        Text(
                            text = "✦  ✦  ✦",
                            fontSize = 14.sp,
                            color = colors.accent,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    } else {
                        Text(
                            text = line,
                            fontSize = (settings.fontSizeSp + 1).sp,
                            lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier * 1.1f).sp,
                            fontWeight = if (line.contains("أبي الغالي")) FontWeight.Bold else FontWeight.Medium,
                            color = if (line.contains("أبي الغالي")) colors.primary else colors.text,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndexPageContent(
    chapter: Chapter,
    settings: ReaderSettings,
    colors: BookThemeColors,
    onOpenChapter: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashedTitleFrame(
            title = "الفـهـرس",
            colors = colors
        )

        Spacer(modifier = Modifier.height(18.dp))

        BookRepository.chapters.filter { !it.isIndex }.forEach { ch ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpenChapter(ch.id) },
                color = colors.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ch.pageNumber}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = ch.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StandardChapterContent(
    chapter: Chapter,
    settings: ReaderSettings,
    colors: BookThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chapter Title Banner in Red-Dashed Box (exact original visual identity)
        DashedTitleFrame(
            title = chapter.title,
            colors = colors
        )

        chapter.subtitle?.let { sub ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sub,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Chapter Text
        Text(
            text = chapter.content,
            fontSize = settings.fontSizeSp.sp,
            lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
            color = colors.text,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun DashedTitleFrame(
    title: String,
    colors: BookThemeColors
) {
    val borderColor = Color(0xFFD32F2F) // Signature red border from the book pages
    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .drawBehind {
                val stroke = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                )
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(14f, 14f),
                    style = stroke
                )
            }
            .background(colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF007791), // Distinctive cyan/blue heading from the book
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DashedContentBox(
    title: String,
    colors: BookThemeColors,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = colors.primary,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            content()
        }
    }
}

@Composable
private fun PageNumberBadge(
    pageNumber: Int,
    colors: BookThemeColors
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .border(1.5.dp, colors.accent, CircleShape)
            .background(colors.surfaceVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$pageNumber",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.primary
        )
    }
}

@Composable
private fun PageActionButtons(
    chapter: Chapter,
    colors: BookThemeColors,
    isBookmarked: Boolean,
    ttsState: TtsState,
    isCurrentTtsChapter: Boolean,
    onToggleBookmark: () -> Unit,
    onToggleTts: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        color = colors.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Audio Narration
            IconButton(
                onClick = onToggleTts,
                modifier = Modifier.testTag("btn_tts_${chapter.pageNumber}")
            ) {
                val isPlayingThis = isCurrentTtsChapter && ttsState == TtsState.PLAYING
                Icon(
                    imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlayingThis) "إيقاف القراءة" else "استماع للصفحة",
                    tint = if (isPlayingThis) Color(0xFFE53935) else colors.primary
                )
            }

            // Bookmark
            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier.testTag("btn_bookmark_${chapter.pageNumber}")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isBookmarked) "إزالة الإشارة" else "إضافة إشارة مرجعية",
                    tint = if (isBookmarked) colors.accent else colors.subtext
                )
            }

            // Copy
            IconButton(
                onClick = onCopy,
                modifier = Modifier.testTag("btn_copy_${chapter.pageNumber}")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "نسخ الصفحة",
                    tint = colors.subtext
                )
            }

            // Share
            IconButton(
                onClick = onShare,
                modifier = Modifier.testTag("btn_share_${chapter.pageNumber}")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "مشاركة الصفحة",
                    tint = colors.subtext
                )
            }
        }
    }
}

private fun shareChapter(context: Context, chapter: Chapter) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            """من كتاب: ${BookRepository.BOOK_TITLE}
تأليف: ${BookRepository.AUTHOR_NAME}

${chapter.title}
---
${chapter.content}
"""
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "مشاركة محتوى الصفحة")
    context.startActivity(shareIntent)
}
