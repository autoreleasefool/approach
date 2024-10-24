import Foundation
import ImportExportServiceInterface

protocol DataImporter {
	func startImport(of: URL, to: URL) async throws -> ImportResult
}
