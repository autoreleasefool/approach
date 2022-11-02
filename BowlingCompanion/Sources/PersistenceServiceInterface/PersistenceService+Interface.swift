import Dependencies
import GRDB

public struct PersistenceService: Sendable {
	public var reader: @Sendable () -> DatabaseReader
	public var write: @Sendable (@escaping (Database) throws -> Void) async throws -> Void

	public init(
		reader: @escaping @Sendable () -> DatabaseReader,
		write: @escaping @Sendable (@escaping (Database) throws -> Void) async throws -> Void
	) {
		self.reader = reader
		self.write = write
	}
}

extension PersistenceService: TestDependencyKey {
	public static var testValue = Self(
		reader: { fatalError("\(Self.self).reader") },
		write: { _ in fatalError("\(Self.self).write") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
