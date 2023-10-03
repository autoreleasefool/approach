import Foundation
import StatisticsChartsLibrary

extension CountingChart.Data {
	private static let shuffledData = [1, 3, 0, 2, 1, 1, 2, 2, 0, 6, 7, 1, 1]
	private static let increasingData = [1, 4, 4, 6, 7, 8, 10, 12, 12, 18, 25, 26, 27]

	public static func createPlaceholderData(forStatistic: String, isAccumulating: Bool) -> CountingChart.Data {
		let data = isAccumulating ? increasingData : shuffledData

		return .init(
			title: forStatistic,
			entries: data.enumerated().map { index, value in
				.init(
					id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
					value: value,
					xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0), 604800)
				)
			},
			isAccumulating: isAccumulating
		)
	}

	public static let bowlerHeadPinsMock: CountingChart.Data =
		createPlaceholderData(forStatistic: "Head Pins", isAccumulating: true)
}
