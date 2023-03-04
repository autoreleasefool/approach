import SwiftUI
import AssetsLibrary

public struct BadgeView: View {
	let text: String
	let style: Style

	public init(_ text: String, style: Style = .plain) {
		self.text = text
		self.style = style
	}

	public var body: some View {
		Text(text)
			.lineLimit(1)
			.font(.caption)
			.foregroundColor(style.foregroundColor)
			.padding(.vertical, .tinySpacing)
			.padding(.horizontal, .smallSpacing)
			.background(
				RoundedRectangle(cornerRadius: .standardRadius)
					.strokeBorder(style.foregroundColor, lineWidth: 1)
					.background(
						RoundedRectangle(cornerRadius: .standardRadius)
							.foregroundColor(style.backgroundColor)
					)
			)
	}
}

extension BadgeView {
	public enum Style {
		case primary
		case plain
		case success
		case destructive
		case info
		case custom(foreground: Color, background: Color)

		var foregroundColor: Color {
			switch self {
			case .primary: return .appPrimary
			case .plain: return .black
			case .success: return .green
			case .destructive: return .appDestructive
			case .info: return .blue
			case let .custom(foreground, _): return foreground
			}
		}

		var backgroundColor: Color {
			switch self {
			case .primary: return .appPrimaryLight
			case .plain: return .gray
			case .success: return .teal
			case .destructive: return .pink
			case .info: return .cyan
			case let .custom(_, background): return background
			}
		}
	}
}
