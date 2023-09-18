import DatabaseServiceInterface
import Dependencies
import FileManagerServiceInterface
import GRDB
import ImportExportServiceInterface

extension ExportService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			exportDatabase: {
				.init { continuation in
					@Dependency(\.database) var database
					@Dependency(\.fileManager) var fileManager

					do {
						let backupDirectory = try fileManager
							.getUserDirectory()
							.appending(path: "backup-database", directoryHint: .isDirectory)

						try fileManager.createDirectory(backupDirectory)

						let backupUrl = backupDirectory.appending(path: "db.sqlite")

						try database.writer().backup(to: DatabaseQueue(path: backupUrl.absoluteString)) {
							continuation.yield(.progress(stepsComplete: $0.completedPageCount, totalSteps: $0.totalPageCount))
						}

						let zippedContents = try fileManager.zipContents(ofUrls: [backupUrl], to: "approach_data.zip")

						try fileManager.remove(backupUrl)

						continuation.yield(.response(zippedContents))
					} catch {
						continuation.finish(throwing: error)
					}
				}
			}
		)
	}()
}
