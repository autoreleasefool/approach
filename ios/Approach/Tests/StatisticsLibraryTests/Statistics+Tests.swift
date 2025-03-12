@testable import StatisticsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Statistics", .tags(.library))
struct StatisticsTests {

	@Test("Finds type by ID", .tags(.unit))
	func findsTypeByID() {
		let id = "High Single"

		let type = Statistics.type(of: id)

		#expect(type is Statistics.HighSingle.Type)
		#expect(!(type is Statistics.HighSeriesOf3.Type))
	}

	@Test("With invalid ID returns nil", .tags(.unit))
	func typeWithInvalidIDReturnsNil() {
		let id = "invalid"

		let type = Statistics.type(of: id)

		#expect(type == nil)
	}
}
