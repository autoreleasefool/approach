import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct BackupsService: Sendable {
	public var lastSuccessfulBackupDate: @Sendable () -> Date?
	public var listBackups: @Sendable () async throws -> [BackupFile]
	public var createBackup: @Sendable () async throws -> Void
}

extension BackupsService: TestDependencyKey {
	public static var testValue: Self { Self() }
}

public struct BackupFile: Identifiable, Sendable {
	public let url: URL
	public let dateCreated: Date

	public var id: URL { url }

	public init(url: URL, dateCreated: Date) {
		self.url = url
		self.dateCreated = dateCreated
	}
}
