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
			load: { game in
				let frames = try await database.reader().read {
					try Frame.Database
						.all()
						.orderByOrdinal()
						.filter(byGame: game)
						.asRequest(of: Frame.Summary.self)
						.fetchAll($0)
				}
				return frames.count > 0 ? frames : nil
			},
			edit: { game in
				let frames = try await database.reader().read {
					try Frame.Database
						.all()
						.orderByOrdinal()
						.filter(byGame: game)
						.asRequest(of: Frame.Edit.self)
						.fetchAll($0)
				}
				return frames.count > 0 ? frames : nil
			},
			update: { frame in
				try await database.writer().write {
					try frame.update($0)
				}
			}
		)
	}()
}
