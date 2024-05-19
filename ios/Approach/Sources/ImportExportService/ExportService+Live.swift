import DatabaseServiceInterface
import DateTimeLibrary
import Dependencies
import FileManagerPackageServiceInterface
import GRDB
import ImportExportServiceInterface

extension ExportService: DependencyKey {
	public static var liveValue: Self = {
		let exportCache = LockIsolated<Set<String>>([])

		return Self(
			exportDatabase: {
				.init { continuation in
					@Dependency(DatabaseService.self) var database
					@Dependency(\.date) var date
					@Dependency(\.fileManager) var fileManager

					do {
						let backupFileName = "approach_data_\(date().machineDateFormat).sqlite"
						let backupUrl = try fileManager
							.getTemporaryDirectory()
							.appending(path: backupFileName)

						_ = exportCache.withValue { $0.insert(backupFileName) }

						try database.writer().backup(to: DatabaseQueue(path: backupUrl.absoluteString)) {
							continuation.yield(.progress(stepsComplete: $0.completedPageCount, totalSteps: $0.totalPageCount))
						}

						continuation.yield(.response(backupUrl))
					} catch {
						continuation.finish(throwing: error)
					}
				}
			},
			cleanUp: {
				@Dependency(\.fileManager) var fileManager

				exportCache.withValue {
					for fileName in $0 {
						do {
							try fileManager.remove(fileManager.getTemporaryDirectory().appending(path: fileName))
						} catch {}
					}

					$0.removeAll()
				}
			}
		)
	}()
}
