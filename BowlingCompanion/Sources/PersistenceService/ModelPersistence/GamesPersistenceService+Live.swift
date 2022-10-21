import Dependencies
import GRDB
import PersistenceModelsLibrary
import PersistenceServiceInterface
import SharedModelsLibrary

extension GamesPersistenceService: DependencyKey {
	public static let liveValue = Self(
		create: { game, db in
			@Dependency(\.framesPersistenceService) var framesPersistenceService: FramesPersistenceService
			try game.insert(db)
			for ordinal in (1...10) {
				let frame = Frame(
					gameId: game.id,
					ordinal: ordinal,
					isAccessed: false,
					firstBall: nil,
					secondBall: nil,
					thirdBall: nil
				)
				try framesPersistenceService.create(frame, db)
			}
		},
		update: { game, db in
			try game.update(db)
		},
		delete: { game, db in
			try game.delete(db)
		}
	)
}
