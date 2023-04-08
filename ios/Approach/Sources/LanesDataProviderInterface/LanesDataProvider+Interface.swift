import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct LanesDataProvider: Sendable {
	public var fetchLanes: @Sendable (Lane.FetchRequest) async throws -> [Lane]
	public var observeLanes: @Sendable (Lane.FetchRequest) -> AsyncThrowingStream<[Lane], Error>

	public init(
		fetchLanes: @escaping @Sendable (Lane.FetchRequest) async throws -> [Lane],
		observeLanes: @escaping @Sendable (Lane.FetchRequest) -> AsyncThrowingStream<[Lane], Error>
	) {
		self.fetchLanes = fetchLanes
		self.observeLanes = observeLanes
	}
}

extension LanesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchLanes: { _ in unimplemented("\(Self.self).fetchLanes") },
		observeLanes: { _ in unimplemented("\(Self.self).observeLanes") }
	)
}

extension DependencyValues {
	public var lanesDataProvider: LanesDataProvider {
		get { self[LanesDataProvider.self] }
		set { self[LanesDataProvider.self] = newValue }
	}
}
