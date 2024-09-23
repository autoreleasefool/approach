import Dependencies
import DependenciesMacros
import Foundation
import GRDB
@_exported import GRDBDatabasePackageServiceInterface

@DependencyClient
public struct DatabaseService: Sendable {
	public var initialize: @Sendable () -> Void
	public var dbUrl: @Sendable () -> URL = { unimplemented("\(Self.self).dbUrl", placeholder: .temporaryDirectory) }
	public var close: @Sendable () throws -> Void
	public var reader: @Sendable () -> DatabaseReader = { { fatalError("\(Self.self).reader not implemented") }() }
	public var writer: @Sendable () -> DatabaseWriter = { { fatalError("\(Self.self).writer not implemented") }() }
}

extension DatabaseService: TestDependencyKey {
	public static var testValue: Self { Self() }
}

extension DependencyValues {
	public var database: DatabaseService {
		get { self[DatabaseService.self] }
		set { self[DatabaseService.self] = newValue }
	}
}
