import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FramesRepositoryInterface
import GRDB
import ModelsLibrary
import RepositoryLibrary

extension FramesRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database

		return Self(
			observe: { game in
				database.reader().observe {
					try Frame.Database
						.all()
						.filter(byGame: game)
						.orderByIndex()
						.including(optional: Frame.Database.bowlingBall0.forKey("bowlingBall0"))
						.including(optional: Frame.Database.bowlingBall1.forKey("bowlingBall1"))
						.including(optional: Frame.Database.bowlingBall2.forKey("bowlingBall2"))
						.asRequest(of: Frame.Edit.self)
						.fetchAll($0)
				}
			},
			update: { frame in
				try await database.writer().write {
					try frame.update($0)
				}
			}
		)
	}()
}
