import AssetsLibrary
import SwiftUI

public struct ChartsGroupBoxStyle: GroupBoxStyle {
	public static let counting = ChartsGroupBoxStyle(
		backgroundColor: .appChartsAccumulatingBackground,
		labelColor: .appChartsAccumulatingText
	)

	public static let averaging = ChartsGroupBoxStyle(
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
