import AssetsLibrary
import SwiftUI

public struct BannerSection: View {
	let content: Banner.Content
	let style: Banner.Style

	public init(_ content: Banner.Content, style: Banner.Style) {
		self.content = content
		self.style = style
	}

	public var body: some View {
		Section {
			Banner(content, style: style)
		}
		.listRowInsets(EdgeInsets())
	}
}

public struct Banner: View {
	let content: Content
	let style: Style

	public init(_ content: Content, style: Style) {
		self.content = content
		self.style = style
	}

	public var body: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			if let title = content.title {
				Text(title)
					.font(.headline)
					.frame(maxWidth: .infinity, alignment: .leading)
			}

			if let message = content.message {
				Text(message)
					.multilineTextAlignment(.leading)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
		}
		.padding()
		.frame(maxWidth: .infinity)
		.foregroundStyle(style.foregroundColor)
		.background(style.backgroundColor)
		.cornerRadius(.standardRadius)
	}
}

extension Banner {
	public enum Content {
		case title(String)
		case message(String)
		case titleAndMessage(String, String)

		var title: String? {
			switch self {
			case .title(let title), .titleAndMessage(let title, _):
				return title
			case .message:
				return nil
			}
		}

		var message: String? {
			switch self {
			case .message(let message), .titleAndMessage(_, let message):
				return message
			case .title:
				return nil
			}
		}
	}
}

extension Banner {
	public struct Style: Sendable {
		let foregroundColor: Color
		let backgroundColor: Color

		public init(foreground: Color, background: Color) {
			self.foregroundColor = foreground
			self.backgroundColor = background
		}

		@MainActor public static let primary: Self = .init(
			foreground: Asset.Colors.Primary.default.swiftUIColor,
			background: Asset.Colors.Primary.light.swiftUIColor
		)

		@MainActor public static let plain: Self = .init(
			foreground: .black,
			background: .gray
		)

		@MainActor public static let success: Self = .init(
			foreground: Color.black,
			background: Asset.Colors.Success.default.swiftUIColor
		)

		@MainActor public static let destructive: Self = .init(
			foreground: Asset.Colors.Destructive.default.swiftUIColor,
			background: .pink
		)

		@MainActor public static let warning: Self = .init(
			foreground: Color.black,
			background: Asset.Colors.Warning.background.swiftUIColor
		)

		@MainActor public static let error: Self = .init(
			foreground: Color(uiColor: .label),
			background: Asset.Colors.Error.light.swiftUIColor
		)

		@MainActor public static let info: Self = .init(
			foreground: .blue,
			background: .cyan
		)
	}
}
