package com.example.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.model.BookRepository
import com.example.model.Chapter
import com.example.tts.ArabicTtsManager
import com.example.tts.TtsState
import com.example.ui.components.AudioPlayerBar
import com.example.ui.components.AuthorInfoDialog
import com.example.ui.components.BookPageFrame
import com.example.ui.components.BookmarksSheet
import com.example.ui.components.ReadingSettingsDialog
import com.example.ui.components.SearchDialog
import com.example.ui.components.TableOfContentsSheet
import com.example.ui.theme.getThemeColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    prefsManager: PreferencesManager,
    ttsManager: ArabicTtsManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val settings by prefsManager.settings.collectAsState()
    val bookmarks by prefsManager.bookmarks.collectAsState()
    val lastChapterId by prefsManager.lastChapterId.collectAsState()

    val ttsState by ttsManager.ttsState.collectAsState()
    val currentTtsChapterId by ttsManager.currentChapterId.collectAsState()
    val speechRate by ttsManager.speechRate.collectAsState()

    val chapters = BookRepository.chapters
    val colors = getThemeColors(settings.readerTheme)

    // Initial pager page from saved state
    val initialPage = (lastChapterId - 1).coerceIn(0, chapters.size - 1)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { chapters.size })
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage)

    // Sync current page to preferences
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            val chId = chapters[pageIndex].id
            prefsManager.saveLastChapterId(chId)
        }
    }

    // Keep screen on behavior
    DisposableEffect(settings.keepScreenOn) {
        val window = (context as? Activity)?.window
        if (settings.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Dialog & Sheet States
    var showTocSheet by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAuthorDialog by remember { mutableStateOf(false) }

    val currentPageIndex = if (settings.continuousScroll) {
        listState.firstVisibleItemIndex.coerceIn(0, chapters.size - 1)
    } else {
        pagerState.currentPage
    }
    val currentChapter = chapters.getOrNull(currentPageIndex) ?: chapters.first()

    // Force RTL layout direction for Arabic
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ظلم الشعوب المسلمة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "صفحة ${currentChapter.pageNumber} من ${chapters.size}",
                                fontSize = 11.sp,
                                color = colors.subtext
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { showTocSheet = true },
                            modifier = Modifier.testTag("btn_open_toc")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "الفهرس",
                                tint = colors.primary
                            )
                        }
                    },
                    actions = {
                        // Search
                        IconButton(
                            onClick = { showSearchDialog = true },
                            modifier = Modifier.testTag("btn_open_search")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = colors.primary
                            )
                        }

                        // Bookmarks
                        IconButton(
                            onClick = { showBookmarksSheet = true },
                            modifier = Modifier.testTag("btn_open_bookmarks")
                        ) {
                            if (bookmarks.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = colors.accent,
                                            contentColor = colors.surface
                                        ) {
                                            Text("${bookmarks.size}")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmarks,
                                        contentDescription = "الإشارات المرجعية",
                                        tint = colors.primary
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Bookmarks,
                                    contentDescription = "الإشارات المرجعية",
                                    tint = colors.primary
                                )
                            }
                        }

                        // Reading Settings
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.testTag("btn_open_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "تنسيق القراءة",
                                tint = colors.primary
                            )
                        }

                        // Author & Info
                        IconButton(
                            onClick = { showAuthorDialog = true },
                            modifier = Modifier.testTag("btn_open_author_info")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "عن المؤلف",
                                tint = colors.accent
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.surface,
                        titleContentColor = colors.primary
                    )
                )
            },
            bottomBar = {
                Column(modifier = Modifier.fillMaxWidth().background(colors.surface)) {
                    // Audio Player floating bar when TTS is playing/paused
                    AudioPlayerBar(
                        currentChapter = chapters.find { it.id == currentTtsChapterId } ?: currentChapter,
                        ttsState = ttsState,
                        speechRate = speechRate,
                        colors = colors,
                        onPlayPause = {
                            val activeCh = chapters.find { it.id == currentTtsChapterId } ?: currentChapter
                            ttsManager.pauseOrResume(activeCh.id, activeCh.content)
                        },
                        onStop = { ttsManager.stop() },
                        onNextChapter = {
                            val nextId = (currentChapter.id % chapters.size) + 1
                            val nextCh = chapters.find { it.id == nextId } ?: chapters.first()
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(nextCh.pageNumber - 1)
                            }
                            ttsManager.speakChapter(nextCh.id, nextCh.content)
                        },
                        onPreviousChapter = {
                            val prevId = if (currentChapter.id > 1) currentChapter.id - 1 else chapters.size
                            val prevCh = chapters.find { it.id == prevId } ?: chapters.first()
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(prevCh.pageNumber - 1)
                            }
                            ttsManager.speakChapter(prevCh.id, prevCh.content)
                        },
                        onSetSpeed = { ttsManager.setSpeed(it) }
                    )

                    // Bottom Navigation / Quick Page Slider Bar
                    Surface(
                        color = colors.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Previous Page Button
                            IconButton(
                                onClick = {
                                    if (currentPageIndex > 0) {
                                        coroutineScope.launch {
                                            if (settings.continuousScroll) {
                                                listState.animateScrollToItem(currentPageIndex - 1)
                                            } else {
                                                pagerState.animateScrollToPage(currentPageIndex - 1)
                                            }
                                        }
                                    }
                                },
                                enabled = currentPageIndex > 0,
                                modifier = Modifier.testTag("btn_prev_page")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "الصفحة السابقة",
                                    tint = if (currentPageIndex > 0) colors.primary else colors.border
                                )
                            }

                            // Slider to leap across pages
                            Slider(
                                value = (currentPageIndex + 1).toFloat(),
                                onValueChange = { targetPage ->
                                    val page = (targetPage.toInt() - 1).coerceIn(0, chapters.size - 1)
                                    coroutineScope.launch {
                                        if (settings.continuousScroll) {
                                            listState.scrollToItem(page)
                                        } else {
                                            pagerState.scrollToPage(page)
                                        }
                                    }
                                },
                                valueRange = 1f..chapters.size.toFloat(),
                                steps = chapters.size - 2,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .testTag("page_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accent,
                                    activeTrackColor = colors.primary,
                                    inactiveTrackColor = colors.border
                                )
                            )

                            // Next Page Button
                            IconButton(
                                onClick = {
                                    if (currentPageIndex < chapters.size - 1) {
                                        coroutineScope.launch {
                                            if (settings.continuousScroll) {
                                                listState.animateScrollToItem(currentPageIndex + 1)
                                            } else {
                                                pagerState.animateScrollToPage(currentPageIndex + 1)
                                            }
                                        }
                                    }
                                },
                                enabled = currentPageIndex < chapters.size - 1,
                                modifier = Modifier.testTag("btn_next_page")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "الصفحة التالية",
                                    tint = if (currentPageIndex < chapters.size - 1) colors.primary else colors.border
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colors.background)
            ) {
                if (settings.continuousScroll) {
                    // Continuous Vertical Scroll List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(chapters, key = { it.id }) { chapter ->
                            val isBookmarked = bookmarks.any { it.chapterId == chapter.id }
                            val isCurrentTts = currentTtsChapterId == chapter.id
                            BookPageFrame(
                                chapter = chapter,
                                settings = settings,
                                colors = colors,
                                isBookmarked = isBookmarked,
                                ttsState = ttsState,
                                isCurrentTtsChapter = isCurrentTts,
                                onToggleBookmark = {
                                    prefsManager.toggleBookmark(chapter.id, chapter.pageNumber, chapter.title)
                                },
                                onToggleTts = {
                                    ttsManager.pauseOrResume(chapter.id, chapter.content)
                                },
                                onOpenChapter = { targetId ->
                                    coroutineScope.launch {
                                        val idx = (targetId - 1).coerceIn(0, chapters.size - 1)
                                        listState.animateScrollToItem(idx)
                                    }
                                },
                                onShowAuthorInfo = { showAuthorDialog = true },
                                modifier = Modifier.fillMaxWidth().height(680.dp)
                            )
                        }
                    }
                } else {
                    // Page by Page Horizontal Pager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize().testTag("horizontal_pager")
                    ) { pageIndex ->
                        val chapter = chapters[pageIndex]
                        val isBookmarked = bookmarks.any { it.chapterId == chapter.id }
                        val isCurrentTts = currentTtsChapterId == chapter.id

                        BookPageFrame(
                            chapter = chapter,
                            settings = settings,
                            colors = colors,
                            isBookmarked = isBookmarked,
                            ttsState = ttsState,
                            isCurrentTtsChapter = isCurrentTts,
                            onToggleBookmark = {
                                prefsManager.toggleBookmark(chapter.id, chapter.pageNumber, chapter.title)
                            },
                            onToggleTts = {
                                ttsManager.pauseOrResume(chapter.id, chapter.content)
                            },
                            onOpenChapter = { targetId ->
                                coroutineScope.launch {
                                    val idx = (targetId - 1).coerceIn(0, chapters.size - 1)
                                    pagerState.animateScrollToPage(idx)
                                }
                            },
                            onShowAuthorInfo = { showAuthorDialog = true }
                        )
                    }
                }
            }
        }

        // Sheets & Dialogs
        if (showTocSheet) {
            TableOfContentsSheet(
                chapters = chapters,
                currentChapterId = currentChapter.id,
                bookmarks = bookmarks,
                colors = colors,
                onSelectChapter = { selectedId ->
                    coroutineScope.launch {
                        val idx = (selectedId - 1).coerceIn(0, chapters.size - 1)
                        if (settings.continuousScroll) {
                            listState.animateScrollToItem(idx)
                        } else {
                            pagerState.animateScrollToPage(idx)
                        }
                    }
                },
                onDismiss = { showTocSheet = false }
            )
        }

        if (showBookmarksSheet) {
            BookmarksSheet(
                bookmarks = bookmarks,
                colors = colors,
                onSelectBookmark = { chapterId ->
                    coroutineScope.launch {
                        val idx = (chapterId - 1).coerceIn(0, chapters.size - 1)
                        if (settings.continuousScroll) {
                            listState.animateScrollToItem(idx)
                        } else {
                            pagerState.animateScrollToPage(idx)
                        }
                    }
                },
                onDeleteBookmark = { chapterId ->
                    val bm = bookmarks.find { it.chapterId == chapterId }
                    if (bm != null) {
                        prefsManager.toggleBookmark(bm.chapterId, bm.pageNumber, bm.title)
                    }
                },
                onUpdateNote = { chapterId, note ->
                    prefsManager.updateBookmarkNote(chapterId, note)
                },
                onDismiss = { showBookmarksSheet = false }
            )
        }

        if (showSearchDialog) {
            SearchDialog(
                chapters = chapters,
                colors = colors,
                onSelectChapter = { chapterId ->
                    coroutineScope.launch {
                        val idx = (chapterId - 1).coerceIn(0, chapters.size - 1)
                        if (settings.continuousScroll) {
                            listState.animateScrollToItem(idx)
                        } else {
                            pagerState.animateScrollToPage(idx)
                        }
                    }
                },
                onDismiss = { showSearchDialog = false }
            )
        }

        if (showSettingsDialog) {
            ReadingSettingsDialog(
                settings = settings,
                colors = colors,
                onUpdateSettings = { newSettings ->
                    prefsManager.updateSettings(newSettings)
                },
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showAuthorDialog) {
            AuthorInfoDialog(
                colors = colors,
                onDismiss = { showAuthorDialog = false }
            )
        }
    }
}
