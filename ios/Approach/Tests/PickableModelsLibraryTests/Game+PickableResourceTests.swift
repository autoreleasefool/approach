import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("Game+PickableResource")
struct GamePickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Games", "Game", "Games"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Game.Summary.pickableModelName(forCount: count) == expected)
	}
}
