import DatabaseMockingServiceInterface
import Dependencies

extension DatabaseMockingService: DependencyKey {
	public static var liveValue: Self {
		Self(mockDatabase: {})
	}
}
