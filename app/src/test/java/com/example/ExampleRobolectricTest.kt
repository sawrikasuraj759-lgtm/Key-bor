package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SOFTBOARD", appName)
  }

  @Test
  fun `verify default preferences`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = com.example.ime.SoftboardPreferences(context)
    assertEquals(com.example.ime.SoftboardThemeType.NEON_PURPLE, prefs.theme)
    assertEquals(com.example.ime.KeyboardHeight.NORMAL, prefs.height)
    assertEquals(com.example.ime.KeyboardLanguage.ENGLISH_US, prefs.language)
    assertTrue(prefs.soundEnabled)
    assertTrue(prefs.vibrationEnabled)
  }
}
