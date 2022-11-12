import GRDB

protocol Queryable {
	associatedtype Model: FetchableRecord

	func fetchValues(_ db: Database) throws -> [Model]
}
