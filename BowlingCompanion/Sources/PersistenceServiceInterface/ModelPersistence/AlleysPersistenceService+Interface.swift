import Dependencies
import GRDB
import SharedModelsLibrary

public struct AlleysPersistenceService: Sendable {
	public var create: @Sendable (Alley, Database) throws -> Void
	public var update: @Sendable (Alley, Database) throws -> Void
	public var delete: @Sendable (Alley, Database) throws -> Void

	public init(
		create: @escaping @Sendable (Alley, Database) throws -> Void,
		update: @escaping @Sendable (Alley, Database) throws -> Void,
		delete: @escaping @Sendable (Alley, Database) throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension AlleysPersistenceService: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		update: { _, _ in fatalError("\(Self.self).update") },
		delete: { _, _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var alleysPersistenceService: AlleysPersistenceService {
		get { self[AlleysPersistenceService.self] }
		set { self[AlleysPersistenceService.self] = newValue }
	}
}
