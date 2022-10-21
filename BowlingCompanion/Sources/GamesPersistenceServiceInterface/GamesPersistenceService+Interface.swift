import Dependencies
import GRDB
import SharedModelsLibrary

public struct GamesPersistenceService: Sendable {
	public var create: @Sendable (Game, Database) throws -> Void
	public var update: @Sendable (Game, Database) throws -> Void
	public var delete: @Sendable (Game, Database) throws -> Void

	public init(
		create: @escaping @Sendable (Game, Database) throws -> Void,
		update: @escaping @Sendable (Game, Database) throws -> Void,
		delete: @escaping @Sendable (Game, Database) throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension GamesPersistenceService: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		update: { _, _ in fatalError("\(Self.self).update") },
		delete: { _, _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var gamesPersistenceService: GamesPersistenceService {
		get { self[GamesPersistenceService.self] }
		set { self[GamesPersistenceService.self] = newValue }
	}
}
