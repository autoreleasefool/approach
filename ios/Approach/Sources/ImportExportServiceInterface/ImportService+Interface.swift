import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct ImportService: Sendable {
	public var getLatestBackupDate: @Sendable () async throws -> Date?
	public var restoreBackup: @Sendable () async throws -> Void
	public var importDatabase: @Sendable (_ fromUrl: URL) async throws -> Void
}

extension ImportService: TestDependencyKey {
	public static var testValue: Self { Self() }
}

extension ImportService {
	public enum ServiceError: Error {
		case cannotAccessResource
		case backupDoesNotExist
	}
}
