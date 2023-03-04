import GRDB

protocol Migration {
	static var identifier: String { get }
	static func migrate(_ db: Database) throws
}

extension Migration {
	static var identifier: String { "\(Self.self)" }
}

extension DatabaseMigrator {
	mutating func registerMigration(_ migration: Migration.Type) {
		self.registerMigration(migration.identifier, migrate: migration.migrate(_:))
	}
}
