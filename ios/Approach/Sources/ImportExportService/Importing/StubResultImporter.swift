import Foundation
import ImportExportServiceInterface

struct StubResultImporter: DataImporter {
	let result: ImportResult

	func startImport(of: URL, to: URL) async throws -> ImportResult {
		return result
	}
}
