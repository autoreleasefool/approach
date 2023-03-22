import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct LanesDataProvider: Sendable {
	public var fetchLanes: @Sendable (Lane.FetchRequest) async throws -> [Lane]

	public init(
		fetchLanes: @escaping @Sendable (Lane.FetchRequest) async throws -> [Lane]
	) {
		self.fetchLanes = fetchLanes
	}
}

extension LanesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchLanes: { _ in unimplemented("\(Self.self).fetchLanes") }
	)
}

extension DependencyValues {
	public var lanesDataProvider: LanesDataProvider {
		get { self[LanesDataProvider.self] }
		set { self[LanesDataProvider.self] = newValue }
	}
}
