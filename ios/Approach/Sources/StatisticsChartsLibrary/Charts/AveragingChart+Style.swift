import SwiftUI

extension AveragingChart {
	public struct Default: View {
		let data: AveragingChart.Data

		public init(_ data: AveragingChart.Data) {
			self.data = data
		}

		public var body: some View {
			GroupBox(data.title) {
				AveragingChart(data)
			}
			.groupBoxStyle(ChartsGroupBoxStyle.averaging)
		}
	}
}

extension AveragingChart {
	public struct Compact: View {
		let data: AveragingChart.Data
		let style: AveragingChart.Style

		public init(_ data: AveragingChart.Data, style: AveragingChart.Style) {
			self.data = data
			self.style = style
		}

		public var body: some View {
			AveragingChart(data, style: style)
		}
	}
}
