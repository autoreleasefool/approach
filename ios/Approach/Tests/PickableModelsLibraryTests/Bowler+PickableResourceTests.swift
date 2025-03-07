import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("Bowler+PickableResource")
struct BowlerPickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Bowlers", "Bowler", "Bowlers"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Bowler.Summary.pickableModelName(forCount: count) == expected)
	}

	@Test(
		"Opponent model name is correct",
		arguments: zip([0, 1, 2], ["Opponents", "Opponent", "Opponents"])
	)
	func opponentModelName(count: Int, expected: String) {
		#expect(Bowler.Opponent.pickableModelName(forCount: count) == expected)
	}
}
