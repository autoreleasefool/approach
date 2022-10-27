import Dependencies
import GRDB
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension LeaguesPersistenceService: DependencyKey {
	public static let liveValue = Self(
		create: { league, db in
			@Dependency(\.uuid) var uuid: UUIDGenerator
			@Dependency(\.date) var date: DateGenerator
			@Dependency(\.leaguesPersistenceService) var leaguesPersistenceService: LeaguesPersistenceService
			@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService

			try league.insert(db)
			if league.recurrence == .oneTimeEvent, let numberOfGames = league.numberOfGames {
				let series = Series(leagueId: league.id, id: uuid(), date: date(), numberOfGames: numberOfGames)
				try seriesPersistenceService.create(series, db)
			}
		},
		update: { league, db in
			try league.update(db)
		},
		delete: { league, db in
			try league.delete(db)
		}
	)
}
