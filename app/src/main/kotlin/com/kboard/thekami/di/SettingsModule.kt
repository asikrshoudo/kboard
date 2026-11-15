package com.kboard.thekami.di

import android.content.Context
import com.kboard.thekami.data.learning.createWordStore
import com.kboard.thekami.data.settings.SettingsRepository
import com.kboard.thekami.data.settings.createSettingsRepository
import com.kboard.thekami.engine.suggest.WordStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository =
        createSettingsRepository(context)

    @Provides
    @Singleton
    fun provideWordStore(@ApplicationContext context: Context): WordStore = createWordStore(context)
}
