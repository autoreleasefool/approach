import AssetsLibrary
import SwiftUI

public struct Banner: View {
	let content: Content
	let style: Style

	public init(_ content: Content, style: Style = .plain) {
		self.content = content
		self.style = style
	}

	public var body: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			if let title = content.title {
				Text(title)
					.font(.headline)
			}

			if let message = content.message {
				Text(message)
					.multilineTextAlignment(.leading)
			}
		}
		.padding()
		.frame(maxWidth: .infinity)
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
	public struct Style {
		let foregroundColor: Color
		let backgroundColor: Color

		public init(foreground: Color, background: Color) {
			self.foregroundColor = foreground
			self.backgroundColor = background
		}

		public static let primary: Self = .init(foreground: .appPrimary, background: .appPrimaryLight)
		public static let plain: Self = .init(foreground: .black, background: .gray)
		public static let success: Self = .init(foreground: .green, background: .teal)
		public static let destructive: Self = .init(foreground: .appDestructive, background: .pink)
		public static let error: Self = .init(foreground: Color(uiColor: .label), background: .appErrorLight)
		public static let info: Self = .init(foreground: .blue, background: .cyan)
	}
}
