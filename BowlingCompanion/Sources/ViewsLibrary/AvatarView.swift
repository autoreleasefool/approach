import SwiftUI
import ThemesLibrary

public struct AvatarView: View {
	let image: UIImage?
	let size: Size
	let title: String?
	let subtitle: String?

	public init(_ image: UIImage? = nil, size: Size, title: String? = nil, subtitle: String? = nil) {
		self.image = image
		self.size = size
		self.title = title
		self.subtitle = subtitle
	}

	public var body: some View {
		HStack(alignment: .center, spacing: .standardSpacing) {
			if let image {
				Image(uiImage: image)
					.resizable()
					.frame(width: size.value, height: size.value)
					.cornerRadius(size.value)
			} else {
				Color.red
					.frame(width: size.value, height: size.value)
					.cornerRadius(size.value)
			}

			if title != nil || subtitle != nil {
				VStack(alignment: .leading, spacing: 0) {
					if let title {
						Text(title)
					}

					if let subtitle {
						Text(subtitle)
							.font(.caption)
					}
				}
			}
		}
	}
}

extension AvatarView {
	public enum Size {
		case small
		case medium
		case large
		case custom(CGFloat)

		var value: CGFloat {
			switch self {
			case .small: return .smallIcon
			case .medium: return .standardIcon
			case .large: return .largeIcon
			case let .custom(value): return value
			}
		}
	}
}

#if DEBUG
struct AvatarViewPreviews: PreviewProvider {
	static var previews: some View {
		VStack {
			HStack {
				AvatarView(size: .small)
				AvatarView(size: .medium)
				AvatarView(size: .large)
			}

			HStack {
				AvatarView(size: .small, title: "Ball Rolled")
				AvatarView(size: .medium, title: "Ball Rolled")
				AvatarView(size: .large, title: "Ball Rolled")
			}

			HStack {
				AvatarView(size: .small, title: "Ball Rolled", subtitle: "Something")
				AvatarView(size: .medium, title: "Ball Rolled", subtitle: "Something")
				AvatarView(size: .large, title: "Ball Rolled", subtitle: "Something")
			}
		}
	}
}
#endif
