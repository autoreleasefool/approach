import Dependencies
import Foundation

public struct DecoderService: Sendable {
	public var decode: @Sendable (any Decodable.Type, Data) throws -> any Decodable

	public init(
		decode: @escaping @Sendable (any Decodable.Type, Data) throws -> any Decodable
	) {
		self.decode = decode
	}

	public init(_ decoder: JSONDecoder) {
		self.init {
			try decoder.decode($0, from: $1)
		}
	}

	public func decode<T: Decodable>(_ type: T.Type, from data: Data) throws -> T {
		let decoded = try decode(type, data)
		guard let decoded = decoded as? T else {
			throw DecoderServiceError.failedToCast(description: String(describing: decoded), to: T.self)
		}

		return decoded
	}
}

extension DecoderService: TestDependencyKey {
	public static var testValue: DecoderService {
		DecoderService(
			decode: { _, _ in
				struct Stub: Decodable {}
				return unimplemented("\(Self.self).decode", placeholder: Stub())
			}
		)
	}
}

public enum DecoderServiceError: Error, LocalizedError {
	case failedToCast(description: String, to: Any.Type)

	public var errorDescription: String? {
		switch self {
		case let .failedToCast(description, to):
			"Failed to cast '\(description)' to '\(to)'"
		}
	}
}
