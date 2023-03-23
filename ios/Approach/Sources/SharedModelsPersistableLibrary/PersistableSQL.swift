import GRDB

public protocol PersistableSQL {
	func save(_ db: Database) throws
}
