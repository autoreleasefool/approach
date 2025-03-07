@testable import StatisticsLibrary
import Testing

@Suite("Statistics")
struct StatisticsTests {

	@Test("Finds type by ID")
	func findsTypeByID() {
		let id = "High Single"

		let type = Statistics.type(of: id)

		#expect(type is Statistics.HighSingle.Type)
		#expect(!(type is Statistics.HighSeriesOf3.Type))
	}

	@Test("With invalid ID returns nil")
	func typeWithInvalidIDReturnsNil() {
		let id = "invalid"

		let type = Statistics.type(of: id)

		#expect(type == nil)
	}
}
