import AssetsLibrary
import Charts
import DateTimeLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI

public struct AccumulatingChart: View {
	let data: Data

	public init(_ data: Data) {
		self.data = data
	}

	public var body: some View {
		GroupBox(data.title) {
			Chart {
				ForEach(data.entries) {
					AreaMark(
						x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date),
						y: .value(data.title, $0.value)
					)
					.foregroundStyle(areaMarkGradient)

					LineMark(
						x: .value(Strings.Statistics.Charts.AxesLabels.date, $0.date),
						y: .value(data.title, $0.value)
					)
					.lineStyle(StrokeStyle(lineWidth: 2))
					.foregroundStyle(Color.appChartsAccumulatingLine)
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
		}
		.groupBoxStyle(ChartsGroupBoxStyle.accumulating)
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

extension AccumulatingChart {
	public struct Data: Equatable {
		public let title: String
		public let entries: [ChartEntry]

		public var isEmpty: Bool { entries.isEmpty }

		public init(title: String, entries: [ChartEntry]) {
			self.title = title
			self.entries = entries
		}
	}
}

public struct ChartsGroupBoxStyle: GroupBoxStyle {
	public static let accumulating = ChartsGroupBoxStyle(
		backgroundColor: .appChartsAccumulatingBackground,
		labelColor: .appChartsAccumulatingText
	)

	let backgroundColor: Color
	let labelColor: Color

	init(
		backgroundColor: Color,
		labelColor: Color
	) {
		self.backgroundColor = backgroundColor
		self.labelColor = labelColor
	}

	public func makeBody(configuration: Configuration) -> some View {
		configuration.content
			.padding(.top, .largeSpacing)
			.padding(.standardSpacing)
			.background(backgroundColor)
			.cornerRadius(.standardRadius)
			.overlay(
				configuration.label
					.font(.headline)
					.foregroundColor(labelColor)
					.padding(.standardSpacing),
				alignment: .topLeading
			)
	}
}

extension Color {
	init(hex: String) {
		let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
		var int: UInt64 = 0
		Scanner(string: hex).scanHexInt64(&int)
		let a, r, g, b: UInt64
		switch hex.count {
		case 3: // RGB (12-bit)
			(a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
		case 6: // RGB (24-bit)
			(a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
		case 8: // ARGB (32-bit)
			(a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
		default:
			(a, r, g, b) = (1, 1, 1, 0)
		}

		self.init(
			.sRGB,
			red: Double(r) / 255,
			green: Double(g) / 255,
			blue:  Double(b) / 255,
			opacity: Double(a) / 255
		)
	}
}

#if DEBUG
struct StatisticsChartPreview: PreviewProvider {
	static var increasingData = [0, 4, 4, 6, 7, 8, 10, 12, 12, 18, 25, 26, 27]

	static var previews: some View {
		VStack {
//			AccumulatingChart(
//				.init(
//					title: "Head Pins",
//					entries: (0..<20).map {
//						.init(
//							id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\($0 + 10)")!,
//							value: .init((0..<12).randomElement()!),
//							date: Calendar.current.date(byAdding: .month, value: $0, to: Calendar.current.startOfDay(for: Date()))!
//						)
//					}
//				)
//			)

			AccumulatingChart(
				.init(
					title: "Head Pins",
					entries: increasingData.enumerated().map { index, value in
							.init(
								id: UUID(uuidString: "00000000-0000-0000-0000-0000000000\(index + 10)")!,
								value: .init(value),
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
