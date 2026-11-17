package com.kboard.thekami.data.settings

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BehaviorTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.repository() = settingsRepository(folder.root)

    @Test
    fun holdingSpaceSwitchesLanguageByDefault() = runTest {
        assertEquals(SpaceLongPress.SWITCH_LANGUAGE, repository().settings.first().behavior.spaceLongPress)
    }

    @Test
    fun everyChoiceIsSavedAndReadBack() = runTest {
        val repository = repository()
        SpaceLongPress.entries.forEach { choice ->
            repository.behavior.setSpaceLongPress(choice)
            assertEquals(choice, repository.settings.first().behavior.spaceLongPress)
        }
    }

    @Test
    fun anUnknownOrMissingIdMeansTheDefault() {
        assertEquals(SpaceLongPress.SWITCH_LANGUAGE, SpaceLongPress.fromId("something else"))
        assertEquals(SpaceLongPress.SWITCH_LANGUAGE, SpaceLongPress.fromId(null))
    }

    @Test
    fun everyChoiceHasItsOwnId() {
        assertEquals(SpaceLongPress.entries.size, SpaceLongPress.entries.map { it.id }.toSet().size)
    }
}
