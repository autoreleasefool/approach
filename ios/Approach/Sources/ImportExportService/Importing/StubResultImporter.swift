import Foundation
import ImportExportServiceInterface

struct StubResultImporter: DataImporter {
	let result: ImportResult

	func startImport(of _: URL, to _: URL) async throws -> ImportResult {
		result
	}
}
