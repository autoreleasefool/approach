import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct BackupsService: Sendable {
	public var isEnabled: @Sendable () -> Bool = { unimplemented("\(Self.self).isEnabled", placeholder: false) }
	public var checkIsServiceAvailable: @Sendable () async throws -> Void
	public var lastSuccessfulBackupDate: @Sendable () -> Date?
	public var listBackups: @Sendable () async throws -> [BackupFile]
	public var createBackup: @Sendable (_ skipIfWithinMinimumTime: Bool) async throws -> BackupFile?
	public var restoreBackup: @Sendable (_ fromUrl: URL) async throws -> Void
	public var deleteBackup: @Sendable (_ fromUrl: URL) async throws -> Void
	public var cleanUp: @Sendable () async throws -> Void
}

extension BackupsService {
	public static let MINIMUM_SECONDS_BETWEEN_BACKUPS: TimeInterval = 60 * 60 * 24 * 14
	public static let MAXIMUM_SECONDS_BACKUP_AGE: TimeInterval = 60 * 60 * 24 * 90
	public static let MAXIMUM_TOTAL_BACKUP_SIZE_BYTES: Int = 1_024 * 1_024 * 100 // 100 MB

	public enum ServiceError: Error {
		case failedToAccessDirectory
		case failedToCreateExport
		case serviceDisabled
	}

	public enum ServiceAvailableError: Error {
		case icloudUnavailable
		case backupsDisabled
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
