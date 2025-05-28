import DatabaseServiceInterface
import Dependencies
import FeatureFlagsLibrary
import FileManagerPackageServiceInterface
import Foundation
import ImportExportServiceInterface
import PreferenceServiceInterface

extension BackupsService: DependencyKey {
	public static var liveValue: Self {
		@Sendable
		func isICloudEnabled() -> Bool {
			@Dependency(\.fileManager) var fileManager
			return fileManager.ubiquityIdentityToken() != nil
		}

		@Sendable
		func isICloudBackupsEnabled() -> Bool {
			@Dependency(\.preferences) var preferences
			return preferences.bool(forKey: .dataICloudBackupEnabled) ?? true
		}

		@Sendable
		func isEnabled() -> Bool {
			@Dependency(\.featureFlags) var featureFlags

			let isAutomaticBackupsEnabled = featureFlags.isFlagEnabled(.automaticBackups)
			let isCloudEnabled = isICloudEnabled()
			let isICloudBackupsEnabled = isICloudBackupsEnabled()

			return isAutomaticBackupsEnabled &&
				isCloudEnabled &&
				isICloudBackupsEnabled
		}

		@Sendable
		func checkIsServiceAvailable() async throws {
			if !isICloudEnabled() {
				throw ServiceAvailableError.icloudUnavailable
			}

			if !isICloudBackupsEnabled() {
				throw ServiceAvailableError.backupsDisabled
			}
		}

		@Sendable
		func lastSuccessfulBackupDate() -> Date? {
			@Dependency(\.preferences) var preferences
			guard let lastBackupDate = preferences.double(forKey: .dataLastBackupDate) else { return nil }
			return Date(timeIntervalSince1970: lastBackupDate)
		}

		@Sendable
		func getNewCoordinatorId() -> FileCoordinatorID {
			@Dependency(\.fileCoordinator) var fileCoordinator
			return fileCoordinator.createCoordinator()
		}

		@Sendable
		func getBackupDirectory() async throws -> URL? {
			@Dependency(\.fileManager) var fileManager
			@Dependency(\.fileCoordinator) var fileCoordinator

			guard let containerUrl = fileManager.urlForUbiquityContainerIdentifier(nil) else {
				return nil
			}

			let coordinatorId = getNewCoordinatorId()
			defer { fileCoordinator.discardCoordinator(coordinatorId) }

			let directory = containerUrl.appending(path: "Documents").appending(path: "Backups")
			let directoryExists = try fileManager.exists(directory)
			if !directoryExists {
				try await fileCoordinator.write(itemAt: directory, withCoordinator: coordinatorId, options: []) {
					try fileManager.createDirectory($0)
				}
			}

			return directory
		}

		@Sendable
		func getBackupFile(for url: URL) throws -> BackupFile? {
			let resourceValues = try url.resourceValues(forKeys: [.creationDateKey, .fileSizeKey])
			guard let dateCreated = resourceValues.creationDate, let fileSize = resourceValues.fileSize else { return nil }
			return BackupFile(url: url, dateCreated: dateCreated, fileSizeBytes: fileSize)
		}

		@Sendable
		func getTemporaryImportUrl() throws -> URL {
			@Dependency(\.fileManager) var fileManager
			return try fileManager
				.getTemporaryDirectory()
				.appending(path: "import.tmp")
		}

		@Sendable
		func listBackups() async throws -> [BackupFile] {
			guard isEnabled() else {
				throw ServiceError.serviceDisabled
			}

			@Dependency(\.fileCoordinator) var fileCoordinator
			@Dependency(\.fileManager) var fileManager

			guard let backupsDirectory = try await getBackupDirectory() else { return [] }
			let backups = LockIsolated<[BackupFile]>([])

			let coordinatorId = getNewCoordinatorId()
			defer { fileCoordinator.discardCoordinator(coordinatorId) }

			try await fileCoordinator.read(
				itemAt: backupsDirectory,
				withCoordinator: coordinatorId,
				options: []
			) { url in
				let contents = try fileManager.contentsOfDirectory(
					at: url,
					includingPropertiesForKeys: [.fileSizeKey, .creationDateKey],
					options: []
				)

				let backupsToReturn: [BackupFile] = try contents
					.compactMap { try getBackupFile(for: $0) }
					.sorted(by: { $0.dateCreated < $1.dateCreated })
					.reversed()

				backups.setValue(backupsToReturn)
			}

			return backups.value
		}

		return Self(
			isEnabled: isEnabled,
			checkIsServiceAvailable: checkIsServiceAvailable,
			lastSuccessfulBackupDate: lastSuccessfulBackupDate,
			listBackups: listBackups,
			createBackup: { skipIfWithinMinimumTime in
				guard isEnabled() else {
					throw ServiceError.serviceDisabled
				}

				@Dependency(\.date) var date

				if skipIfWithinMinimumTime {
					@Dependency(\.calendar) var calendar

					let lastBackupDate = lastSuccessfulBackupDate() ?? .distantPast

					let dayOfBackup = calendar.startOfDay(for: lastBackupDate)
					guard date().timeIntervalSince(dayOfBackup) > BackupsService.MINIMUM_SECONDS_BETWEEN_BACKUPS else {
						return nil
					}
				}

				@Dependency(ExportService.self) var export
				@Dependency(\.fileCoordinator) var fileCoordinator
				@Dependency(\.fileManager) var fileManager
				@Dependency(\.preferences) var preferences

				guard let backupsDirectory = try await getBackupDirectory() else {
					throw BackupsService.ServiceError.failedToAccessDirectory
				}

				let coordinatorId = getNewCoordinatorId()
				defer { fileCoordinator.discardCoordinator(coordinatorId) }

				var exportUrl: URL?
				for try await event in export.exportDatabase() {
					guard case let .response(url) = event else {
						continue
					}

					exportUrl = url
					break
				}

				guard let exportUrl else {
					throw BackupsService.ServiceError.failedToCreateExport
				}

				let fileName = exportUrl.lastPathComponent

				let backupFile = backupsDirectory.appending(path: fileName)
				let result = LockIsolated<BackupFile?>(nil)

				try await fileCoordinator.write(
					itemAt: backupFile,
					withCoordinator: coordinatorId,
					options: []
				) { url in
					try fileManager.moveItem(at: exportUrl, to: url)
					let resultFile = try getBackupFile(for: url)
					result.setValue(resultFile)
				}

				preferences.setDouble(forKey: .dataLastBackupDate, to: date().timeIntervalSince1970)
				return result.value
			},
			restoreBackup: { url in
				@Dependency(\.database) var database
				@Dependency(\.fileCoordinator) var fileCoordinator
				@Dependency(\.fileManager) var fileManager

				let coordinatorId = getNewCoordinatorId()
				defer { fileCoordinator.discardCoordinator(coordinatorId) }

				try database.close()
				defer { database.initialize() }

				let temporaryImportUrl = try getTemporaryImportUrl()
				try fileManager.removeIfExists(temporaryImportUrl)

				for dbUrlItem in database.dbUrl().relativeSQLiteFileUrls {
					try fileManager.removeIfExists(dbUrlItem)
				}

				try await fileCoordinator.read(
					itemAt: url,
					withCoordinator: coordinatorId,
					options: []
				) { url in
					try fileManager.copyItem(at: url, to: temporaryImportUrl)
				}

				switch try FileType.of(url: temporaryImportUrl) {
				case .sqlite:
					try fileManager.copyItem(at: temporaryImportUrl, to: database.dbUrl())
				case .zip: break
				case .none: break
				}
			},
			deleteBackup: { url in
				@Dependency(\.fileCoordinator) var fileCoordinator
				@Dependency(\.fileManager) var fileManager

				let coordinatorId = getNewCoordinatorId()
				defer { fileCoordinator.discardCoordinator(coordinatorId) }

				try await fileCoordinator.write(
					itemAt: url,
					withCoordinator: coordinatorId,
					options: [.forDeleting]
				) { url in
					try fileManager.remove(url)
				}
			},
			cleanUp: {
				@Dependency(\.date) var date

				let backups = try await listBackups()
					.sorted(by: { $0.dateCreated < $1.dateCreated })

				// There should always be at least 1 backup, so exit if we only have 1 or fewer
				guard backups.count > 1 else { return }

				var backupsToRemove: Set<URL> = []

				// Only keep the most recent backups until we reach the maximum size
				var cumulativeSize = 0

				// Drop most recent backup, so we always keep at least 1
				for file in backups.dropFirst() {
					cumulativeSize += file.fileSizeBytes
					if cumulativeSize > BackupsService.MAXIMUM_TOTAL_BACKUP_SIZE_BYTES {
						backupsToRemove.insert(file.url)
					}

					// If a backup is older than the maximum age, remove it
					if date().timeIntervalSince(file.dateCreated) > BackupsService.MAXIMUM_SECONDS_BACKUP_AGE {
						backupsToRemove.insert(file.url)
					}
				}

				guard !backupsToRemove.isEmpty else { return }

				// Remove the backups
				@Dependency(\.fileManager) var fileManager
				@Dependency(\.fileCoordinator) var fileCoordinator

				let coordinatorId = getNewCoordinatorId()
				defer { fileCoordinator.discardCoordinator(coordinatorId) }

				for backupUrl in backupsToRemove {
					try await fileCoordinator.write(
						itemAt: backupUrl,
						withCoordinator: coordinatorId,
						options: [.forDeleting]
					) { url in
						try fileManager.remove(url)
					}
				}
			}
		)
	}
}
