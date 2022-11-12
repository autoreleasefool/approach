import Dependencies
import SharedModelsLibrary

public struct AlleysDataProvider: Sendable {
	public var fetchAlleys: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>

	public init(
		fetchAlleys: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>
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
