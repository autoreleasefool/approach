import StatisticsLibrary
import Testing

@testable import ImportExportService
@testable import ImportExportServiceInterface

@Suite("AndroidStatistics tests", .tags(.android))
struct AndroidStatisticsTests {
	@Test("Android statistics map to iOS")
	func mappingExists() {
		let androidStatistics = [
			"ACES",
			"ACES_SPARED",
			"CHOPS",
			"CHOPS_SPARED",
			"FIVES",
			"FIVES_SPARED",
			"HEAD_PINS",
			"HEAD_PINS_SPARED",
			"LEFT_CHOPS",
			"LEFT_CHOPS_SPARED",
			"LEFT_FIVES",
			"LEFT_FIVES_SPARED",
			"LEFT_SPLITS",
			"LEFT_SPLITS_SPARED",
			"LEFT_TAPS",
			"LEFT_TAPS_SPARED",
			"LEFT_THREES",
			"LEFT_THREES_SPARED",
			"LEFT_TWELVES",
			"LEFT_TWELVES_SPARED",
			"RIGHT_CHOPS",
			"RIGHT_CHOPS_SPARED",
			"RIGHT_FIVES",
			"RIGHT_FIVES_SPARED",
			"RIGHT_SPLITS",
			"RIGHT_SPLITS_SPARED",
			"RIGHT_TAPS",
			"RIGHT_TAPS_SPARED",
			"RIGHT_THREES",
			"RIGHT_THREES_SPARED",
			"RIGHT_TWELVES",
			"RIGHT_TWELVES_SPARED",
			"SPLITS",
			"SPLITS_SPARED",
			"TAPS",
			"TAPS_SPARED",
			"THREES",
			"THREES_SPARED",
			"TWELVES",
			"TWELVES_SPARED",
			"FOULS",
			"SPARE_CONVERSIONS",
			"STRIKES",
			"MATCHES_LOST",
			"MATCHES_WON",
			"MATCHES_TIED",
			"MATCHES_PLAYED",
			"LEFT_OF_MIDDLE_HITS",
			"RIGHT_OF_MIDDLE_HITS",
			"MIDDLE_HITS",
			"STRIKE_MIDDLE_HITS",
			"GAME_AVERAGE",
			"HIGH_SINGLE_GAME",
			"NUMBER_OF_GAMES",
			"TOTAL_PIN_FALL",
			"TOTAL_ROLLS",
			"AVERAGE_PINS_LEFT_ON_DECK",
			"TOTAL_PINS_LEFT_ON_DECK",
			"AVERAGE_FIRST_ROLL",
			"HIGH_SERIES_OF_3",
		]

		let mapped = androidStatistics.compactMap {
			AndroidStatistic(rawValue: $0)
		}

		#expect(androidStatistics.count == mapped.count)
		#expect(mapped.count == Statistics.allCases.count)
	}
}
