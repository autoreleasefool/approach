import ModelsLibrary
@testable import PickableModelsLibrary
import Testing

@Suite("Series+PickableResource")
struct SeriesPickableResourceTests {

	@Test(
		"Model name is correct",
		arguments: zip([0, 1, 2], ["Series", "Series", "Series"])
	)
	func modelName(count: Int, expected: String) {
		#expect(Series.Summary.pickableModelName(forCount: count) == expected)
	}
}
