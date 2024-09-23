import Dependencies
import Foundation

public struct JSONEncoderService: Sendable {
	public var encode: @Sendable (any Encodable) throws -> Data

	public init(encode: @escaping @Sendable (Encodable) throws -> Data) {
		self.encode = encode
	}

	public init(_ encoder: JSONEncoder) {
		self.init {
			try encoder.encode($0)
		}
	}
}

extension JSONEncoderService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			encode: { _ in unimplemented("\(Self.self).encode", placeholder: Data()) }
		)
	}
}
