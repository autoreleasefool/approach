import DatabaseServiceInterface
import DateTimeLibrary
import Dependencies
import FileManagerPackageServiceInterface
import Foundation
import GRDB
import ImportExportServiceInterface

extension ImportService: DependencyKey {
	public static var liveValue: Self {
		let latestBackupUrl = LockIsolated<URL?>(nil)

		@Sendable func getBackupDirectory() throws -> URL {
			@Dependency(\.fileManager) var fileManager
			let backupDirectory = try fileManager
				.getUserDirectory()
				.appending(path: "backup", directoryHint: .isDirectory)

			let exists = try fileManager.exists(backupDirectory)
			if !exists {
				try fileManager.createDirectory(backupDirectory)
			}

			return backupDirectory
		}

		@Sendable func getLatestBackup() throws -> URL? {
			@Dependency(\.fileManager) var fileManager
			return try fileManager
				.contentsOfDirectory(at: getBackupDirectory())
				.sorted { $0.path() < $1.path() }
				.last
		}

		@Sendable func backupExistingDatabase() async throws {
			@Dependency(\.fileManager) var fileManager
			@Dependency(ExportService.self) var export

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
			let backupFile = try getBackupDirectory()
				.appending(path: fileName)

			try fileManager.removeIfExists(backupFile)
			try fileManager.moveItem(at: exportUrl, to: backupFile)
			latestBackupUrl.setValue(backupFile)
		}

		@Sendable func importDatabase(fromUrl: URL, performBackup: Bool) async throws -> ImportResult {
			@Dependency(\.database) var database
			@Dependency(\.fileManager) var fileManager

			if performBackup {
				try await backupExistingDatabase()
			}

			// Copy the file to import to a temporary cache file
			let temporaryImportUrl = try fileManager
				.getTemporaryDirectory()
				.appending(path: "importedFile.tmp")
			defer { try? fileManager.removeIfExists(temporaryImportUrl) }

			try fileManager.removeIfExists(temporaryImportUrl)
			try fileManager.copyItem(at: fromUrl, to: temporaryImportUrl)

			let importedDbUrl = try fileManager
				.getTemporaryDirectory()
				.appending(path: "importedDb.tmp")
			defer { try? fileManager.removeIfExists(importedDbUrl) }

			let result: ImportResult
			let fileType = try FileType.of(url: temporaryImportUrl)
			 switch fileType {
			case .sqlite:
				let dbType = try await DatabaseFormat.of(url: temporaryImportUrl)
				guard let importer = dbType?.getImporter() else { return .unrecognized }
				result = try await importer.startImport(of: temporaryImportUrl, to: importedDbUrl)
			case .none:
				return .unrecognized
			}

			// Delete existing DB
			try database.close()
			defer { database.initialize() }

			// Once the imported database is ready, replace the existing DB
			for dbUrlItem in database.dbUrl().relativeSQLiteFileUrls {
				try fileManager.removeIfExists(dbUrlItem)
			}
			try fileManager.copyItem(at: importedDbUrl, to: database.dbUrl())

			return result
		}

		return Self(
			getLatestBackupDate: {
				@Dependency(\.fileManager) var fileManager

				return try latestBackupUrl.withValue {
					let latest: URL? = if let latestUrl = $0 {
						latestUrl
					} else {
						try getLatestBackup()
					}

					$0 = latest

					if let latest {
						let attributes = try fileManager.attributesOfItem(atPath: latest.path())
						if let modificationDate = attributes[FileAttributeKey.modificationDate] as? Date {
							return modificationDate
						}
					}

					return nil
				}
			},
			restoreBackup: {
				guard let backupUrl = try getLatestBackup() else {
					throw ServiceError.backupDoesNotExist
				}

				_ = try await importDatabase(fromUrl: backupUrl, performBackup: false)
			},
			importDatabase: { url in
				guard url.startAccessingSecurityScopedResource() else {
					throw ServiceError.cannotAccessResource
				}
				defer { url.stopAccessingSecurityScopedResource() }

				return try await importDatabase(fromUrl: url, performBackup: true)
			}
		)
	}
}

extension URL {
	var relativeSQLiteFileUrls: [URL] {
		[
			self,
			self.deletingLastPathComponent().appending(path: self.lastPathComponent + "-shm"),
			self.deletingLastPathComponent().appending(path: self.lastPathComponent + "-wal"),
		]
	}
}
