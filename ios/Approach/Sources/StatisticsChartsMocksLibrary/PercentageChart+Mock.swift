import Foundation
import StatisticsChartsLibrary

extension PercentageChart.Data {
	private static let periodicNumerators = [7, 11, 3, 1, 5, 6, 0, 0, 14]
	private static let periodicDenominators = [8, 11, 6, 5, 12, 13, 2, 0, 15]
	private static let accumulatingNumerators: [Int] = {
		var accumulating: [Int] = []
		for numerator in periodicNumerators {
			accumulating.append((accumulating.last ?? 0) + numerator)
		}
		return accumulating
	}()
	private static let accumulatingDenominators: [Int] = {
		var accumulating: [Int] = []
		for denominator in periodicDenominators {
			accumulating.append((accumulating.last ?? 0) + denominator)
		}
		return accumulating
	}()

	public static func createPlaceholderData(forStatistic: String, isAccumulating: Bool) -> PercentageChart.Data {
		let numerators = isAccumulating ? accumulatingNumerators : periodicNumerators
		let denominators = isAccumulating ? accumulatingDenominators : periodicDenominators

		return .init(
			title: forStatistic,
			entries: zip(numerators, denominators)
				.enumerated()
				.map { index, value in
					.init(
						id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
						numerator: value.0,
						denominator: value.1,
						xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0), 604800)
					)
				},
			isAccumulating: isAccumulating,
			preferredTrendDirection: .upwards
		)
	}

	public static let bowlerMiddleHitsMock: PercentageChart.Data =
		createPlaceholderData(forStatistic: "Middle Hits", isAccumulating: true)
}
