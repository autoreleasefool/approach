import SwiftUI

extension PercentageChart {
	public struct Default: View {
		let data: PercentageChart.Data

		public init(_ data: PercentageChart.Data) {
			self.data = data
		}

		public var body: some View {
			GroupBox(data.title) {
				PercentageChart(data)
			}
			.groupBoxStyle(ChartsGroupBoxStyle.percentage)
		}
	}
}

extension PercentageChart {
	public struct Compact: View {
		let data: PercentageChart.Data
		let style: PercentageChart.Style

		public init(_ data: PercentageChart.Data, style: PercentageChart.Style) {
			self.data = data
			self.style = style
		}

		public var body: some View {
			PercentageChart(data, style: style)
		}
	}
}
