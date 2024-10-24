import GRDB

protocol SQLiteImportStep: Sendable {
	func performImport(from: Database, to: Database) throws
}
