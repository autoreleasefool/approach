import GRDB

protocol Fetchable {
	associatedtype Model: FetchableRecord

	func fetchValues(_ db: Database) throws -> [Model]
}
