import ModelsLibrary
@testable import PickableModelsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Game+PickableResource", .tags(.library))
struct GamePickableResourceTests {

	@Test(
		"Model name is correct",
		.tags(.unit),
		arguments: zip([0, 1, 2], ["Games", "Game", "Games"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Game.Summary.pickableModelName(forCount: count) == expected)
	}
}
