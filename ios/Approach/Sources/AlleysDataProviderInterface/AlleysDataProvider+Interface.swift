import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct AlleysDataProvider: Sendable {
	public var fetchAlleys: @Sendable (Alley.FetchRequest) async throws -> [Alley]
	public var observeAlleys: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>

	public init(
		fetchAlleys: @escaping @Sendable (Alley.FetchRequest) async throws -> [Alley],
		observeAlleys: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>
	) {
		self.fetchAlleys = fetchAlleys
		self.observeAlleys = observeAlleys
	}
}

extension AlleysDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchAlleys: { _ in unimplemented("\(Self.self).fetchAlleys") },
		observeAlleys: { _ in unimplemented("\(Self.self).observeAlleys") }
	)
}

extension DependencyValues {
	public var alleysDataProvider: AlleysDataProvider {
		get { self[AlleysDataProvider.self] }
		set { self[AlleysDataProvider.self] = newValue }
	}
}
