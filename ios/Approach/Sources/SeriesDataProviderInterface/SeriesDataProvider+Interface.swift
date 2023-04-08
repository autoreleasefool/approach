import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct SeriesDataProvider: Sendable {
	public var observeSeries: @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>

	public init(
		observeSeries: @escaping @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>
	) {
		self.observeSeries = observeSeries
	}
}

extension SeriesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeSeries: { _ in unimplemented("\(Self.self).observeSeries") }
	)
}

extension DependencyValues {
	public var seriesDataProvider: SeriesDataProvider {
		get { self[SeriesDataProvider.self] }
		set { self[SeriesDataProvider.self] = newValue }
	}
}
