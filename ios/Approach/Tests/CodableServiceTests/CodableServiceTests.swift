@testable import CodableService
@testable import CodableServiceInterface
import Dependencies
import Foundation
import Testing
import TestUtilitiesLibrary

@Suite("CodableService", .tags(.service))
struct CodableServiceTests {
	@Dependency(DecoderService.self) var decoder
	@Dependency(EncoderService.self) var encoder

	@Test("Encodes and decodes", .tags(.unit))
	func encodesAndDecodes() throws {
		let codable = ["foo": "bar"]

		try withDependencies {
			$0[EncoderService.self] = .liveValue
			$0[DecoderService.self] = .liveValue
		} operation: {
			let data = try encoder.encode(codable)
			let decoded = try decoder.decode([String: String].self, from: data)

			#expect(decoded == codable)
		}
	}
}
