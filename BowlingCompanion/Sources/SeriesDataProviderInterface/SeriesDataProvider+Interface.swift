import Dependencies
import SharedModelsLibrary

public struct SeriesDataProvider {
	public var create: @Sendable (Series) async throws -> Void
	public var update: @Sendable (Series) async throws -> Void
	public var delete: @Sendable (Series) async throws -> Void
	public var fetchAll: @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>

	public init(
		create: @escaping @Sendable (Series) async throws -> Void,
		update: @escaping @Sendable (Series) async throws -> Void,
		delete: @escaping @Sendable (Series) async throws -> Void,
		fetchAll: @escaping @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>) {
		self.create = create
		self.update = update
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension SeriesDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		update: { _ in fatalError("\(Self.self).update") },
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
