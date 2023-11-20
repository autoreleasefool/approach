package ca.josephroque.bowlingcompanion.core.data.di

import ca.josephroque.bowlingcompanion.core.data.repository.AcknowledgementsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.DataTransferRepository
import ca.josephroque.bowlingcompanion.core.data.repository.FramesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LegacyMigrationRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstAcknowledgementsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstAlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstBowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstDataTransferRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstFramesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstGamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstGearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstLanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstLeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstLegacyMigrationRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstRecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstScoresRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstStatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstUserDataRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.ScoresRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
	@Binds
	fun bindsBowlersRepository(
		bowlersRepository: OfflineFirstBowlersRepository,
	): BowlersRepository

	@Binds
	fun bindsLeaguesRepository(
		leaguesRepository: OfflineFirstLeaguesRepository,
	): LeaguesRepository

	@Binds
	fun bindsSeriesRepository(
		seriesRepository: OfflineFirstSeriesRepository,
	): SeriesRepository

	@Binds
	fun bindsGamesRepository(
		gamesRepository: OfflineFirstGamesRepository,
	): GamesRepository

	@Binds
	fun bindsFramesRepository(
		framesRepository: OfflineFirstFramesRepository,
	): FramesRepository

	@Binds
	fun bindsGearRepository(
		gearRepository: OfflineFirstGearRepository,
	): GearRepository

	@Binds
	fun bindsAlleysRepository(
		alleysRepository: OfflineFirstAlleysRepository,
	): AlleysRepository

	@Binds
	fun bindsLanesRepository(
		lanesRepository: OfflineFirstLanesRepository,
	): LanesRepository

	@Binds
	fun bindsUserDataRepository(
		userDataRepository: OfflineFirstUserDataRepository,
	): UserDataRepository

	@Binds
	fun bindsRecentlyUsedRepository(
		recentlyUsedRepository: OfflineFirstRecentlyUsedRepository,
	): RecentlyUsedRepository

	@Binds
	fun bindsScoresRepository(
		scoresRepository: OfflineFirstScoresRepository,
	): ScoresRepository

	@Binds
	fun bindsLegacyMigrationRepository(
		legacyMigrationRepository: OfflineFirstLegacyMigrationRepository,
	): LegacyMigrationRepository

	@Binds
	fun bindsAcknowledgementsRepository(
		acknowledgementsRepository: OfflineFirstAcknowledgementsRepository,
	): AcknowledgementsRepository

	@Binds
	fun bindsDataTransferRepository(
		dataTransferRepository: OfflineFirstDataTransferRepository,
	): DataTransferRepository

	@Binds
	fun bindsStatisticsRepository(
		statisticsRepository: OfflineFirstStatisticsRepository,
	): StatisticsRepository
}