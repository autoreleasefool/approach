import Dependencies
import Foundation
import StatisticsChartsLibrary
import SwiftUI

extension AveragingChart.Data {
	public static let bowlerAverageMock: AveragingChart.Data = {
		let averages: [Double] = [180.0, 181.2, 183.6, 185.2]
		let startDate = Calendar.current.date(byAdding: .month, value: -1, to: Date.now)!

		return .init(
			title: "Your Average",
			entries: averages.enumerated().map { index, average in
				.init(
					id: .init(index),
					value: average,
					date: Calendar.current.date(byAdding: .weekOfYear, value: index, to: startDate)!
				)
			}
		)
	}()
}

// MARK: - Previews

#if DEBUG
struct AveragingChartMockPreview: PreviewProvider {
	static var previews: some View {
		VStack {
			AveragingChart.Compact(
				AveragingChart.Data.bowlerAverageMock
			)
			.layoutPriority(0.5)

			Spacer().layoutPriority(0.5)
		}
		.padding()
	}
}
#endif
