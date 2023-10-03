import AssetsLibrary
import Charts
import DateTimeLibrary
import StatisticsLibrary
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
				$0.lineMark(withTitle: data.title)
					.lineStyle(StrokeStyle(lineWidth: 3))
					.foregroundStyle(style.lineMarkColor.swiftUIColor)
					.interpolationMethod(.catmullRom)
			}
		}
		.chartXAxis {
			if !style.hideXAxis {
				AxisMarks {
					AxisGridLine()
						.foregroundStyle(style.axesColor.swiftUIColor)
					AxisTick()
						.foregroundStyle(style.axesColor.swiftUIColor)
					AxisValueLabel()
						.foregroundStyle(style.axesColor.swiftUIColor)
				}
			}
		}
		.chartYAxis {
			if !style.hideYAxis {
				AxisMarks {
					AxisGridLine()
						.foregroundStyle(style.axesColor.swiftUIColor)
					AxisTick()
						.foregroundStyle(style.axesColor.swiftUIColor)
					AxisValueLabel()
						.foregroundStyle(style.axesColor.swiftUIColor)
				}
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

		public let preferredTrendDirection: StatisticTrendDirection?
		public let percentDifferenceOverFullTimeSpan: Double

		public var isEmpty: Bool {
			entries.count <= 1
		}

		public init(title: String, entries: [Entry], preferredTrendDirection: StatisticTrendDirection?) {
			self.title = title
			self.entries = entries
			self.preferredTrendDirection = preferredTrendDirection
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
		public let xAxis: XAxis

		public init(id: UUID, value: Double, xAxis: XAxis) {
			self.id = id
			self.value = value
			self.xAxis = xAxis
		}

		func lineMark(withTitle title: String) -> LineMark {
			switch xAxis {
			case let .date(date):
				return LineMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, date),
					y: .value(title, value)
				)
			case let .game(ordinal):
				return LineMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.game, Strings.Game.titleWithOrdinal(ordinal)),
					y: .value(title, value)
				)
			}
		}
	}
}

extension AveragingChart.Data {
	public enum XAxis: Equatable {
		case date(Date)
		case game(ordinal: Int)
	}
}

// MARK: - Style

extension AveragingChart {
	public struct Style {
		public let lineMarkColor: ColorAsset
		public let axesColor: ColorAsset
		public let hideXAxis: Bool
		public let hideYAxis: Bool

		public init(
			lineMarkColor: ColorAsset = Asset.Colors.Charts.Averaging.lineMark,
			axesColor: ColorAsset = Asset.Colors.Charts.Averaging.axes,
			hideXAxis: Bool = false,
			hideYAxis: Bool = false
		) {
			self.lineMarkColor = lineMarkColor
			self.axesColor = axesColor
			self.hideXAxis = hideXAxis
			self.hideYAxis = hideYAxis
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
							xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0))
						)
					},
					preferredTrendDirection: .downwards
				)
			)

			AveragingChart.Compact(
				.init(
					title: "Head Pins",
					entries: averagingData.enumerated().map { index, value in
						.init(
							id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
							value: value,
							xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0))
						)
					},
					preferredTrendDirection: .upwards
				),
				style: .init(hideXAxis: true)
			)
		}
		.padding()
	}
}
#endif
