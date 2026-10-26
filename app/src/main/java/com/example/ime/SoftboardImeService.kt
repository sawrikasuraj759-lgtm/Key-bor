package com.example.ime

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity

class SoftboardImeService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var prefs: SoftboardPreferences
    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator
    private lateinit var clipboardManager: ClipboardManager

    private var activeTheme by mutableStateOf(SoftboardThemes.NeonPurple)
    private var keyboardHeightDp by mutableStateOf(255)
    private var showNumberRow by mutableStateOf(true)
    private var soundEnabled by mutableStateOf(true)
    private var vibrationEnabled by mutableStateOf(true)
    private var popupPreviewEnabled by mutableStateOf(true)
    private var activeLanguage by mutableStateOf(KeyboardLanguage.ENGLISH_US)
    private var clipboardList by mutableStateOf<List<String>>(emptyList())

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        prefs = SoftboardPreferences(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        reloadPreferences()
    }

    private fun reloadPreferences() {
        activeTheme = SoftboardThemes.getTheme(prefs.theme)
        keyboardHeightDp = prefs.height.heightDp
        showNumberRow = prefs.showNumberRow
        soundEnabled = prefs.soundEnabled
        vibrationEnabled = prefs.vibrationEnabled
        popupPreviewEnabled = prefs.popupPreviewEnabled
        activeLanguage = prefs.language
        clipboardList = prefs.getClipboardHistory()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        reloadPreferences()
        captureCurrentClipboard()
    }

    private fun captureCurrentClipboard() {
        try {
            if (clipboardManager.hasPrimaryClip()) {
                val clip = clipboardManager.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0)?.coerceToText(this)?.toString()
                    if (!text.isNullOrBlank()) {
                        prefs.addClipboardItem(text)
                        clipboardList = prefs.getClipboardHistory()
                    }
                }
            }
        } catch (_: Exception) {}
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        reloadPreferences()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setViewTreeLifecycleOwner(this@SoftboardImeService)
            setViewTreeViewModelStoreOwner(this@SoftboardImeService)
            setViewTreeSavedStateRegistryOwner(this@SoftboardImeService)

            setContent {
                SoftboardKeyboardView(
                    theme = activeTheme,
                    heightDp = keyboardHeightDp,
                    showNumberRow = showNumberRow,
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled,
                    popupPreviewEnabled = popupPreviewEnabled,
                    initialLanguage = activeLanguage,
                    clipboardItems = clipboardList,
                    onCommitText = { text ->
                        currentInputConnection?.commitText(text, 1)
                    },
                    onDelete = {
                        val ic = currentInputConnection
                        val selected = ic?.getSelectedText(0)
                        if (!selected.isNullOrEmpty()) {
                            ic.commitText("", 1)
                        } else {
                            ic?.deleteSurroundingText(1, 0)
                        }
                    },
                    onEnter = {
                        handleEnterKey()
                    },
                    onOpenSettings = {
                        val intent = Intent(this@SoftboardImeService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        startActivity(intent)
                    },
                    onSwitchIme = {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            try {
                                switchToNextInputMethod(false)
                            } catch (_: Exception) {
                                imm.showInputMethodPicker()
                            }
                        } else {
                            imm.showInputMethodPicker()
                        }
                    },
                    onToggleTheme = {
                        val allThemes = SoftboardThemeType.values()
                        val nextIndex = (prefs.theme.ordinal + 1) % allThemes.size
                        prefs.theme = allThemes[nextIndex]
                        activeTheme = SoftboardThemes.getTheme(prefs.theme)
                    },
                    onFeedback = { sound, vibrate ->
                        triggerFeedback(sound, vibrate)
                    }
                )
            }
        }

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        return composeView
    }

    private fun handleEnterKey() {
        val editorInfo = currentInputEditorInfo
        val ic = currentInputConnection
        val action = editorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic?.performEditorAction(action)
        } else {
            ic?.commitText("\n", 1)
        }
    }

    private fun triggerFeedback(sound: Boolean, vibrate: Boolean) {
        if (sound) {
            try {
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.4f)
            } catch (_: Exception) {}
        }
        if (vibrate) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(18)
                }
            } catch (_: Exception) {}
        }
    }
}
