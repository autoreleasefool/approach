import Foundation
import ImportExportServiceInterface

protocol Importer {
	func startImport(of: URL, to: URL) async throws -> ImportResult
}
