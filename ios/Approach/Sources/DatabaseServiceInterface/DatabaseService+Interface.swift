import Dependencies
import DependenciesMacros
import GRDB
@_exported import GRDBDatabasePackageServiceInterface

@DependencyClient
public struct DatabaseService: Sendable {
	public var reader: @Sendable () -> DatabaseReader = { unimplemented("\(Self.self).reader") }
	public var writer: @Sendable () -> DatabaseWriter = { unimplemented("\(Self.self).writer") }
}

extension DatabaseService: TestDependencyKey {
	public static var testValue = Self()
}

extension DependencyValues {
	public var database: DatabaseService {
		get { self[DatabaseService.self] }
		set { self[DatabaseService.self] = newValue }
	}
}
