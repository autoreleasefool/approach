package ca.josephroque.bowlingcompanion.core.scoring.di

import ca.josephroque.bowlingcompanion.core.scoring.FivePinScoreKeeper
import ca.josephroque.bowlingcompanion.core.scoring.ScoreKeeper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ScoresModule {
	@Binds
	fun bindsScoreKeeper(scoreKeeper: FivePinScoreKeeper): ScoreKeeper
}