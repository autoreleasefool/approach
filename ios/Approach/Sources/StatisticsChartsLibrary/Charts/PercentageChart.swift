import AssetsLibrary
import Charts
import DateTimeLibrary
import StringsLibrary
import SwiftUI

public struct PercentageChart: View {
	let data: Data

	public init(_ data: Data) {
		self.data = data
	}

	public var body: some View {
		GroupBox(data.title) {
			Chart {
				ForEach(data.entries) {
					if data.isAccumulating {
						AreaMark(
							x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date),
							y: .value(data.title, $0.numerator),
							series: .value("", data.title)
						)
						.lineStyle(StrokeStyle(lineWidth: 3))
						.foregroundStyle(Asset.Colors.Charts.Percentage.numeratorLineMark.swiftUIColor)
						.interpolationMethod(.catmullRom)

						AreaMark(
							x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date),
							y: .value(Strings.Statistics.Title.totalRolls, $0.denominator),
							series: .value("", Strings.Statistics.Title.totalRolls)
						)
						.lineStyle(StrokeStyle(lineWidth: 3))
						.foregroundStyle(Asset.Colors.Charts.Percentage.denominatorLineMark.swiftUIColor)
						.interpolationMethod(.catmullRom)
					} else {
						BarMark(
							x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date ..< $0.date.advanced(by: $0.timeRange)),
							y: .value(data.title, $0.numerator)
						)
						.foregroundStyle(barMarkGradient)

					}
				}
			}
			.chartXAxis {
				AxisMarks {
					AxisGridLine().foregroundStyle(Asset.Colors.Charts.Percentage.axes.swiftUIColor)
					AxisTick().foregroundStyle(Asset.Colors.Charts.Percentage.axes.swiftUIColor)
					AxisValueLabel().foregroundStyle(Asset.Colors.Charts.Percentage.axes.swiftUIColor)
				}
			}
			.chartYAxis {
				AxisMarks {
					AxisGridLine().foregroundStyle(Asset.Colors.Charts.Percentage.axes.swiftUIColor)
					AxisTick().foregroundStyle(Asset.Colors.Charts.Percentage.axes.swiftUIColor)
					AxisValueLabel().foregroundStyle(Asset.Colors.Charts.Percentage.axes.swiftUIColor)
				}
			}
		}
		.groupBoxStyle(ChartsGroupBoxStyle.percentage)
	}

	private var barMarkGradient: LinearGradient {
		.init(
			gradient: Gradient(colors: [
				Asset.Colors.Charts.Percentage.barMark.swiftUIColor.opacity(0.8),
				Asset.Colors.Charts.Percentage.barMark.swiftUIColor.opacity(0.3)
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

extension PercentageChart.Data {
	public struct Entry: Equatable, Identifiable {
		public let id: UUID
		public let numerator: Int
		public let denominator: Int
		public let percentage: Double
		public let date: Date
		public let timeRange: TimeInterval

		public init(id: UUID, numerator: Int, denominator: Int, date: Date, timeRange: TimeInterval) {
			self.id = id
			self.numerator = numerator
			self.denominator = denominator
			self.percentage = denominator > 0 ? Double(numerator) / Double(denominator) : 0
			self.date = date
			self.timeRange = timeRange
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
								date: Date(timeIntervalSince1970: Double(index) * 604800.0),
								timeRange: 604800.0
							)
					},
					isAccumulating: false
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
								date: Date(timeIntervalSince1970: Double(index) * 604800.0),
								timeRange: 604800.0
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
