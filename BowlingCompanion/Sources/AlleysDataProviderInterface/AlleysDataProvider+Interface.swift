import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct AlleysDataProvider: Sendable {
	public var fetchAlleys: @Sendable (Alley.FetchRequest) async throws -> [Alley]

	public init(
		fetchAlleys: @escaping @Sendable (Alley.FetchRequest) async throws -> [Alley]
	) {
		self.fetchAlleys = fetchAlleys
	}
}

extension AlleysDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchAlleys: { _ in fatalError("\(Self.self).fetchAlleys") }
	)
}

extension DependencyValues {
	public var alleysDataProvider: AlleysDataProvider {
		get { self[AlleysDataProvider.self] }
		set { self[AlleysDataProvider.self] = newValue }
	}
}
