import Dependencies
import FramesDataProviderInterface
import PersistenceServiceInterface

extension FramesDataProvider: DependencyKey {
	public static let liveValue = Self(
		fetchFrames: { request in
			@Dependency(\.persistenceService) var persistenceService: PersistenceService
			let frames = try await persistenceService.fetchFrames(request)

			switch request.ordering {
			case .byOrdinal:
				return frames
			}
		}
	)
}
