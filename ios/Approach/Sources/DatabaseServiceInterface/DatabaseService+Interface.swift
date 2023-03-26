import Dependencies
import GRDB

public struct DatabaseService: Sendable {
	public var reader: @Sendable () -> DatabaseReader
	public var writer: @Sendable () -> DatabaseWriter

	public init(
		reader: @escaping @Sendable () -> DatabaseReader,
		writer: @escaping @Sendable () -> DatabaseWriter
	) {
		self.reader = reader
		self.writer = writer
	}
}

extension DatabaseService: TestDependencyKey {
	public static var testValue = Self(
		reader: { unimplemented("\(Self.self).reader") },
		writer: { unimplemented("\(Self.self).writer") }
	)
}

extension DependencyValues {
	public var database: DatabaseService {
		get { self[DatabaseService.self] }
		set { self[DatabaseService.self] = newValue }
	}
}
