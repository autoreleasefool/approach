import ModelsLibrary
@testable import PickableModelsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("League+PickableResource", .tags(.library))
struct LeaguePickableResourceTests {

	@Test(
		"Model name is correct",
		.tags(.unit),
		arguments: zip([0, 1, 2], ["Leagues", "League", "Leagues"])
	)
	func modelName(count: Int, expected: String) {
		#expect(League.Summary.pickableModelName(forCount: count) == expected)
	}
}
