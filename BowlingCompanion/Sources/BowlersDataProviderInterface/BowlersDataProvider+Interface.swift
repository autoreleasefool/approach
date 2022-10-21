import Dependencies
import SharedModelsLibrary

public struct BowlersDataProvider: Sendable {
	public var create: @Sendable (Bowler) async throws -> Void
	public var update: @Sendable (Bowler) async throws -> Void
	public var delete: @Sendable (Bowler) async throws -> Void
	public var fetchAll: @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>

	public init(
		create: @escaping @Sendable (Bowler) async throws -> Void,
		update: @escaping @Sendable (Bowler) async throws -> Void,
		delete: @escaping @Sendable (Bowler) async throws -> Void,
		fetchAll: @escaping @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>
	) {
		self.create = create
		self.update = update
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension BowlersDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		update: { _ in fatalError("\(Self.self).update") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { _ in fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var bowlersDataProvider: BowlersDataProvider {
		get { self[BowlersDataProvider.self] }
		set { self[BowlersDataProvider.self] = newValue }
	}
}
