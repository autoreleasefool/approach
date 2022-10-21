import Dependencies
import GRDB
import SharedModelsLibrary

public struct SeriesPersistenceService: Sendable {
	public var create: @Sendable (Series, Database) throws -> Void
	public var update: @Sendable (Series, Database) throws -> Void
	public var delete: @Sendable (Series, Database) throws -> Void

	public init(
		create: @escaping @Sendable (Series, Database) throws -> Void,
		update: @escaping @Sendable (Series, Database) throws -> Void,
		delete: @escaping @Sendable (Series, Database) throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension SeriesPersistenceService: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		update: { _, _ in fatalError("\(Self.self).update") },
		delete: { _, _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var seriesPersistenceService: SeriesPersistenceService {
		get { self[SeriesPersistenceService.self] }
		set { self[SeriesPersistenceService.self] = newValue }
	}
}
