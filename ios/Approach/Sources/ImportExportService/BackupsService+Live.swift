import DatabaseServiceInterface
import Dependencies
import FeatureFlagsLibrary
import FileManagerPackageServiceInterface
import Foundation
import ImportExportServiceInterface
import PreferenceServiceInterface

extension BackupsService: DependencyKey {
	public static var liveValue: Self {
		@Sendable func isEnabled() -> Bool {
			@Dependency(\.featureFlags) var featureFlags
			@Dependency(\.fileManager) var fileManager
			@Dependency(\.preferences) var preferences

			return featureFlags.isFlagEnabled(.automaticBackups) &&
				fileManager.ubiquityIdentityToken() != nil &&
				(preferences.bool(forKey: .dataICloudBackupEnabled) ?? true)
		}

		@Sendable func getNewCoordinatorId() -> FileCoordinatorID {
			@Dependency(\.fileCoordinator) var fileCoordinator
			return fileCoordinator.createCoordinator()
		}

		@Sendable func getBackupDirectory() async throws -> URL? {
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

		@Sendable func getBackupFile(for url: URL, attributes: [FileAttributeKey: Any]) -> BackupFile? {
			guard let dateCreated = attributes[.creationDate] as? Date,
						let fileSize = attributes[.size] as? NSNumber else { return nil }
			return BackupFile(url: url, dateCreated: dateCreated, fileSizeBytes: Int(truncating: fileSize))
		}

		return Self(
			isEnabled: isEnabled,
			lastSuccessfulBackupDate: {
				@Dependency(\.preferences) var preferences
				guard let lastBackupDate = preferences.double(forKey: .dataLastBackupDate) else { return nil }
				return Date(timeIntervalSince1970: lastBackupDate)
			},
			listBackups: {
				guard isEnabled() else { return [] }

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
					let contents = try fileManager.contentsOfDirectory(at: url)
					let attributes = try contents.map {
						try fileManager.attributesOfItem(atPath: $0.path())
					}

					let backupsToReturn: [BackupFile] = zip(contents, attributes).compactMap { url, attributes in
						getBackupFile(for: url, attributes: attributes)
					}

					backups.setValue(backupsToReturn)
				}

				return backups.value
			},
			createBackup: {
				guard isEnabled() else { return nil }

				@Dependency(\.date) var date
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
					let attributes = try fileManager.attributesOfItem(atPath: url.path())
					let resultFile = getBackupFile(for: url, attributes: attributes)
					result.setValue(resultFile)
				}

				preferences.setDouble(forKey: .dataLastBackupDate, to: date().timeIntervalSince1970)
				return result.value
			}
		)
	}
}
