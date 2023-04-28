import AssetsLibrary
import SwiftUI

public struct Banner: View {
	let title: String
	let message: String?
	let style: Style

	public init(_ title: String, message: String? = nil, style: Style = .plain) {
		self.title = title
		self.message = message
		self.style = style
	}

	public var body: some View {
		VStack(alignment: .leading, spacing: .smallSpacing) {
			Text(title)
				.font(.headline)

			if let message {
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
