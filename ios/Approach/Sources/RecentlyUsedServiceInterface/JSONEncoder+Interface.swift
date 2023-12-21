import Dependencies
import Foundation

public struct JSONEncoderService: Sendable {
	public var encode: @Sendable (Encodable) throws -> Data

	public init(encode: @escaping @Sendable (Encodable) throws -> Data) {
		self.encode = encode
	}
}

extension JSONEncoderService: TestDependencyKey {
	public static var testValue = Self(
		encode: { _ in unimplemented("\(Self.self).encode") }
	)
}

extension DependencyValues {
	public var jsonEncoder: JSONEncoderService {
		get { self[JSONEncoderService.self] }
		set { self[JSONEncoderService.self] = newValue }
	}
}
