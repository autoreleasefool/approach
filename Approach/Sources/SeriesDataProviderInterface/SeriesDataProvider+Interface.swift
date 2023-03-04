import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct SeriesDataProvider: Sendable {
	public var fetchSeries: @Sendable (Series.FetchRequest) async throws -> [Series]
	public var observeSeries: @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>

	public init(
		fetchSeries: @escaping @Sendable (Series.FetchRequest) async throws -> [Series],
		observeSeries: @escaping @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>
	) {
		self.fetchSeries = fetchSeries
		self.observeSeries = observeSeries
	}
}

extension SeriesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchSeries: { _ in fatalError("\(Self.self).fetchSeries") },
		observeSeries: { _ in fatalError("\(Self.self).observeSeries") }
	)
}

extension DependencyValues {
	public var seriesDataProvider: SeriesDataProvider {
		get { self[SeriesDataProvider.self] }
		set { self[SeriesDataProvider.self] = newValue }
	}
}
