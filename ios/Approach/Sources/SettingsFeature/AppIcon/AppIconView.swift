import AssetsLibrary
import SwiftUI

struct AppIconView: View {
	let icon: Icon
	let title: String
	let message: String?
	let isCompact: Bool

	init(
		_ title: String,
		message: String? = nil,
		icon: Icon,
		isCompact: Bool = false
	) {
		self.title = title
		self.message = message
		self.icon = icon
		self.isCompact = isCompact
	}

	var body: some View {
		HStack {
			Image(uiImage: icon.image)
				.resizable()
				.scaledToFit()
				.frame(width: .standardIcon)
				.cornerRadius(.standardRadius)
				.shadow(radius: .standardRadius)
				.padding(.horizontal, isCompact ? .unitSpacing : .smallSpacing)
				.padding(.vertical, isCompact ? .unitSpacing : .standardSpacing)

			VStack(alignment: .leading, spacing: .unitSpacing) {
				Text(title)

				if let message {
					Text(message)
						.font(.caption)
				}
			}
		}
	}
}

extension AppIconView {
	public enum Icon {
		case appIcon(AppIcon)
		case image(UIImage)

		var image: UIImage {
			switch self {
			case let .appIcon(appIcon):
				return appIcon.image ?? UIImage()
			case let .image(image):
				return image
			}
		}
	}
}
