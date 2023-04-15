import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FramesRepositoryInterface
import GRDB
import ModelsLibrary
import RepositoryLibrary

extension FramesRepository: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			edit: { game in
				@Dependency(\.database) var database
				let frames = try await database.reader().read {
					try Frame.Edit
						.all()
						.orderByOrdinal()
						.filter(byGame: game)
						.fetchAll($0)
				}

				return frames.count > 0 ? frames : nil
			},
			update: { frame in
				@Dependency(\.database) var database
				try await database.writer().write {
					try frame.update($0)
				}
			}
		)
	}()
}
