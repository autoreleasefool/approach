import ModelsLibrary
@testable import PickableModelsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Lane+PickableResource", .tags(.library))
struct LanePickableResourceTests {

	@Test(
		"Model name is correct",
		.tags(.unit),
		arguments: zip([0, 1, 2], ["Lanes", "Lane", "Lanes"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Lane.Summary.pickableModelName(forCount: count) == expected)
	}
}
