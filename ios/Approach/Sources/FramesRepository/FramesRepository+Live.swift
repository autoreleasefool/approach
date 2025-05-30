import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FramesRepositoryInterface
import GRDB
import ModelsLibrary
import RepositoryLibrary

extension FramesRepository: DependencyKey {
	public static var liveValue: Self {
		Self(
			observe: { game in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Frame.Database
						.all()
						.filter { $0.gameId == game }
						.order(\.index)
						.including(
							optional: Frame.Database
								.bowlingBall0
								.includingSummaryProperties()
								.forKey("bowlingBall0")
						)
						.including(
							optional: Frame.Database
								.bowlingBall1
								.includingSummaryProperties()
								.forKey("bowlingBall1")
						)
						.including(
							optional: Frame.Database.bowlingBall2
								.includingSummaryProperties()
								.forKey("bowlingBall2")
						)
						.asRequest(of: Frame.Edit.self)
						.fetchAll($0)
				}
			},
			observeRolls: { game in
				@Dependency(DatabaseService.self) var database

				return database.reader().observe {
					try Frame.Database
						.all()
						.filter { $0.gameId == game }
						.order(\.index)
						.select(
							Frame.Database.Columns.roll0,
							Frame.Database.Columns.roll1,
							Frame.Database.Columns.roll2
						)
						.asRequest(of: Frame.Rolls.self)
						.fetchAll($0)
						.map(\.rolls)
				}
			},
			update: { frame in
				@Dependency(DatabaseService.self) var database

				try await database.writer().write {
					try frame.update($0)
				}
			}
		)
	}
}
