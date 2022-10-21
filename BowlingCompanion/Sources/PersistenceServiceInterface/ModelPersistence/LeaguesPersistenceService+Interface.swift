import Dependencies
import GRDB
import SharedModelsLibrary

public struct LeaguesPersistenceService: Sendable {
	public var create: @Sendable (League, Database) throws -> Void
	public var update: @Sendable (League, Database) throws -> Void
	public var delete: @Sendable (League, Database) throws -> Void

	public init(
		create: @escaping @Sendable (League, Database) throws -> Void,
		update: @escaping @Sendable (League, Database) throws -> Void,
		delete: @escaping @Sendable (League, Database) throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension LeaguesPersistenceService: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		update: { _, _ in fatalError("\(Self.self).update") },
		delete: { _, _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var leaguesPersistenceService: LeaguesPersistenceService {
		get { self[LeaguesPersistenceService.self] }
		set { self[LeaguesPersistenceService.self] = newValue }
	}
}
