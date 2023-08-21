import Foundation
import GRDB

public enum FetchableError: Error, LocalizedError {
	case recordNotFound(type: Any.Type, id: Any)
	case fetchRequestFailed(type: Any.Type, statement: String)

	public var errorDescription: String? {
		switch self {
		case let .recordNotFound(type, id):
			return "Could not find ID '\(id)' for \(type)"
		case let .fetchRequestFailed(type, statement):
			return "Failed to resolve request for \(type): \(statement)"
		}
	}
}

extension FetchableRecord where Self: TableRecord & Identifiable, ID: DatabaseValueConvertible {
	public static func fetchOneGuaranteed(_ db: Database, id: ID) throws -> Self {
		guard let result = try filter(id: id).fetchOne(db) else {
			throw FetchableError.recordNotFound(type: Self.self, id: id)
		}
		return result
	}
}

extension FetchableRecord {
	public static func fetchOneGuaranteed(_ db: Database, _ request: some FetchRequest) throws -> Self {
		guard let result = try Self.fetchOne(db, request) else {
			let request = try request.makePreparedRequest(db, forSingleResult: true)
			throw FetchableError.fetchRequestFailed(type: Self.self, statement: request.statement.description)
		}
		return result
	}
}

extension FetchRequest where RowDecoder: FetchableRecord {
	public func fetchOneGuaranteed(_ db: Database) throws -> RowDecoder {
		guard let result = try RowDecoder.fetchOne(db, self) else {
			let request = try self.makePreparedRequest(db, forSingleResult: true)
			throw FetchableError.fetchRequestFailed(type: RowDecoder.self, statement: request.statement.description)
		}
		return result
	}
}
