import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct SeriesDataProvider: Sendable {
	public var fetchSeries: @Sendable (Series.FetchRequest) async throws -> [Series]

	public init(
		fetchSeries: @escaping @Sendable (Series.FetchRequest) async throws -> [Series]
	) {
		self.fetchSeries = fetchSeries
	}
}

extension SeriesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchSeries: { _ in fatalError("\(Self.self).fetchSeries") }
	)
}

extension DependencyValues {
	public var seriesDataProvider: SeriesDataProvider {
		get { self[SeriesDataProvider.self] }
		set { self[SeriesDataProvider.self] = newValue }
	}
}
