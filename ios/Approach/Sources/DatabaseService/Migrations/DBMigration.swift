import GRDB

protocol DBMigration {
	static var identifier: String { get }
	static func migrate(_ db: Database) throws
}

extension DBMigration {
	static var identifier: String { "\(Self.self)" }
}

extension DatabaseMigrator {
	mutating func registerMigration(_ migration: DBMigration.Type) {
		self.registerMigration(migration.identifier, migrate: migration.migrate(_:))
	}
}
