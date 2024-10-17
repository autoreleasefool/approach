import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct BackupsService: Sendable {
	public var isEnabled: @Sendable () -> Bool = { unimplemented("\(Self.self).isEnabled", placeholder: false) }
	public var lastSuccessfulBackupDate: @Sendable () -> Date?
	public var listBackups: @Sendable () async throws -> [BackupFile]
	public var createBackup: @Sendable () async throws -> BackupFile?
}

extension BackupsService {
	public static let MINIMUM_SECONDS_BETWEEN_BACKUPS: TimeInterval = 60 * 60 * 24 * 14

	public enum ServiceError: Error {
		case failedToAccessDirectory
		case failedToCreateExport
	}
}

extension BackupsService: TestDependencyKey {
	public static var testValue: Self { Self() }
}

public struct BackupFile: Identifiable, Equatable, Sendable {
	public let url: URL
	public let dateCreated: Date
	public let fileSizeBytes: Int

	public var id: URL { url }

	public init(url: URL, dateCreated: Date, fileSizeBytes: Int) {
		self.url = url
		self.dateCreated = dateCreated
		self.fileSizeBytes = fileSizeBytes
	}
}
