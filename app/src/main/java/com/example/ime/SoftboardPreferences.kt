package com.example.ime

import android.content.Context
import android.content.SharedPreferences

enum class SoftboardThemeType(val id: String, val displayName: String, val description: String) {
    NEON_PURPLE("neon_purple", "Neon Purple", "Ultra dark with glowing violet & purple accents"),
    CYBER_BLUE("cyber_blue", "Cyber Blue", "Deep cyber navy with electric cyan highlights"),
    MIDNIGHT_BLACK("midnight_black", "Midnight Black", "Pure OLED black with sleek minimalist contrast"),
    CLEAN_LIGHT("clean_light", "Clean Light", "Crisp pearl white with modern slate typography")
}

enum class KeyboardHeight(val id: String, val displayName: String, val heightDp: Int) {
    COMPACT("compact", "Compact", 220),
    NORMAL("normal", "Normal", 255),
    TALL("tall", "Tall", 290)
}

enum class KeyboardLanguage(val id: String, val displayName: String, val shortLabel: String) {
    ENGLISH_US("en_us", "English (QWERTY)", "EN"),
    SINHALA_WIJESEKARA("si_wijesekara", "Sinhala (Wijesekara)", "සිං"),
    SINHALA_PHONETIC("si_phonetic", "Sinhala (Phonetic / Easy)", "සිං-E")
}

class SoftboardPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("softboard_settings", Context.MODE_PRIVATE)

    var theme: SoftboardThemeType
        get() {
            val id = prefs.getString("theme", SoftboardThemeType.NEON_PURPLE.id)
            return SoftboardThemeType.values().firstOrNull { it.id == id } ?: SoftboardThemeType.NEON_PURPLE
        }
        set(value) = prefs.edit().putString("theme", value.id).apply()

    var height: KeyboardHeight
        get() {
            val id = prefs.getString("height", KeyboardHeight.NORMAL.id)
            return KeyboardHeight.values().firstOrNull { it.id == id } ?: KeyboardHeight.NORMAL
        }
        set(value) = prefs.edit().putString("height", value.id).apply()

    var language: KeyboardLanguage
        get() {
            val id = prefs.getString("language", KeyboardLanguage.ENGLISH_US.id)
            return KeyboardLanguage.values().firstOrNull { it.id == id } ?: KeyboardLanguage.ENGLISH_US
        }
        set(value) = prefs.edit().putString("language", value.id).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var popupPreviewEnabled: Boolean
        get() = prefs.getBoolean("popup_preview_enabled", true)
        set(value) = prefs.edit().putBoolean("popup_preview_enabled", value).apply()

    var showNumberRow: Boolean
        get() = prefs.getBoolean("show_number_row", true)
        set(value) = prefs.edit().putBoolean("show_number_row", value).apply()

    fun getClipboardHistory(): List<String> {
        val raw = prefs.getString("clipboard_history", "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split("###DELIMITER###").filter { it.isNotBlank() }
    }

    fun addClipboardItem(item: String) {
        if (item.isBlank()) return
        val current = getClipboardHistory().toMutableList()
        current.remove(item)
        current.add(0, item)
        val limited = current.take(20)
        prefs.edit().putString("clipboard_history", limited.joinToString("###DELIMITER###")).apply()
    }

    fun clearClipboardHistory() {
        prefs.edit().remove("clipboard_history").apply()
    }
}
