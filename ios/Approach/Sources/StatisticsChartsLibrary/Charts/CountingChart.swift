import AssetsLibrary
import Charts
import DateTimeLibrary
import Foundation
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct CountingChart: View {
	let data: Data
	let style: Style

	public init(_ data: Data, style: Style = .init()) {
		self.data = data
		self.style = style
	}

	public var body: some View {
		Chart {
			ForEach(data.entries) {
				if data.isAccumulating {
					$0.areaMark(withTitle: data.title)
						.foregroundStyle(areaMarkGradient)

					$0.lineMark(withTitle: data.title)
						.lineStyle(StrokeStyle(lineWidth: 2))
						.foregroundStyle(style.lineMarkColor.swiftUIColor)
				} else {
					$0.barMark(withTitle: data.title)
						.foregroundStyle(barMarkGradient)
				}
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

	private var barMarkGradient: LinearGradient {
		.init(
			gradient: Gradient(colors: [
				style.barMarkColor.swiftUIColor.opacity(0.8),
				style.barMarkColor.swiftUIColor.opacity(0.3),
			]),
			startPoint: .top,
			endPoint: .bottom
		)
	}

	private var areaMarkGradient: LinearGradient {
		.init(
			gradient: Gradient(colors: [
				style.areaMarkColor.swiftUIColor.opacity(0.8),
				style.areaMarkColor.swiftUIColor.opacity(0.4),
				style.areaMarkColor.swiftUIColor.opacity(0.2),
			]),
			startPoint: .top,
			endPoint: .bottom
		)
	}
}

// MARK: - Data

extension CountingChart {
	public struct Data: Equatable {
		public let title: String
		public let entries: [Entry]
		public let isAccumulating: Bool

		public var isEmpty: Bool {
			entries.isEmpty || (isAccumulating && entries.count == 1)
		}

		public init(title: String, entries: [Entry], isAccumulating: Bool) {
			self.title = title
			self.entries = entries
			self.isAccumulating = isAccumulating
		}
	}
}

extension CountingChart.Data {
	public struct Entry: Equatable, Identifiable {
		public let id: UUID
		public let value: Int
		public let xAxis: XAxis

		public init(id: UUID, value: Int, xAxis: XAxis) {
			self.id = id
			self.value = value
			self.xAxis = xAxis
		}

		func areaMark(withTitle title: String) -> AreaMark {
			switch xAxis {
			case let .date(date, _):
				return AreaMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, date),
					y: .value(title, value)
				)
			case let .game(ordinal):
				return AreaMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.game, Strings.Game.titleWithOrdinal(ordinal)),
					y: .value(title, value)
				)
			}
		}

		func lineMark(withTitle title: String) -> LineMark {
			switch xAxis {
			case let .date(date, _):
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

		func barMark(withTitle title: String) -> BarMark {
			switch xAxis {
			case let .date(date, timeRange):
				return BarMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, date ..< date.advanced(by: timeRange)),
					y: .value(title, value)
				)
			case let .game(ordinal):
				return BarMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.game, Strings.Game.titleWithOrdinal(ordinal)),
					y: .value(title, value)
				)
			}
		}
	}
}

extension CountingChart.Data {
	public enum XAxis: Equatable {
		case date(Date, TimeInterval)
		case game(ordinal: Int)
	}
}

// MARK: - Style

extension CountingChart {
	public struct Style {
		public let areaMarkColor: ColorAsset
		public let barMarkColor: ColorAsset
		public let lineMarkColor: ColorAsset
		public let axesColor: ColorAsset
		public let hideXAxis: Bool

		public init(
			areaMarkColor: ColorAsset = Asset.Colors.Charts.Counting.areaMark,
			barMarkColor: ColorAsset = Asset.Colors.Charts.Counting.barMark,
			lineMarkColor: ColorAsset = Asset.Colors.Charts.Counting.lineMark,
			axesColor: ColorAsset = Asset.Colors.Charts.Counting.axes,
			hideXAxis: Bool = false
		) {
			self.areaMarkColor = areaMarkColor
			self.barMarkColor = barMarkColor
			self.lineMarkColor = lineMarkColor
			self.axesColor = axesColor
			self.hideXAxis = hideXAxis
		}
	}
}

// MARK: - Previews

#if DEBUG
struct CountingChartPreview: PreviewProvider {
	static var shuffledData = [1, 3, 0, 2, 1, 1, 2, 2, 0, 6, 7, 1, 1]
	static var increasingData = [1, 4, 4, 6, 7, 8, 10, 12, 12, 18, 25, 26, 27]

	static var previews: some View {
		VStack {
			CountingChart(
				.init(
					title: "Head Pins",
					entries: shuffledData.enumerated().map { index, value in
							.init(
								id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
								value: value,
								xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0), 604800)
							)
					},
					isAccumulating: false
				)
			)

			CountingChart(
				.init(
					title: "Head Pins",
					entries: increasingData.enumerated().map { index, value in
							.init(
								id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
								value: value,
								xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0), 604800)
							)
					},
					isAccumulating: true
				)
			)
		}
		.padding()
	}
}
#endif
