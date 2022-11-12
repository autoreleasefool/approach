import Dependencies
import FramesDataProviderInterface
import PersistenceServiceInterface
import SharedModelsLibrary

extension FramesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchFrames: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			return persistenceService.fetchFrames(.init(game: request.game))
		}
	)
}
