import AssetsLibrary
import Charts
import DateTimeLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct PercentageChart: View {
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
					$0.numeratorAreaMark(withTitle: data.title)
						.lineStyle(StrokeStyle(lineWidth: 3))
						.foregroundStyle(style.numeratorLineMarkColor.swiftUIColor)
						.interpolationMethod(.catmullRom)

					$0.denominatorAreaMark(withTitle: data.title)
						.lineStyle(StrokeStyle(lineWidth: 3))
						.foregroundStyle(style.denominatorLineMarkColor.swiftUIColor)
						.interpolationMethod(.catmullRom)
				} else {
					$0.barMark(withTitle: data.title)
						.foregroundStyle(barMarkGradient)
				}
			}
		}
		.chartXAxis {
			AxisMarks {
				if !style.hideXAxis {
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
}

// MARK: - Data

extension PercentageChart {
	public struct Data: Equatable {
		public let title: String
		public let entries: [Entry]
		public let isAccumulating: Bool

		public let preferredTrendDirection: StatisticTrendDirection?
		public let percentDifferenceOverFullTimeSpan: Double?

		public var isEmpty: Bool {
			entries.isEmpty || (isAccumulating && entries.count == 1)
		}

		public init(
			title: String,
			entries: [Entry],
			isAccumulating: Bool,
			preferredTrendDirection: StatisticTrendDirection?
		) {
			self.title = title
			self.entries = entries
			self.isAccumulating = isAccumulating
			self.preferredTrendDirection = preferredTrendDirection

			if isAccumulating {
				let firstValue = entries.first?.percentage ?? 0
				let lastValue = entries.last?.percentage ?? 0
				self.percentDifferenceOverFullTimeSpan = firstValue > 0
					? ((lastValue - firstValue) / abs(firstValue))
					: 0
			} else {
				self.percentDifferenceOverFullTimeSpan = nil
			}
		}
	}
}

extension PercentageChart.Data {
	public struct Entry: Equatable, Identifiable {
		public let id: UUID
		public let numerator: Int
		public let denominator: Int
		public let percentage: Double
		public let xAxis: XAxis

		public init(id: UUID, numerator: Int, denominator: Int, xAxis: XAxis) {
			self.id = id
			self.numerator = numerator
			self.denominator = denominator
			self.percentage = denominator > 0 ? Double(numerator) / Double(denominator) : 0
			self.xAxis = xAxis
		}

		func numeratorAreaMark(withTitle title: String) -> AreaMark {
			switch xAxis {
			case let .date(date, _):
				return AreaMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, date),
					y: .value(title, numerator),
					series: .value("", title)
				)
			case let .game(ordinal):
				return AreaMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.game, Strings.Game.titleWithOrdinal(ordinal)),
					y: .value(title, numerator),
					series: .value("", title)
				)
			}
		}

		func denominatorAreaMark(withTitle title: String) -> AreaMark {
			switch xAxis {
			case let .date(date, _):
				return AreaMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, date),
					y: .value(Strings.Statistics.Title.totalRolls, denominator),
					series: .value("", Strings.Statistics.Title.totalRolls)
				)
			case let .game(ordinal):
				return AreaMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.game, Strings.Game.titleWithOrdinal(ordinal)),
					y: .value(Strings.Statistics.Title.totalRolls, denominator),
					series: .value("", Strings.Statistics.Title.totalRolls)
				)
			}
		}

		func barMark(withTitle title: String) -> BarMark {
			switch xAxis {
			case let .date(date, timeRange):
				return BarMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.date, date ..< date.advanced(by: timeRange)),
					y: .value(title, numerator)
				)
			case let .game(ordinal):
				return BarMark(
					x: .value(Strings.Statistics.Charts.AxesLabels.game, Strings.Game.titleWithOrdinal(ordinal)),
					y: .value(title, numerator)
				)
			}
		}
	}
}

extension PercentageChart.Data {
	public enum XAxis: Equatable {
		case date(Date, TimeInterval)
		case game(ordinal: Int)
	}
}

// MARK: - Style

extension PercentageChart {
	public struct Style {
		public let barMarkColor: ColorAsset
		public let denominatorLineMarkColor: ColorAsset
		public let numeratorLineMarkColor: ColorAsset
		public let axesColor: ColorAsset
		public let hideXAxis: Bool

		public init(
			barMarkColor: ColorAsset = Asset.Colors.Charts.Percentage.barMark,
			denominatorLineMarkColor: ColorAsset = Asset.Colors.Charts.Percentage.denominatorLineMark,
			numeratorLineMarkColor: ColorAsset = Asset.Colors.Charts.Percentage.numeratorLineMark,
			axesColor: ColorAsset = Asset.Colors.Charts.Percentage.axes,
			hideXAxis: Bool = false
		) {
			self.barMarkColor = barMarkColor
			self.denominatorLineMarkColor = denominatorLineMarkColor
			self.numeratorLineMarkColor = numeratorLineMarkColor
			self.axesColor = axesColor
			self.hideXAxis = hideXAxis
		}
	}
}

// MARK: - Previews

#if DEBUG
struct PercentageChartPreview: PreviewProvider {
	static var periodicNumerators = [7, 11, 3, 1, 5, 6, 0, 0, 14]
	static var periodicDenominators = [8, 11, 6, 5, 12, 13, 2, 0, 15]
	static var accumulatingNumerators: [Int] = {
		var accumulating: [Int] = []
		for numerator in periodicNumerators {
			accumulating.append((accumulating.last ?? 0) + numerator)
		}
		return accumulating
	}()
	static var accumulatingDenominators: [Int] = {
		var accumulating: [Int] = []
		for denominator in periodicDenominators {
			accumulating.append((accumulating.last ?? 0) + denominator)
		}
		return accumulating
	}()

	static var previews: some View {
		VStack {
			PercentageChart(
				.init(
					title: "Middle Hits",
					entries: zip(periodicNumerators, periodicDenominators).enumerated().map { index, value in
							.init(
								id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
								numerator: value.0,
								denominator: value.1,
								xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0), 604800.0)
							)
					},
					isAccumulating: false,
					preferredTrendDirection: .upwards
				)
			)

			PercentageChart(
				.init(
					title: "Head Pins",
					entries: zip(accumulatingNumerators, accumulatingDenominators).enumerated().map { index, value in
							.init(
								id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
								numerator: value.0,
								denominator: value.1,
								xAxis: .date(Date(timeIntervalSince1970: Double(index) * 604800.0), 604800.0)
							)
					},
					isAccumulating: true,
					preferredTrendDirection: .downwards
				)
			)
		}
		.padding()
	}
}
#endif
