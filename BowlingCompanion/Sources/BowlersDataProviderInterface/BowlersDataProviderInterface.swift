import Dependencies
import SharedModelsLibrary

public struct BowlersDataProvider: Sendable {
	public var save: @Sendable (Bowler) async throws -> Void
	public var delete: @Sendable (Bowler) async throws -> Void
	public var fetchAll: @Sendable () -> AsyncStream<[Bowler]>

	public init(
		save: @escaping @Sendable (Bowler) async throws -> Void,
		delete: @escaping @Sendable (Bowler) async throws -> Void,
		fetchAll: @escaping @Sendable () -> AsyncStream<[Bowler]>
	) {
		self.save = save
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension BowlersDataProvider: TestDependencyKey {
	public static var testValue = Self(
		save: { _ in fatalError("\(Self.self).save") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var bowlersDataProvider: BowlersDataProvider {
		get { self[BowlersDataProvider.self] }
		set { self[BowlersDataProvider.self] = newValue }
	}
}
