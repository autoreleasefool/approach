import Foundation
import ImportExportServiceInterface

struct StubResultImporter: Importer {
	let result: ImportResult

	func startImport(of: URL, to: URL) async throws -> ImportResult {
		return result
	}
}
