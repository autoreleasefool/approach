import ModelsLibrary
@testable import PickableModelsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Series+PickableResource", .tags(.library))
struct SeriesPickableResourceTests {

	@Test(
		"Model name is correct",
		.tags(.unit),
		arguments: zip([0, 1, 2], ["Series", "Series", "Series"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Series.Summary.pickableModelName(forCount: count) == expected)
	}
}
