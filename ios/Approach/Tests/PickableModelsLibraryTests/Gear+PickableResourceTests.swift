import ModelsLibrary
@testable import PickableModelsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Gear+PickableResource", .tags(.library))
struct GearPickableResourceTests {

	@Test(
		"Model name is correct",
		.tags(.unit),
		arguments: zip([0, 1, 2], ["Gear", "Gear", "Gear"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Gear.Summary.pickableModelName(forCount: count) == expected)
	}
}
