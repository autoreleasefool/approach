import Dependencies
import GRDB
import SharedModelsLibrary

public struct BowlersPersistenceService: Sendable {
	public var create: @Sendable (Bowler, Database) throws -> Void
	public var update: @Sendable (Bowler, Database) throws -> Void
	public var delete: @Sendable (Bowler, Database) throws -> Void

	public init(
		create: @escaping @Sendable (Bowler, Database) throws -> Void,
		update: @escaping @Sendable (Bowler, Database) throws -> Void,
		delete: @escaping @Sendable (Bowler, Database) throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension BowlersPersistenceService: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		update: { _, _ in fatalError("\(Self.self).update") },
		delete: { _, _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var bowlersPersistenceService: BowlersPersistenceService {
		get { self[BowlersPersistenceService.self] }
		set { self[BowlersPersistenceService.self] = newValue }
	}
}
