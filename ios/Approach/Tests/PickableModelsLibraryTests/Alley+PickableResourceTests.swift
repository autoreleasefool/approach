import ModelsLibrary
@testable import PickableModelsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Alley+PickableResource", .tags(.library))
struct AlleyPickableResourceTests {

	@Test(
		"Model name is correct",
		.tags(.unit),
		arguments: zip([0, 1, 2], ["Alleys", "Alley", "Alleys"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Alley.Summary.pickableModelName(forCount: count) == expected)
	}
}
