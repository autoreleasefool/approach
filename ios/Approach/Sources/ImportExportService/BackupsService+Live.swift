import DatabaseServiceInterface
import Dependencies
import FileManagerPackageServiceInterface
import Foundation
import ImportExportServiceInterface
import PreferenceServiceInterface

extension BackupsService: DependencyKey {
	public static var liveValue: Self {
		let coordinatorId = LockIsolated<FileCoordinatorID?>(nil)
		@Sendable func getCoordinatorId() -> FileCoordinatorID {
			coordinatorId.withValue {
				if let id = $0 {
					return id
				} else {
					@Dependency(\.fileCoordinator) var fileCoordinator
					let id = fileCoordinator.createCoordinator()
					$0 = id
					return id
				}
			}
		}

		@Sendable func getBackupDirectory() async throws -> URL? {
			@Dependency(\.fileManager) var fileManager
			@Dependency(\.fileCoordinator) var fileCoordinator

			guard let containerUrl = fileManager.urlForUbiquityContainerIdentifier(nil) else {
				return nil
			}

			let directory = containerUrl.appending(path: "Backups")
			let directoryExists = try fileManager.exists(directory)
			if !directoryExists {
				try await fileCoordinator.write(itemAt: directory, withCoordinator: getCoordinatorId(), options: []) {
					try fileManager.createDirectory($0)
				}
			}

			return directory
		}

		return Self(
			lastSuccessfulBackupDate: {
				@Dependency(\.preferences) var preferences
				guard let lastBackupDate = preferences.double(forKey: .dataLastBackupDate) else { return nil }
				return Date(timeIntervalSince1970: lastBackupDate)
			},
			listBackups: {
				@Dependency(\.fileCoordinator) var fileCoordinator
				@Dependency(\.fileManager) var fileManager

				guard let backupsDirectory = try await getBackupDirectory() else { return [] }
				let backups = LockIsolated<[BackupFile]>([])

				try await fileCoordinator.read(
					itemAt: backupsDirectory,
					withCoordinator: getCoordinatorId(),
					options: []
				) { url in
					let contents = try fileManager.contentsOfDirectory(at: url)
					let attributes = try contents.map {
						try fileManager.attributesOfItem(atPath: $0.path())
					}

					let backupsToReturn: [BackupFile] = zip(contents, attributes).compactMap { url, attributes in
						guard let dateCreated = attributes[.creationDate] as? Date else { return nil }
						return BackupFile(url: url, dateCreated: dateCreated)
					}

					backups.setValue(backupsToReturn)
				}

				return backups.value
			},
			createBackup: {
				@Dependency(\.date) var date
				@Dependency(ExportService.self) var export
				@Dependency(\.fileCoordinator) var fileCoordinator
				@Dependency(\.fileManager) var fileManager
				@Dependency(\.preferences) var preferences

				guard let backupsDirectory = try await getBackupDirectory() else { return }

				var exportUrl: URL?
				for try await event in export.exportDatabase() {
					guard case let .response(url) = event else {
						continue
					}

					exportUrl = url
					break
				}

				guard let exportUrl else { return }

				let fileName = exportUrl.lastPathComponent
				let backupFile = backupsDirectory.appending(path: fileName)

				try await fileCoordinator.write(
					itemAt: backupFile,
					withCoordinator: getCoordinatorId(),
					options: []
				) { url in
					try fileManager.copyItem(at: exportUrl, to: url)
				}

				preferences.setDouble(forKey: .dataLastBackupDate, to: date().timeIntervalSince1970)
			}
		)
	}
}
