package com.example.ime

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SWITCH_LAYOUT,
    SWITCH_SYMBOLS,
    EMOJI,
    CLIPBOARD,
    SETTINGS
}

data class SoftKeyData(
    val primaryText: String,
    val shiftedText: String = primaryText,
    val secondaryText: String? = null,
    val type: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f
)

object KeyboardLayouts {
    val numberRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val qwertyRow1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val qwertyRow2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val qwertyRow3 = listOf("z", "x", "c", "v", "b", "n", "m")

    // Wijesekara Sinhala layout (Standard SLS 1134)
    val wijesekaraNormalRow1 = listOf("ො", "අ", "ැ", "ර", "එ", "හ", "ම", "ස", "ද", "ච")
    val wijesekaraNormalRow2 = listOf("්", "ි", "ා", "ෙ", "ට", "ය", "ව", "න", "ක", "ත")
    val wijesekaraNormalRow3 = listOf("ූ", "ං", "ජ", "ඩ", "ඉ", "බ", "ප", "ල", "ග")

    val wijesekaraShiftRow1 = listOf("ෝ", "ඌ", "ෑ", "ඍ", "ඒ", "ශ", "ඹ", "ෂ", "ධ", "ඡ")
    val wijesekaraShiftRow2 = listOf("ෟ", "ී", "ෘ", "ෆ", "ඨ", "්‍ය", "ළු", "ණ", "ඛ", "ථ")
    val wijesekaraShiftRow3 = listOf("ෲ", "ඞ", "ඣ", "ඪ", "ඊ", "භ", "ඵ", "ළ", "ඝ")

    // Phonetic / Easy Sinhala Layout
    val phoneticRow1 = listOf("ක", "ග", "ච", "ජ", "ට", "ඩ", "ත", "ද", "න", "ප")
    val phoneticRow2 = listOf("බ", "ම", "ය", "ර", "ල", "ව", "ස", "හ", "ළ", "ෆ")
    val phoneticRow3 = listOf("අ", "ආ", "ඇ", "ඈ", "ඉ", "ඊ", "උ", "ඌ", "එ", "ඔ")

    val sinhalaDiacritics = listOf(
        "්", "ා", "ැ", "ෑ", "ි", "ී", "ු", "ූ", "ෘ", "ෙ", "ේ", "ෛ", "ො", "ෝ", "ෞ", "ං"
    )

    // Symbol layouts
    val symbolsRow1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val symbolsRow2 = listOf("@", "#", "$", "%", "&", "*", "-", "+", "(", ")")
    val symbolsRow3 = listOf("!", "\"", "'", ":", ";", "/", "?")

    val altSymbolsRow1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val altSymbolsRow2 = listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
    val altSymbolsRow3 = listOf("%", "<", ">", "[", "]", "_", "¡", "¿")

    // Emoji collections
    val emojiSmileys = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗",
        "😚", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐", "🤨", "😐", "😑", "😶", "😏", "😒",
        "🙄", "😬", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "🥵", "🥶", "🥴", "😵",
        "🤯", "🤠", "🥳", "😎", "🤓", "🧐", "😕", "😟", "🙁", "😮", "😲", "😳", "🥺", "😦", "😧", "😨", "😰", "😥",
        "😢", "😭", "😱", "😖", "😣", "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠", "🤬", "😈", "👿", "💀", "💩"
    )

    val emojiGestures = listOf(
        "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞", "🤟", "🤘", "🤙", "👈", "👉", "👆", "👇", "☝️",
        "👍", "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪", "❤️", "🧡",
        "💛", "💚", "💙", "💜", "🤎", "🖤", "🤍", "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘"
    )

    val emojiObjects = listOf(
        "💻", "📱", "⌨️", "🖥️", "🖨️", "🖱️", "🎮", "🕹️", "🎧", "🎤", "📷", "📸", "📹", "🎥", "📽️", "🔋", "🔌", "💡",
        "🔦", "🕯️", "🪙", "💵", "💴", "💶", "💷", "💎", "⚖️", "🧲", "⚙️", "🧰", "🔧", "🔨", "🚀", "🛸", "🛰️", "⚡",
        "🌟", "✨", "🔮", "🪄", "🔒", "🔓", "🔑", "🗝️", "📦", "✉️", "📝", "📊", "📈", "📉", "☕", "🍕", "🍔", "🍰"
    )

    val emojiSriLanka = listOf(
        "🇱🇰", "🐘", "🦁", "🦚", "🌴", "🥥", "🌺", "🌸", "🏏", "🏆", "🎯", "☀️", "🌙", "⛅", "🌈", "🔥", "💧", "🌍", "🪐", "⭐"
    )

    val quickSnippetsEn = listOf("Hello", "Thanks!", "On my way", "Sounds good", "Great!", "Call you later", "OK 👍")
    val quickSnippetsSi = listOf("ආයුබෝවන්", "ස්තූතියි", "සුබ දවසක්", "හරි", "එළකිරි", "පස්සෙ කතා කරමු", "කොහොමද?")
}
