import AssetsLibrary
import Charts
import DateTimeLibrary
import StringsLibrary
import SwiftUI

public struct AveragingChart: View {
	let data: Data
	let style: Style

	public init(_ data: Data, style: Style = .init()) {
		self.data = data
		self.style = style
	}

	public var body: some View {
		Chart {
			ForEach(data.entries) {
				LineMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date),
					y: .value(data.title, $0.value)
				)
				.lineStyle(StrokeStyle(lineWidth: 3))
				.foregroundStyle(style.lineMarkColor.swiftUIColor)
				.interpolationMethod(.catmullRom)
			}
		}
		.chartXAxis {
			if !style.hideXAxis {
				AxisMarks {
					AxisGridLine().foregroundStyle(style.axesColor.swiftUIColor)
					AxisTick().foregroundStyle(style.axesColor.swiftUIColor)
					AxisValueLabel().foregroundStyle(style.axesColor.swiftUIColor)
				}
			}
		}
		.chartYAxis {
			AxisMarks {
				AxisGridLine().foregroundStyle(style.axesColor.swiftUIColor)
				AxisTick().foregroundStyle(style.axesColor.swiftUIColor)
				AxisValueLabel().foregroundStyle(style.axesColor.swiftUIColor)
			}
		}
		.chartYScale(domain: [data.minimumValue, data.maximumValue])
	}
}

// MARK: - Data

extension AveragingChart {
	public struct Data: Equatable {
		public let title: String
		public let entries: [Entry]
		public let minimumValue: Double
		public let maximumValue: Double
		public let percentDifferenceOverFullTimeSpan: Double

		public var isEmpty: Bool {
			entries.count <= 1
		}

		public init(title: String, entries: [Entry]) {
			self.title = title
			self.entries = entries
			let minimumValue = entries.min { $0.value < $1.value }?.value ?? 0
			let maximumValue = entries.max { $0.value < $1.value }?.value ?? 0
			let padding = (maximumValue - minimumValue) / 10
			self.minimumValue = minimumValue - padding
			self.maximumValue = maximumValue + padding

			let firstValue = entries.first?.value ?? 0
			let lastValue = entries.last?.value ?? 0
			self.percentDifferenceOverFullTimeSpan = firstValue > 0
				? ((lastValue - firstValue) / abs(firstValue))
				: 0
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

// MARK: - Style

extension AveragingChart {
	public struct Style {
		public let lineMarkColor: ColorAsset
		public let axesColor: ColorAsset
		public let hideXAxis: Bool

		public init(
			lineMarkColor: ColorAsset = Asset.Colors.Charts.Averaging.lineMark,
			axesColor: ColorAsset = Asset.Colors.Charts.Averaging.axes,
			hideXAxis: Bool = false
		) {
			self.lineMarkColor = lineMarkColor
			self.axesColor = axesColor
			self.hideXAxis = hideXAxis
		}
	}
}

// MARK: - Previews

#if DEBUG
struct AveragingChartPreview: PreviewProvider {
	static var averagingData: [Double] = [180, 180.5, 180.8, 190.2, 189.3, 187, 185, 186.3, 186.5]

	static var previews: some View {
		VStack {
			AveragingChart.Default(
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

			AveragingChart.Compact(
				.init(
					title: "Head Pins",
					entries: averagingData.enumerated().map { index, value in
						.init(
							id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
							value: value,
							date: Date(timeIntervalSince1970: Double(index) * 604800.0)
						)
					}
				),
				style: .init(hideXAxis: true)
			)
		}
		.padding()
	}
}
#endif
