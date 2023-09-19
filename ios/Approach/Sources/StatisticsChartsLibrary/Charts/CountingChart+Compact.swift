import SwiftUI

extension CountingChart {
	public struct Default: View {
		let data: CountingChart.Data

		public init(_ data: CountingChart.Data) {
			self.data = data
		}

		public var body: some View {
			GroupBox(data.title) {
				CountingChart(data)
			}
			.groupBoxStyle(ChartsGroupBoxStyle.counting)
		}
	}
}

extension CountingChart {
	public struct Compact: View {
		let data: CountingChart.Data
		let style: CountingChart.Style

		public init(_ data: CountingChart.Data, style: CountingChart.Style) {
			self.data = data
			self.style = style
		}

		public var body: some View {
			CountingChart(data, style: style)
		}
	}
}
