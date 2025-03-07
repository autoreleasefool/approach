import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("Gear+PickableResource")
struct GearPickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Gear", "Gear", "Gear"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Gear.Summary.pickableModelName(forCount: count) == expected)
	}
}
