import GRDB

protocol Queryable {
	associatedtype Model: FetchableRecord

	@Sendable func fetchValues(_ db: Database) throws -> [Model]
}
