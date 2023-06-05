import Foundation
import GRDB

public enum FetchableError: Error, LocalizedError {
	case recordNotFound(type: Any.Type, id: Any)

	public var errorDescription: String? {
		switch self {
		case let .recordNotFound(type, id):
			return "Could not find ID '\(id)' for \(type)"
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
