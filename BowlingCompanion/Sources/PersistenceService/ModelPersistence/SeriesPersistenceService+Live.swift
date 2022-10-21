import Dependencies
import GRDB
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension SeriesPersistenceService: DependencyKey {
	public static let liveValue = Self(
		create: { series, db in
			@Dependency(\.uuid) var uuid: UUIDGenerator
			@Dependency(\.seriesPersistenceService) var seriesPersistenceService: SeriesPersistenceService
			@Dependency(\.gamesPersistenceService) var gamesPersistenceService: GamesPersistenceService

			try series.insert(db)
			for ordinal in (1...series.numberOfGames) {
				let game = Game(
					seriesId: series.id,
					id: uuid(),
					ordinal: ordinal,
					locked: .unlocked,
					manualScore: nil
				)
				try gamesPersistenceService.create(game, db)
			}
		},
		update: { series, db in
			try series.update(db)
		},
		delete: { series, db in
			try series.delete(db)
		}
	)
}
