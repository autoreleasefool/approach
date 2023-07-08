import AssetsLibrary
import SwiftUI

public struct AppIconView: View {
	let icon: Icon
	let title: String
	let isCompact: Bool

	init(_ title: String, icon: Icon, isCompact: Bool = false) {
		self.title = title
		self.icon = icon
		self.isCompact = isCompact
	}

	public var body: some View {
		HStack {
			Image(uiImage: icon.image)
				.resizable()
				.scaledToFit()
				.frame(width: .standardIcon)
				.cornerRadius(.standardRadius)
				.shadow(radius: .standardRadius)
				.padding(.horizontal, isCompact ? .unitSpacing : .smallSpacing)
				.padding(.vertical, isCompact ? .unitSpacing : .standardSpacing)
			Text(title)
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
				return UIImage(named: appIcon.rawValue) ?? UIImage()
			case let .image(image):
				return image
			}
		}
	}
}
