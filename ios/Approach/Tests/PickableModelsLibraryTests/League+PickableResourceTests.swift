import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("League+PickableResource")
struct LeaguePickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Leagues", "League", "Leagues"])
	)
	func modelName(count: Int, expected: String) {
		#expect(League.Summary.pickableModelName(forCount: count) == expected)
	}
}

