package dev.byrt.burb.util.extension

private val VOWELS = listOf('a', 'e', 'i', 'o', 'u')

/**
 * Check if the string begins with a vowel.
 * Case-insensitive, if empty or not a vowel returns false.
 * [VOWELS] are a, e, i, o, u.
 */
fun String.startsWithVowel(): Boolean {
    try {
        val firstChar = this.lowercase().first()
        return VOWELS.contains(firstChar)
    } catch (ex: NoSuchElementException) {
        return false
    }
}