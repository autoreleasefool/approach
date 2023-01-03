import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct AveragesDataProvider: Sendable {
	public var fetchAverages: @Sendable (Average.FetchRequest) async throws -> [Average]
	public var observeAverages: @Sendable (Average.FetchRequest) -> AsyncThrowingStream<[Average], Error>

	public init(
		fetchAverages: @escaping @Sendable (Average.FetchRequest) async throws -> [Average],
		observeAverages: @escaping @Sendable (Average.FetchRequest) -> AsyncThrowingStream<[Average], Error>
	) {
		self.fetchAverages = fetchAverages
		self.observeAverages = observeAverages
	}
}

extension AveragesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchAverages: { _ in fatalError("\(Self.self).fetchAverages") },
		observeAverages: { _ in fatalError("\(Self.self).observeAverages") }
	)
}

extension DependencyValues {
	public var averagesDataProvider: AveragesDataProvider {
		get { self[AveragesDataProvider.self] }
		set { self[AveragesDataProvider.self] = newValue }
	}
}
