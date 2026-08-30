package com.example.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class TtsState {
    IDLE,
    PLAYING,
    PAUSED
}

class ArabicTtsManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _ttsState = MutableStateFlow(TtsState.IDLE)
    val ttsState: StateFlow<TtsState> = _ttsState.asStateFlow()

    private val _currentChapterId = MutableStateFlow<Int?>(null)
    val currentChapterId: StateFlow<Int?> = _currentChapterId.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private var currentTextToSpeak: String = ""

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                val arabicLocale = Locale("ar")
                val result = tts?.setLanguage(arabicLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.getDefault()
                }
                tts?.setSpeechRate(_speechRate.value)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _ttsState.value = TtsState.PLAYING
                    }

                    override fun onDone(utteranceId: String?) {
                        _ttsState.value = TtsState.IDLE
                    }

                    override fun onError(utteranceId: String?) {
                        _ttsState.value = TtsState.IDLE
                    }
                })
            }
        }
    }

    fun speakChapter(chapterId: Int, text: String) {
        if (!isInitialized || tts == null) return
        currentTextToSpeak = text
        _currentChapterId.value = chapterId
        tts?.stop()
        tts?.setSpeechRate(_speechRate.value)
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "chapter_$chapterId"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "chapter_$chapterId")
        _ttsState.value = TtsState.PLAYING
    }

    fun pauseOrResume(chapterId: Int, text: String) {
        if (_ttsState.value == TtsState.PLAYING) {
            tts?.stop()
            _ttsState.value = TtsState.PAUSED
        } else if (_ttsState.value == TtsState.PAUSED) {
            speakChapter(chapterId, text.ifEmpty { currentTextToSpeak })
        } else {
            speakChapter(chapterId, text)
        }
    }

    fun stop() {
        tts?.stop()
        _ttsState.value = TtsState.IDLE
        _currentChapterId.value = null
    }

    fun setSpeed(rate: Float) {
        _speechRate.value = rate
        tts?.setSpeechRate(rate)
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
