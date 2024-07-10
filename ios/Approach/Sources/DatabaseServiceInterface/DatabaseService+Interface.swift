import Dependencies
import DependenciesMacros
import Foundation
import GRDB
@_exported import GRDBDatabasePackageServiceInterface

@DependencyClient
public struct DatabaseService: Sendable {
	public var initialize: @Sendable () -> Void
	public var dbUrl: @Sendable () -> URL = { unimplemented("\(Self.self).dbUrl") }
	public var close: @Sendable () throws -> Void
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
