@testable import AddressLookupService
@testable import AddressLookupServiceInterface
import Dependencies
import XCTest

final class AddressLookupServiceTests: XCTestCase {
	@Dependency(\.addressLookup) var addressLookup

	enum LookupID { case lookup }

	func testBeginLookupHasNoResults() async throws {
		let results = await addressLookup.beginSearch(LookupID.lookup)
		var iterator = results.makeAsyncIterator()
		await addressLookup.finishSearch(LookupID.lookup)

		let value = try await iterator.next()
		XCTAssertNil(value)
	}

	func testUpdateLookupReturnsResults() async throws {
		// FIXME: does MKLocalSearchCompleter work in XCTests?
		let results = await addressLookup.beginSearch(LookupID.lookup)
		var iterator = results.makeAsyncIterator()

		await addressLookup.updateSearchQuery(LookupID.lookup, "2 Harrison Street")
		var value = try await iterator.next()
		XCTAssertNotNil(value)

		await addressLookup.finishSearch(LookupID.lookup)
		value = try await iterator.next()
		XCTAssertNil(value)
	}

	func testLookupReturnsFirstResult() async {
		// FIXME: does MKLocalSearch work in XCTests?
	}
}
