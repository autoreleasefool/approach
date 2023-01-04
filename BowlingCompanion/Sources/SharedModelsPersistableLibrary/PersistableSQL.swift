import GRDB

public protocol PersistableSQL {
	func update(_ db: Database) throws
}
