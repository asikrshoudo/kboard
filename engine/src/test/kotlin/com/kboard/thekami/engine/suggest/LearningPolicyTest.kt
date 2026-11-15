package com.kboard.thekami.engine.suggest

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningPolicyTest {
    @Test
    fun learningIsAllowedInANormalField() {
        assertTrue(LearningPolicy.isLearningAllowed(false, false, false))
    }

    @Test
    fun eachRestrictionAloneTurnsLearningOff() {
        assertFalse(LearningPolicy.isLearningAllowed(sensitiveField = true, noPersonalizedLearning = false, incognito = false))
        assertFalse(LearningPolicy.isLearningAllowed(sensitiveField = false, noPersonalizedLearning = true, incognito = false))
        assertFalse(LearningPolicy.isLearningAllowed(sensitiveField = false, noPersonalizedLearning = false, incognito = true))
    }

    @Test
    fun sensitiveFieldsGetNoSuggestions() {
        assertFalse(LearningPolicy.isSuggestingAllowed(sensitiveField = true))
        assertTrue(LearningPolicy.isSuggestingAllowed(sensitiveField = false))
    }

    @Test
    fun onlyRealWordsAreLearnable() {
        assertTrue(LearningPolicy.isLearnableWord("hello"))
        assertTrue(LearningPolicy.isLearnableWord("don't"))
        assertTrue(LearningPolicy.isLearnableWord("আমি"))
    }

    @Test
    fun digitsAndExtremeLengthsAreNotLearnable() {
        assertFalse(LearningPolicy.isLearnableWord("a"))
        assertFalse(LearningPolicy.isLearnableWord("abc123"))
        assertFalse(LearningPolicy.isLearnableWord("1234"))
        assertFalse(LearningPolicy.isLearnableWord("a".repeat(33)))
        assertFalse(LearningPolicy.isLearnableWord(""))
    }
}
