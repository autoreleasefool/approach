import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("Alley+PickableResource")
struct AlleyPickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Alleys", "Alley", "Alleys"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Alley.Summary.pickableModelName(forCount: count) == expected)
	}
}
