import AssetsLibrary
import SwiftUI

public struct ChartsGroupBoxStyle: GroupBoxStyle {
	public static let counting = ChartsGroupBoxStyle(
		backgroundColor: Asset.Colors.Charts.background.swiftUIColor,
		labelColor: Asset.Colors.Charts.foreground.swiftUIColor
	)

	public static let averaging = ChartsGroupBoxStyle(
		backgroundColor: Asset.Colors.Charts.background.swiftUIColor,
		labelColor: Asset.Colors.Charts.foreground.swiftUIColor
	)

	public static let percentage = ChartsGroupBoxStyle(
		backgroundColor: Asset.Colors.Charts.background.swiftUIColor,
		labelColor: Asset.Colors.Charts.foreground.swiftUIColor
	)

	let backgroundColor: Color
	let labelColor: Color

	public func makeBody(configuration: Configuration) -> some View {
		configuration.content
			.padding(.top, .largeSpacing)
			.padding(.standardSpacing)
			.background(backgroundColor)
			.cornerRadius(.standardRadius)
			.overlay(
				configuration.label
					.font(.headline)
					.foregroundStyle(labelColor)
					.padding(.standardSpacing),
				alignment: .topLeading
			)
	}
}
