package ca.josephroque.bowlingcompanion.core.data.di

import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LegacyMigrationRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstAlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstBowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstGamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstGearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstLeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstLegacyMigrationRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstRecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstTeamsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.OfflineFirstUserDataRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
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
	fun bindsTeamsRepository(
		teamsRepository: OfflineFirstTeamsRepository,
	): TeamsRepository

	@Binds
	fun bindsGearRepository(
		gearRepository: OfflineFirstGearRepository,
	): GearRepository

	@Binds
	fun bindsAlleysRepository(
		alleysRepository: OfflineFirstAlleysRepository,
	): AlleysRepository

	@Binds
	fun bindsUserDataRepository(
		userDataRepository: OfflineFirstUserDataRepository,
	): UserDataRepository

	@Binds
	fun bindsRecentlyUsedRepository(
		recentlyUsedRepository: OfflineFirstRecentlyUsedRepository,
	): RecentlyUsedRepository

	@Binds
	fun bindsLegacyMigrationRepository(
		legacyMigrationRepository: OfflineFirstLegacyMigrationRepository,
	): LegacyMigrationRepository
}