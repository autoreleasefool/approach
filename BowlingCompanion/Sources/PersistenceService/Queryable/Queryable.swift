import GRDB

protocol ManyQueryable {
	associatedtype Model

	@Sendable func fetchValues(_ db: Database) throws -> [Model]
}

protocol SingleQueryable {
	associatedtype Model

	@Sendable func fetchValue(_ db: Database) throws -> Model?
}
