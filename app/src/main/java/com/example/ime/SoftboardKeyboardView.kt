package com.example.ime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class KeyboardMode {
    ALPHA,
    SYMBOLS,
    ALT_SYMBOLS,
    EMOJI,
    CLIPBOARD
}

enum class ShiftState {
    OFF,
    SHIFTED,
    CAPS_LOCKED
}

@Composable
fun SoftboardKeyboardView(
    theme: SoftboardThemeColors,
    heightDp: Int,
    showNumberRow: Boolean,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    popupPreviewEnabled: Boolean,
    initialLanguage: KeyboardLanguage,
    clipboardItems: List<String>,
    onCommitText: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onOpenSettings: () -> Unit,
    onSwitchIme: () -> Unit,
    onToggleTheme: () -> Unit,
    onFeedback: (sound: Boolean, vibrate: Boolean) -> Unit
) {
    var mode by remember { mutableStateOf(KeyboardMode.ALPHA) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var language by remember { mutableStateOf(initialLanguage) }
    var activePopupChar by remember { mutableStateOf<String?>(null) }
    var numberRowVisible by remember { mutableStateOf(showNumberRow) }

    fun handleKeyPress(text: String) {
        onFeedback(soundEnabled, vibrationEnabled)
        val charToEmit = if (mode == KeyboardMode.ALPHA && language == KeyboardLanguage.ENGLISH_US) {
            when (shiftState) {
                ShiftState.SHIFTED -> {
                    shiftState = ShiftState.OFF
                    text.uppercase()
                }
                ShiftState.CAPS_LOCKED -> text.uppercase()
                ShiftState.OFF -> text.lowercase()
            }
        } else {
            text
        }
        onCommitText(charToEmit)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .background(theme.background)
            .testTag("softboard_keyboard_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            // --- TOP TOOLBAR & QUICK ACTIONS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(theme.surface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.keyBackground)
                        .border(1.dp, theme.keyBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            onFeedback(soundEnabled, vibrationEnabled)
                            language = when (language) {
                                KeyboardLanguage.ENGLISH_US -> KeyboardLanguage.SINHALA_WIJESEKARA
                                KeyboardLanguage.SINHALA_WIJESEKARA -> KeyboardLanguage.SINHALA_PHONETIC
                                KeyboardLanguage.SINHALA_PHONETIC -> KeyboardLanguage.ENGLISH_US
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = language.shortLabel,
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Number Row Toggle Button
                IconButton(
                    onClick = {
                        onFeedback(soundEnabled, vibrationEnabled)
                        numberRowVisible = !numberRowVisible
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        contentDescription = "Toggle Numbers",
                        tint = if (numberRowVisible) theme.accentColor else theme.secondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Emoji Button
                IconButton(
                    onClick = {
                        onFeedback(soundEnabled, vibrationEnabled)
                        mode = if (mode == KeyboardMode.EMOJI) KeyboardMode.ALPHA else KeyboardMode.EMOJI
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = "Emoji Picker",
                        tint = if (mode == KeyboardMode.EMOJI) theme.accentColor else theme.secondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Clipboard Drawer Button
                IconButton(
                    onClick = {
                        onFeedback(soundEnabled, vibrationEnabled)
                        mode = if (mode == KeyboardMode.CLIPBOARD) KeyboardMode.ALPHA else KeyboardMode.CLIPBOARD
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Clipboard History",
                        tint = if (mode == KeyboardMode.CLIPBOARD) theme.accentColor else theme.secondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Theme Switcher Button
                IconButton(
                    onClick = {
                        onFeedback(soundEnabled, vibrationEnabled)
                        onToggleTheme()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Switch Theme",
                        tint = theme.accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Settings Shortcut
                IconButton(
                    onClick = {
                        onFeedback(soundEnabled, vibrationEnabled)
                        onOpenSettings()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Keyboard Settings",
                        tint = theme.secondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Quick Words / Snippets Scroll
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val snippets = if (language == KeyboardLanguage.ENGLISH_US) {
                        KeyboardLayouts.quickSnippetsEn
                    } else {
                        KeyboardLayouts.quickSnippetsSi
                    }
                    snippets.forEach { snippet ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.keyBackground)
                                .clickable {
                                    handleKeyPress(snippet + " ")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = snippet,
                                color = theme.primaryText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // --- VIEW MODE SWITCHER (EMOJI, CLIPBOARD, OR KEYBOARD) ---
            when (mode) {
                KeyboardMode.EMOJI -> {
                    EmojiPickerView(
                        theme = theme,
                        onEmojiSelected = { emoji ->
                            handleKeyPress(emoji)
                        },
                        onBackToKeyboard = {
                            onFeedback(soundEnabled, vibrationEnabled)
                            mode = KeyboardMode.ALPHA
                        },
                        onDelete = {
                            onFeedback(soundEnabled, vibrationEnabled)
                            onDelete()
                        }
                    )
                }
                KeyboardMode.CLIPBOARD -> {
                    ClipboardDrawerView(
                        theme = theme,
                        items = clipboardItems,
                        onPaste = { text ->
                            handleKeyPress(text)
                        },
                        onBackToKeyboard = {
                            onFeedback(soundEnabled, vibrationEnabled)
                            mode = KeyboardMode.ALPHA
                        }
                    )
                }
                else -> {
                    // --- OPTIONAL NUMBER ROW ---
                    if (numberRowVisible) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            KeyboardLayouts.numberRow.forEach { digit ->
                                KeyButton(
                                    label = digit,
                                    theme = theme,
                                    modifier = Modifier.weight(1f),
                                    onClick = { handleKeyPress(digit) },
                                    onPressStart = { if (popupPreviewEnabled) activePopupChar = digit },
                                    onPressEnd = { activePopupChar = null }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    // --- SINHALA QUICK DIACRITICS BAR (IF SINHALA ACTIVE) ---
                    if (language != KeyboardLanguage.ENGLISH_US && mode == KeyboardMode.ALPHA) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            KeyboardLayouts.sinhalaDiacritics.forEach { diacritic ->
                                Box(
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(theme.specialKeyBackground)
                                        .border(1.dp, theme.keyBorder, RoundedCornerShape(6.dp))
                                        .clickable { handleKeyPress(diacritic) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diacritic,
                                        color = theme.accentColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    // --- MAIN KEYBOARD ROWS ---
                    val rows = when (mode) {
                        KeyboardMode.SYMBOLS -> listOf(
                            KeyboardLayouts.symbolsRow1,
                            KeyboardLayouts.symbolsRow2,
                            KeyboardLayouts.symbolsRow3
                        )
                        KeyboardMode.ALT_SYMBOLS -> listOf(
                            KeyboardLayouts.altSymbolsRow1,
                            KeyboardLayouts.altSymbolsRow2,
                            KeyboardLayouts.altSymbolsRow3
                        )
                        else -> {
                            when (language) {
                                KeyboardLanguage.ENGLISH_US -> listOf(
                                    KeyboardLayouts.qwertyRow1,
                                    KeyboardLayouts.qwertyRow2,
                                    KeyboardLayouts.qwertyRow3
                                )
                                KeyboardLanguage.SINHALA_WIJESEKARA -> {
                                    if (shiftState != ShiftState.OFF) {
                                        listOf(
                                            KeyboardLayouts.wijesekaraShiftRow1,
                                            KeyboardLayouts.wijesekaraShiftRow2,
                                            KeyboardLayouts.wijesekaraShiftRow3
                                        )
                                    } else {
                                        listOf(
                                            KeyboardLayouts.wijesekaraNormalRow1,
                                            KeyboardLayouts.wijesekaraNormalRow2,
                                            KeyboardLayouts.wijesekaraNormalRow3
                                        )
                                    }
                                }
                                KeyboardLanguage.SINHALA_PHONETIC -> listOf(
                                    KeyboardLayouts.phoneticRow1,
                                    KeyboardLayouts.phoneticRow2,
                                    KeyboardLayouts.phoneticRow3
                                )
                            }
                        }
                    }

                    // Row 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        rows[0].forEach { char ->
                            val displayChar = if (mode == KeyboardMode.ALPHA && language == KeyboardLanguage.ENGLISH_US) {
                                if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                            } else char
                            KeyButton(
                                label = displayChar,
                                theme = theme,
                                modifier = Modifier.weight(1f),
                                onClick = { handleKeyPress(displayChar) },
                                onPressStart = { if (popupPreviewEnabled) activePopupChar = displayChar },
                                onPressEnd = { activePopupChar = null }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Row 2 (with small side padding for visual stagger in QWERTY)
                    val row2Padding = if (mode == KeyboardMode.ALPHA && language == KeyboardLanguage.ENGLISH_US) 14.dp else 0.dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = row2Padding),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        rows[1].forEach { char ->
                            val displayChar = if (mode == KeyboardMode.ALPHA && language == KeyboardLanguage.ENGLISH_US) {
                                if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                            } else char
                            KeyButton(
                                label = displayChar,
                                theme = theme,
                                modifier = Modifier.weight(1f),
                                onClick = { handleKeyPress(displayChar) },
                                onPressStart = { if (popupPreviewEnabled) activePopupChar = displayChar },
                                onPressEnd = { activePopupChar = null }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Row 3 (with Shift on Left and Backspace on Right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // Shift / Alt-Symbols switch key on left
                        Box(
                            modifier = Modifier
                                .weight(1.35f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (shiftState != ShiftState.OFF) theme.accentColor.copy(alpha = 0.25f)
                                    else theme.specialKeyBackground
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (shiftState != ShiftState.OFF) theme.accentColor else theme.keyBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onFeedback(soundEnabled, vibrationEnabled)
                                    if (mode == KeyboardMode.SYMBOLS) {
                                        mode = KeyboardMode.ALT_SYMBOLS
                                    } else if (mode == KeyboardMode.ALT_SYMBOLS) {
                                        mode = KeyboardMode.SYMBOLS
                                    } else {
                                        shiftState = when (shiftState) {
                                            ShiftState.OFF -> ShiftState.SHIFTED
                                            ShiftState.SHIFTED -> ShiftState.CAPS_LOCKED
                                            ShiftState.CAPS_LOCKED -> ShiftState.OFF
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (mode == KeyboardMode.SYMBOLS) {
                                Text(
                                    text = "=\\<",
                                    color = theme.accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (mode == KeyboardMode.ALT_SYMBOLS) {
                                Text(
                                    text = "?123",
                                    color = theme.accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Shift",
                                    tint = when (shiftState) {
                                        ShiftState.CAPS_LOCKED -> theme.accentColor
                                        ShiftState.SHIFTED -> theme.primaryText
                                        ShiftState.OFF -> theme.secondaryText
                                    },
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        // Middle Characters
                        rows[2].forEach { char ->
                            val displayChar = if (mode == KeyboardMode.ALPHA && language == KeyboardLanguage.ENGLISH_US) {
                                if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                            } else char
                            KeyButton(
                                label = displayChar,
                                theme = theme,
                                modifier = Modifier.weight(1f),
                                onClick = { handleKeyPress(displayChar) },
                                onPressStart = { if (popupPreviewEnabled) activePopupChar = displayChar },
                                onPressEnd = { activePopupChar = null }
                            )
                        }

                        // Backspace Key on right
                        Box(
                            modifier = Modifier
                                .weight(1.35f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.specialKeyBackground)
                                .border(1.dp, theme.keyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    onFeedback(soundEnabled, vibrationEnabled)
                                    onDelete()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = theme.accentColor,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Row 4 (Bottom Bar: Mode switch, Comma, Space, Period, Enter)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // ?123 / ABC Switch Key
                        Box(
                            modifier = Modifier
                                .weight(1.35f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.specialKeyBackground)
                                .border(1.dp, theme.keyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    onFeedback(soundEnabled, vibrationEnabled)
                                    mode = if (mode == KeyboardMode.ALPHA) KeyboardMode.SYMBOLS else KeyboardMode.ALPHA
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (mode == KeyboardMode.ALPHA) "?123" else "ABC",
                                color = theme.accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // IME System Switcher (Globe)
                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.specialKeyBackground)
                                .border(1.dp, theme.keyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    onFeedback(soundEnabled, vibrationEnabled)
                                    onSwitchIme()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Next Keyboard",
                                tint = theme.secondaryText,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Comma Key
                        KeyButton(
                            label = ",",
                            theme = theme,
                            modifier = Modifier.weight(0.9f),
                            onClick = { handleKeyPress(",") }
                        )

                        // SPACEBAR with "SOFTBOARD" branding
                        Box(
                            modifier = Modifier
                                .weight(4.0f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.keyBackground)
                                .border(1.dp, theme.keyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    handleKeyPress(" ")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SOFTBOARD",
                                color = theme.secondaryText.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp
                            )
                        }

                        // Period Key
                        KeyButton(
                            label = ".",
                            theme = theme,
                            modifier = Modifier.weight(0.9f),
                            onClick = { handleKeyPress(".") }
                        )

                        // ENTER / RETURN Key
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.returnKeyBackground)
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    onFeedback(soundEnabled, vibrationEnabled)
                                    onEnter()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardReturn,
                                contentDescription = "Enter",
                                tint = theme.returnKeyText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Popup Preview Bubble for pressed key
        AnimatedVisibility(
            visible = activePopupChar != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            activePopupChar?.let { char ->
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(52.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(theme.accentColor)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun KeyButton(
    label: String,
    theme: SoftboardThemeColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onPressStart: () -> Unit = {},
    onPressEnd: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) theme.keyPressedBackground else theme.keyBackground)
            .border(
                width = 1.dp,
                color = if (isPressed) theme.accentColor else theme.keyBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(label) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressStart()
                        tryAwaitRelease()
                        isPressed = false
                        onPressEnd()
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = theme.primaryText,
            fontSize = if (label.length > 1) 12.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmojiPickerView(
    theme: SoftboardThemeColors,
    onEmojiSelected: (String) -> Unit,
    onBackToKeyboard: () -> Unit,
    onDelete: () -> Unit
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val categories = listOf("Smileys", "Gestures", "Objects", "Sri Lanka")
    val currentEmojis = when (selectedCategory) {
        0 -> KeyboardLayouts.emojiSmileys
        1 -> KeyboardLayouts.emojiGestures
        2 -> KeyboardLayouts.emojiObjects
        else -> KeyboardLayouts.emojiSriLanka
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedCategory,
            containerColor = theme.surface,
            contentColor = theme.accentColor,
            edgePadding = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            categories.forEachIndexed { index, name ->
                Tab(
                    selected = selectedCategory == index,
                    onClick = { selectedCategory = index },
                    text = {
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = if (selectedCategory == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedCategory == index) theme.accentColor else theme.secondaryText
                        )
                    }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 42.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            items(currentEmojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 22.sp)
                }
            }
        }

        // Bottom action bar for Emoji view
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.specialKeyBackground)
                    .clickable { onBackToKeyboard() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "ABC",
                    color = theme.accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.specialKeyBackground)
                    .clickable { onDelete() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = theme.accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ClipboardDrawerView(
    theme: SoftboardThemeColors,
    items: List<String>,
    onPaste: (String) -> Unit,
    onBackToKeyboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clipboard History",
                color = theme.accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.specialKeyBackground)
                    .clickable { onBackToKeyboard() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Done",
                    color = theme.primaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recent clips. Copy text in any app to paste here.",
                    color = theme.secondaryText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items) { clip ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.keyBackground)
                            .border(1.dp, theme.keyBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                onPaste(clip)
                                onBackToKeyboard()
                            }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = clip,
                            color = theme.primaryText,
                            fontSize = 13.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
