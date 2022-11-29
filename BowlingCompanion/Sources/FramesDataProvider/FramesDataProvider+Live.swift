import Dependencies
import FramesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension FramesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchFrames: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			return try await persistenceService.fetchFrames(.init(request))
		}
	)
}

extension Frame.Query {
	init(_ request: Frame.FetchRequest) {
		self.init(game: request.game)
	}
}
