@testable import AddressLookupService
@testable import AddressLookupServiceInterface
import Dependencies
import XCTest

final class AddressLookupServiceTests: XCTestCase {
	struct LookupID {}

	func testBeginLookupHasNoResults() async throws {
		let addressLookup: AddressLookupService = .liveValue

		let results = await addressLookup.beginSearch(LookupID.self)
		var iterator = results.makeAsyncIterator()
		await addressLookup.finishSearch(LookupID.self)

		let value = try await iterator.next()
		XCTAssertNil(value)
	}

	func testUpdateLookupReturnsResults() async throws {
		// FIXME: does MKLocalSearchCompleter work in XCTests?
		let addressLookup: AddressLookupService = .liveValue

		let results = await addressLookup.beginSearch(LookupID.self)
		var iterator = results.makeAsyncIterator()

		await addressLookup.updateSearchQuery(LookupID.self, "2 Harrison Street")
		var value = try await iterator.next()
		XCTAssertNotNil(value)

		await addressLookup.finishSearch(LookupID.self)
		value = try await iterator.next()
		XCTAssertNil(value)
	}

	func testLookupReturnsFirstResult() async {
		// FIXME: does MKLocalSearch work in XCTests?
	}
}
