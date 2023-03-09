import AssetsLibrary
import Dependencies
import SharedModelsLibrary
import SwiftUI

public struct AvatarView: View {
	let avatar: Avatar
	let size: Size

	@State private var image: UIImage?

	public init(
		_ avatar: Avatar,
		size: Size
	) {
		self.avatar = avatar
		self.size = size
	}

	public var body: some View {
		Group {
			if let image {
				Image(uiImage: image)
					.resizable()
					.scaledToFit()
					.frame(width: size.value, height: size.value)
					.cornerRadius(size.value)
			} else {
				Color.clear
					.frame(width: size.value, height: size.value)
					.cornerRadius(size.value)
			}
		}.task {
			@Dependency(\.avatarService) var avatarService: AvatarService
			image = await avatarService.render(avatar)
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
				AvatarView(.text("JR", .red()), size: .small)
				AvatarView(.text("JR", .red()), size: .medium)
				AvatarView(.text("JR", .red()), size: .large)
			}

			HStack {
				AvatarView(.text("JR", .red()), size: .small)
				AvatarView(.text("JR", .red()), size: .medium)
				AvatarView(.text("JR", .red()), size: .large)
			}
		}
	}
}
#endif
