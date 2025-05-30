import AssetsLibrary
import SwiftUI

public struct BadgeView: View {
	let text: String
	let style: Style

	public init(_ text: String, style: Style) {
		self.text = text
		self.style = style
	}

	public var body: some View {
		Text(text)
			.lineLimit(1)
			.font(.caption)
			.foregroundStyle(style.foregroundStyle)
			.padding(.vertical, .tinySpacing)
			.padding(.horizontal, .smallSpacing)
			.background(
				RoundedRectangle(cornerRadius: .standardRadius)
					.strokeBorder(style.foregroundStyle, lineWidth: 1)
					.background(
						RoundedRectangle(cornerRadius: .standardRadius)
							.foregroundStyle(style.backgroundColor)
					)
			)
	}
}

extension BadgeView {
	public struct Style: Sendable {
		let foregroundStyle: Color
		let backgroundColor: Color

		public init(foreground: Color, background: Color) {
			self.foregroundStyle = foreground
			self.backgroundColor = background
		}

		public static let primary: Self = .init(
			foreground: Asset.Colors.Primary.default.swiftUIColor,
			background: Asset.Colors.Primary.light.swiftUIColor
		)

		public static let plain: Self = .init(
			foreground: .black,
			background: .gray
		)

		public static let success: Self = .init(
			foreground: .green,
			background: .teal
		)

		public static let destructive: Self = .init(
			foreground: Asset.Colors.Destructive.default.swiftUIColor,
			background: .pink
		)

		public static let info: Self = .init(
			foreground: .blue,
			background: .cyan
		)
	}
}
