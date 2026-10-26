package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ime.KeyboardHeight
import com.example.ime.KeyboardLanguage
import com.example.ime.SoftboardHeaderBranding
import com.example.ime.SoftboardPreferences
import com.example.ime.SoftboardThemeColors
import com.example.ime.SoftboardThemeType
import com.example.ime.SoftboardThemes
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SoftboardBorder
import com.example.ui.theme.SoftboardDeepDark
import com.example.ui.theme.SoftboardElectricBlue
import com.example.ui.theme.SoftboardNeonPurple
import com.example.ui.theme.SoftboardSurface
import com.example.ui.theme.SoftboardSurfaceVariant
import com.example.ui.theme.SoftboardTextPrimary
import com.example.ui.theme.SoftboardTextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SoftboardMainScreen()
            }
        }
    }
}

enum class SoftboardScreen(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SETUP("Setup & Test", Icons.Default.Keyboard),
    THEMES("Themes", Icons.Default.ColorLens),
    LANGUAGES("Languages", Icons.Default.Language),
    KEYPAD("Keypad", Icons.Default.Tune),
    CLIPBOARD("Clipboard", Icons.Default.Assignment),
    ABOUT("About", Icons.Default.Info)
}

@Composable
fun SoftboardMainScreen() {
    val context = LocalContext.current
    val prefs = remember { SoftboardPreferences(context) }
    var currentScreen by remember { mutableStateOf(SoftboardScreen.SETUP) }

    var isEnabled by remember { mutableStateOf(checkIsKeyboardEnabled(context)) }
    var isDefault by remember { mutableStateOf(checkIsKeyboardDefault(context)) }

    // Refresh IME status whenever activity resumes
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isEnabled = checkIsKeyboardEnabled(context)
                isDefault = checkIsKeyboardDefault(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftboardDeepDark)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = SoftboardDeepDark,
        bottomBar = {
            NavigationBar(
                containerColor = SoftboardSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                SoftboardScreen.values().forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (currentScreen == screen) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SoftboardNeonPurple,
                            selectedTextColor = SoftboardNeonPurple,
                            indicatorColor = SoftboardSurfaceVariant,
                            unselectedIconColor = SoftboardTextSecondary.copy(alpha = 0.6f),
                            unselectedTextColor = SoftboardTextSecondary.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SoftboardHeaderBranding()

                // Live IME Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isEnabled && isDefault) Color(0xFF064E3B) else Color(0xFF3B1D28))
                        .border(
                            1.dp,
                            if (isEnabled && isDefault) Color(0xFF10B981) else Color(0xFFF43F5E),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isEnabled && isDefault) Color(0xFF10B981) else Color(0xFFF43F5E))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnabled && isDefault) "Active & Ready" else "Setup Needed",
                            color = if (isEnabled && isDefault) Color(0xFFA7F3D0) else Color(0xFFFECDD3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Type Smooth. Type Smart.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SoftboardTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Body Content based on Tab
            when (currentScreen) {
                SoftboardScreen.SETUP -> SetupScreen(
                    isEnabled = isEnabled,
                    isDefault = isDefault,
                    onRefresh = {
                        isEnabled = checkIsKeyboardEnabled(context)
                        isDefault = checkIsKeyboardDefault(context)
                    }
                )
                SoftboardScreen.THEMES -> ThemesScreen(prefs = prefs)
                SoftboardScreen.LANGUAGES -> LanguagesScreen(prefs = prefs)
                SoftboardScreen.KEYPAD -> KeypadScreen(prefs = prefs)
                SoftboardScreen.CLIPBOARD -> ClipboardScreen(prefs = prefs)
                SoftboardScreen.ABOUT -> AboutScreen()
            }
        }
    }
}

@Composable
fun SetupScreen(
    isEnabled: Boolean,
    isDefault: Boolean,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    var testText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // STEP 1 CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step_1_enable_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SoftboardSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "1",
                                    fontWeight = FontWeight.Bold,
                                    color = SoftboardElectricBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Enable SOFTBOARD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftboardTextPrimary
                            )
                        }

                        if (isEnabled) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Enabled",
                                tint = Color(0xFF10B981)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Pending",
                                tint = Color(0xFFF59E0B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEnabled)
                            "✓ SOFTBOARD is enabled in Android Keyboard & Input Settings."
                        else
                            "Turn on the SOFTBOARD switch in Android Language & Input settings.",
                        fontSize = 13.sp,
                        color = SoftboardTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabled) SoftboardSurfaceVariant else SoftboardNeonPurple
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_ime_settings")
                    ) {
                        Text(
                            text = if (isEnabled) "Manage Settings" else "Enable in Android Settings",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            // STEP 2 CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step_2_select_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SoftboardSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "2",
                                    fontWeight = FontWeight.Bold,
                                    color = SoftboardNeonPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Select Default Keyboard",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftboardTextPrimary
                            )
                        }

                        if (isDefault) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active Default",
                                tint = Color(0xFF10B981)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Not Default",
                                tint = Color(0xFFF59E0B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isDefault)
                            "✓ SOFTBOARD is your current active default keyboard."
                        else
                            "Select SOFTBOARD Keyboard in the Android input method chooser dialog.",
                        fontSize = 13.sp,
                        color = SoftboardTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDefault) SoftboardSurfaceVariant else SoftboardElectricBlue
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_ime_picker")
                    ) {
                        Text(
                            text = if (isDefault) "Change Input Method" else "Switch to SOFTBOARD",
                            color = if (isDefault) Color.White else Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            // STEP 3: LIVE TYPING TEST SANDBOX
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("typing_test_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SoftboardSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "3",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE879F9)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Interactive Typing Test",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftboardTextPrimary
                            )
                        }

                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh status",
                                tint = SoftboardTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the field below to bring up SOFTBOARD and test English, Sinhala, emojis, and gestures!",
                        fontSize = 13.sp,
                        color = SoftboardTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = testText,
                        onValueChange = { testText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("typing_test_input"),
                        placeholder = {
                            Text(
                                text = "Tap here to test typing with SOFTBOARD...",
                                color = SoftboardTextSecondary.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftboardNeonPurple,
                            unfocusedBorderColor = SoftboardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = SoftboardElectricBlue,
                            focusedContainerColor = SoftboardSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = SoftboardSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${testText.length} characters",
                            fontSize = 12.sp,
                            color = SoftboardTextSecondary
                        )

                        if (testText.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { testText = "" },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Clear", fontSize = 11.sp, color = SoftboardNeonPurple)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemesScreen(prefs: SoftboardPreferences) {
    var activeTheme by remember { mutableStateOf(prefs.theme) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Keyboard Themes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftboardTextPrimary
            )
            Text(
                text = "Choose an aesthetic crafted for maximum typing clarity and style.",
                fontSize = 13.sp,
                color = SoftboardTextSecondary
            )
        }

        items(SoftboardThemeType.values()) { themeType ->
            val themeColors = SoftboardThemes.getTheme(themeType)
            val isSelected = activeTheme == themeType

            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) SoftboardNeonPurple else SoftboardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        prefs.theme = themeType
                        activeTheme = themeType
                    }
                    .testTag("theme_card_${themeType.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(themeColors.accentColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = themeType.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftboardTextPrimary
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftboardNeonPurple)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Active",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = themeType.description,
                        fontSize = 12.sp,
                        color = SoftboardTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mini Keyboard Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(themeColors.background)
                            .border(1.dp, themeColors.keyBorder, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Row 1 sample
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Q", "W", "E", "R", "T", "Y", "U", "I").forEach {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(themeColors.keyBackground)
                                            .border(0.5.dp, themeColors.keyBorder, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = it,
                                            color = themeColors.primaryText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Row 2 sample
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(themeColors.specialKeyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("?123", color = themeColors.accentColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(4f)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(themeColors.keyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("SOFTBOARD", color = themeColors.secondaryText, fontSize = 8.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(themeColors.returnKeyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("↵", color = themeColors.returnKeyText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguagesScreen(prefs: SoftboardPreferences) {
    var activeLanguage by remember { mutableStateOf(prefs.language) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Language & Layouts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftboardTextPrimary
            )
            Text(
                text = "Switch default layout or toggle between English and Sinhala seamlessly while typing.",
                fontSize = 13.sp,
                color = SoftboardTextSecondary
            )
        }

        items(KeyboardLanguage.values()) { lang ->
            val isSelected = activeLanguage == lang

            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) SoftboardNeonPurple else SoftboardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        prefs.language = lang
                        activeLanguage = lang
                    }
                    .testTag("lang_card_${lang.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SoftboardSurfaceVariant)
                                    .border(1.dp, SoftboardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = lang.shortLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftboardElectricBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = lang.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftboardTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (lang) {
                                KeyboardLanguage.ENGLISH_US -> "Standard English QWERTY with smart word suggestions"
                                KeyboardLanguage.SINHALA_WIJESEKARA -> "Official Sri Lanka SLS 1134 Wijesekara standard layout"
                                KeyboardLanguage.SINHALA_PHONETIC -> "Intuitive phonetic layout with one-tap diacritics bar"
                            },
                            fontSize = 12.sp,
                            color = SoftboardTextSecondary
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = SoftboardNeonPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "💡 Quick Switch Tip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SoftboardElectricBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When typing in any app, tap the language badge (EN / සිං) in the SOFTBOARD toolbar to switch layouts instantly without leaving the chat.",
                        fontSize = 12.sp,
                        color = SoftboardTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun KeypadScreen(prefs: SoftboardPreferences) {
    var height by remember { mutableStateOf(prefs.height) }
    var sound by remember { mutableStateOf(prefs.soundEnabled) }
    var vibration by remember { mutableStateOf(prefs.vibrationEnabled) }
    var popup by remember { mutableStateOf(prefs.popupPreviewEnabled) }
    var numberRow by remember { mutableStateOf(prefs.showNumberRow) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Keypad Personalization",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftboardTextPrimary
            )
            Text(
                text = "Adjust keyboard dimensions, feedback, and interactive popups.",
                fontSize = 13.sp,
                color = SoftboardTextSecondary
            )
        }

        item {
            // Keyboard Height Selection
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Keyboard Height",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftboardTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyboardHeight.values().forEach { h ->
                            val isSelected = height == h
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SoftboardNeonPurple else SoftboardSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) SoftboardNeonPurple else SoftboardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        prefs.height = h
                                        height = h
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${h.displayName}\n(${h.heightDp}dp)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else SoftboardTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Toggles Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Number row toggle
                    ToggleRow(
                        title = "Always Show Number Row",
                        description = "Display digits 1-0 in a dedicated top row",
                        checked = numberRow,
                        onCheckedChange = {
                            prefs.showNumberRow = it
                            numberRow = it
                        }
                    )

                    HorizontalDivider(color = SoftboardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // Sound on keypress
                    ToggleRow(
                        title = "Keypress Sound",
                        description = "Play subtle mechanical audio feedback when typing",
                        checked = sound,
                        onCheckedChange = {
                            prefs.soundEnabled = it
                            sound = it
                        }
                    )

                    HorizontalDivider(color = SoftboardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // Vibration / Haptics
                    ToggleRow(
                        title = "Haptic Vibration",
                        description = "Vibrate on each keystroke for tactile response",
                        checked = vibration,
                        onCheckedChange = {
                            prefs.vibrationEnabled = it
                            vibration = it
                        }
                    )

                    HorizontalDivider(color = SoftboardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // Popup preview
                    ToggleRow(
                        title = "Key Popup Preview",
                        description = "Show enlarged floating bubble of the pressed key",
                        checked = popup,
                        onCheckedChange = {
                            prefs.popupPreviewEnabled = it
                            popup = it
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SoftboardTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = SoftboardTextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SoftboardNeonPurple,
                uncheckedThumbColor = SoftboardTextSecondary,
                uncheckedTrackColor = SoftboardSurfaceVariant
            )
        )
    }
}

@Composable
fun ClipboardScreen(prefs: SoftboardPreferences) {
    var history by remember { mutableStateOf(prefs.getClipboardHistory()) }
    var newSnippet by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Clipboard Manager",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftboardTextPrimary
            )
            Text(
                text = "Saved text snippets are available in SOFTBOARD's clipboard drawer.",
                fontSize = 13.sp,
                color = SoftboardTextSecondary
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Custom Quick Snippet",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftboardTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSnippet,
                            onValueChange = { newSnippet = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("e.g. My address or phone...", color = SoftboardTextSecondary, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftboardNeonPurple,
                                unfocusedBorderColor = SoftboardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newSnippet.isNotBlank()) {
                                    prefs.addClipboardItem(newSnippet.trim())
                                    history = prefs.getClipboardHistory()
                                    newSnippet = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftboardNeonPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Clips (${history.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftboardTextPrimary
                )

                if (history.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            prefs.clearClipboardHistory()
                            history = emptyList()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All", fontSize = 11.sp, color = Color(0xFFF43F5E))
                    }
                }
            }
        }

        if (history.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved snippets yet.\nAdd snippets above or copy text in any app.",
                        color = SoftboardTextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(history) { clip ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clip,
                            color = SoftboardTextPrimary,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    com.example.ime.SoftboardKeyLogo(size = 64.dp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "SOFTBOARD",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Type Smooth. Type Smart.",
                        fontSize = 13.sp,
                        color = SoftboardElectricBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version 1.0.0 • Native Android InputMethodService",
                        fontSize = 11.sp,
                        color = SoftboardTextSecondary
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Open Source Heritage",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftboardTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SOFTBOARD is inspired by the open-source FlorisBoard Android keyboard project (https://github.com/florisboard/florisboard), licensed under the Apache License 2.0.\n\nFlorisBoard was created by Patrick Goldinger and the open-source community to provide a privacy-respecting, modern keyboard for Android.",
                        fontSize = 13.sp,
                        color = SoftboardTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftboardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftboardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Privacy Shield",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy Guarantee",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftboardTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SOFTBOARD operates 100% offline on your device:\n• Zero internet permission\n• No keystroke logging or transmission\n• Local clipboard only\n• No hidden telemetry or third-party SDKs",
                        fontSize = 13.sp,
                        color = SoftboardTextSecondary,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

private fun checkIsKeyboardEnabled(context: Context): Boolean {
    return try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.enabledInputMethodList.any { it.packageName == context.packageName }
    } catch (_: Exception) {
        false
    }
}

private fun checkIsKeyboardDefault(context: Context): Boolean {
    return try {
        val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        defaultIme?.contains(context.packageName) == true
    } catch (_: Exception) {
        false
    }
}
