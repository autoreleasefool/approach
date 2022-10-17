import Dependencies
import SharedModelsLibrary

public struct SeriesDataProvider {
	public var create: @Sendable (League, Series) async throws -> Void
	public var delete: @Sendable (Series) async throws -> Void
	public var fetchAll: @Sendable (League) -> AsyncStream<[Series]>

	public init(
		create: @escaping @Sendable (League, Series) async throws -> Void,
		delete: @escaping @Sendable (Series) async throws -> Void,
		fetchAll: @escaping @Sendable (League) -> AsyncStream<[Series]>) {
		self.create = create
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension SeriesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).save") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { _ in fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var seriesDataProvider: SeriesDataProvider {
		get { self[SeriesDataProvider.self] }
		set { self[SeriesDataProvider.self] = newValue }
	}
}
