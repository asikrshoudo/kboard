package com.kboard.thekami.engine.autocorrect

/**
 * Finds the fix for a typed word. [ignored] returns the words the user has
 * undone a correction for; those are never corrected again.
 */
class AutoCorrector(private val ignored: () -> Set<String>) {
    /**
     * The fix for [word] typed just before [delimiter], or null to leave it alone.
     * A single letter (i -> I) is only fixed before a space or a new line, so
     * "i.e." is not touched.
     */
    fun correctionFor(word: String, delimiter: String): String? {
        val key = word.lowercase()
        val fix = MisspellingTable.rules[key]
            ?.takeIf { key !in ignored() && (word.length > 1 || delimiter in SPACE_OR_NEWLINE) }
        return fix?.let { matchCase(word, it) }?.takeIf { it != word }
    }

    /** Keeps the typed capitalization: teh -> the, Teh -> The, TEH -> THE. */
    private fun matchCase(typed: String, fix: String): String = when {
        typed.length > 1 && typed.all { it.isUpperCase() } -> fix.uppercase()
        typed.first().isUpperCase() -> fix.replaceFirstChar { it.uppercase() }
        else -> fix
    }

    private companion object {
        val SPACE_OR_NEWLINE = setOf(" ", "\n")
    }
}
