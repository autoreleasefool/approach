import GRDB

protocol Queryable {
	associatedtype Model

	@Sendable func fetchValues(_ db: Database) throws -> [Model]
}
