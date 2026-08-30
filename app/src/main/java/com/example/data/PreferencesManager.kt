package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Bookmark
import com.example.model.ReaderSettings
import com.example.model.ReaderTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("zulm_book_prefs", Context.MODE_PRIVATE)

    private val _lastChapterId = MutableStateFlow(prefs.getInt("last_chapter_id", 1))
    val lastChapterId: StateFlow<Int> = _lastChapterId.asStateFlow()

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<ReaderSettings> = _settings.asStateFlow()

    private val _bookmarks = MutableStateFlow(loadBookmarks())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private fun loadSettings(): ReaderSettings {
        val fontSize = prefs.getFloat("font_size", 20f)
        val lineSpacing = prefs.getFloat("line_spacing", 1.6f)
        val themeStr = prefs.getString("reader_theme", ReaderTheme.PARCHMENT.name) ?: ReaderTheme.PARCHMENT.name
        val theme = try {
            ReaderTheme.valueOf(themeStr)
        } catch (e: Exception) {
            ReaderTheme.PARCHMENT
        }
        val continuousScroll = prefs.getBoolean("continuous_scroll", false)
        val keepScreenOn = prefs.getBoolean("keep_screen_on", true)

        return ReaderSettings(
            fontSizeSp = fontSize,
            lineSpacingMultiplier = lineSpacing,
            readerTheme = theme,
            continuousScroll = continuousScroll,
            keepScreenOn = keepScreenOn
        )
    }

    private fun loadBookmarks(): List<Bookmark> {
        val raw = prefs.getString("bookmarks_json", "[]") ?: "[]"
        val list = mutableListOf<Bookmark>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Bookmark(
                        chapterId = obj.getInt("chapterId"),
                        pageNumber = obj.getInt("pageNumber"),
                        title = obj.getString("title"),
                        note = obj.optString("note", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveLastChapterId(chapterId: Int) {
        _lastChapterId.value = chapterId
        prefs.edit().putInt("last_chapter_id", chapterId).apply()
    }

    fun updateSettings(newSettings: ReaderSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putFloat("font_size", newSettings.fontSizeSp)
            .putFloat("line_spacing", newSettings.lineSpacingMultiplier)
            .putString("reader_theme", newSettings.readerTheme.name)
            .putBoolean("continuous_scroll", newSettings.continuousScroll)
            .putBoolean("keep_screen_on", newSettings.keepScreenOn)
            .apply()
    }

    fun toggleBookmark(chapterId: Int, pageNumber: Int, title: String, note: String = ""): Boolean {
        val current = _bookmarks.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.chapterId == chapterId }
        val isAdded: Boolean
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
            isAdded = false
        } else {
            current.add(0, Bookmark(chapterId, pageNumber, title, note, System.currentTimeMillis()))
            isAdded = true
        }
        _bookmarks.value = current
        saveBookmarksToPrefs(current)
        return isAdded
    }

    fun updateBookmarkNote(chapterId: Int, newNote: String) {
        val current = _bookmarks.value.map {
            if (it.chapterId == chapterId) it.copy(note = newNote) else it
        }
        _bookmarks.value = current
        saveBookmarksToPrefs(current)
    }

    private fun saveBookmarksToPrefs(list: List<Bookmark>) {
        val arr = JSONArray()
        for (b in list) {
            val obj = JSONObject().apply {
                put("chapterId", b.chapterId)
                put("pageNumber", b.pageNumber)
                put("title", b.title)
                put("note", b.note)
                put("timestamp", b.timestamp)
            }
            arr.put(obj)
        }
        prefs.edit().putString("bookmarks_json", arr.toString()).apply()
    }
}
