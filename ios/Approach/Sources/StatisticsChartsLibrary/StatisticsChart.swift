import Charts
import StatisticsLibrary
import SwiftUI

public struct StatisticsChart: View {
	let data: Data

	public init(_ data: Data) {
		self.data = data
	}

	public var body: some View {
		Chart {
			ForEach(data.entries) { entry in
				BarMark(
					x: .value("Date", entry.date),
					y: .value(data.title, entry.value.value)
				)
			}
		}
	}
}

extension StatisticsChart {
	public struct Data: Equatable {
		public let title: String
		public let entries: [ChartEntry]

		public init(title: String, entries: [ChartEntry]) {
			self.title = title
			self.entries = entries
		}
	}
}
