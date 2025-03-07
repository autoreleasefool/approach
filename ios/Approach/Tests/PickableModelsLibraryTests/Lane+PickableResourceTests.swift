import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("Lane+PickableResource")
struct LanePickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Lanes", "Lane", "Lanes"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Lane.Summary.pickableModelName(forCount: count) == expected)
	}
}
