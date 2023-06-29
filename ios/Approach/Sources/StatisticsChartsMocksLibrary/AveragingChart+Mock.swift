import Dependencies
import Foundation
import StatisticsChartsLibrary
import SwiftUI

extension AveragingChart.Data {
	private static let bowlerAverageIncrementing: [Double] = [180.0, 181.2, 183.6, 185.2]
	private static func createMockData(fromArray: [Double]) -> AveragingChart.Data {
		let startDate = Calendar.current.date(byAdding: .month, value: -1, to: Date.now)!
		return .init(
			title: "Your Average",
			entries: fromArray.enumerated().map { index, average in
				.init(
					id: .init(index),
					value: average,
					date: Calendar.current.date(byAdding: .weekOfYear, value: index, to: startDate)!
				)
			}
		)
	}

	public static let bowlerAverageIncrementingMock: AveragingChart.Data =
		createMockData(fromArray: bowlerAverageIncrementing)
	public static let bowlerAverageDecrementingMock: AveragingChart.Data =
		createMockData(fromArray: bowlerAverageIncrementing.reversed())
}

// MARK: - Previews

#if DEBUG
struct AveragingChartMockPreview: PreviewProvider {
	static var previews: some View {
		VStack {
			AveragingChart.Compact(
				AveragingChart.Data.bowlerAverageIncrementingMock,
				style: .init()
			)
			.layoutPriority(0.5)

			Spacer().layoutPriority(0.5)
		}
		.padding()
	}
}
#endif
