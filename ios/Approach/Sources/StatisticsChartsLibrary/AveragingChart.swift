import AssetsLibrary
import Charts
import DateTimeLibrary
import StringsLibrary
import SwiftUI

public struct AveragingChart: View {
	let data: Data

	public init(_ data: Data) {
		self.data = data
	}

	public var body: some View {
		GroupBox(data.title) {
			Chart {
				ForEach(data.entries) {
					LineMark(
						x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date),
						y: .value(data.title, $0.value)
					)
					.lineStyle(StrokeStyle(lineWidth: 3))
					.foregroundStyle(Color.appChartsAccumulatingLine)
					.interpolationMethod(.catmullRom)
				}
			}
			.chartXAxis {
				AxisMarks {
					AxisGridLine().foregroundStyle(Color.appChartsAccumulatingAxes)
					AxisTick().foregroundStyle(Color.appChartsAccumulatingAxes)
					AxisValueLabel().foregroundStyle(Color.appChartsAccumulatingAxes)
				}
			}
			.chartYAxis {
				AxisMarks {
					AxisGridLine().foregroundStyle(Color.appChartsAccumulatingAxes)
					AxisTick().foregroundStyle(Color.appChartsAccumulatingAxes)
					AxisValueLabel().foregroundStyle(Color.appChartsAccumulatingAxes)
				}
			}
			.chartYScale(domain: [data.minimumValue, data.maximumValue])
		}
		.groupBoxStyle(ChartsGroupBoxStyle.averaging)
	}

	private var areaMarkGradient: LinearGradient {
		.init(
			gradient: Gradient(colors: [
				.appChartsAccumulatingArea.opacity(0.8),
				.appChartsAccumulatingArea.opacity(0.5),
				.appChartsAccumulatingArea.opacity(0.2),
			]),
			startPoint: .top,
			endPoint: .bottom
		)
	}
}

// MARK: - Data

extension AveragingChart {
	public struct Data: Equatable {
		public let title: String
		public let entries: [Entry]
		public let minimumValue: Double
		public let maximumValue: Double

		public init(title: String, entries: [Entry]) {
			self.title = title
			self.entries = entries
			let minimumValue = entries.min { $0.value < $1.value }?.value ?? 0
			let maximumValue = entries.max { $0.value < $1.value }?.value ?? 0
			let padding = (maximumValue - minimumValue) / 10
			self.minimumValue = minimumValue - padding
			self.maximumValue = maximumValue + padding
		}
	}
}

extension AveragingChart.Data {
	public struct Entry: Equatable, Identifiable {
		public let id: UUID
		public let value: Double
		public let date: Date

		public init(id: UUID, value: Double, date: Date) {
			self.id = id
			self.value = value
			self.date = date
		}
	}
}

// MARK: - Previews

#if DEBUG
struct AveragingChartPreview: PreviewProvider {
	static var averagingData: [Double] = [180, 180.5, 180.8, 190.2, 189.3, 187, 185, 186.3, 186.5]

	static var previews: some View {
		VStack {
			AveragingChart(
				.init(
					title: "Head Pins",
					entries: averagingData.enumerated().map { index, value in
							.init(
								id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
								value: value,
								date: Date(timeIntervalSince1970: Double(index) * 604800.0)
							)
					}
				)
			)
			.layoutPriority(0.5)

			Spacer().layoutPriority(0.5)
		}
		.padding()
	}
}
#endif
