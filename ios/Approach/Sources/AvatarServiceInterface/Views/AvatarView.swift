import AssetsLibrary
import Dependencies
import SharedModelsLibrary
import SwiftUI

public struct AvatarView: View {
	let avatar: Avatar
	let size: CGFloat

	@State private var image: UIImage?

	public init(
		_ avatar: Avatar,
		size: CGFloat
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
					.frame(width: size, height: size)
					.cornerRadius(size)
			} else {
				Color.clear
					.frame(width: size, height: size)
					.cornerRadius(size)
			}
		}.task {
			@Dependency(\.avatarService) var avatarService: AvatarService
			image = await avatarService.render(avatar)
		}
	}
}

#if DEBUG
struct AvatarViewPreviews: PreviewProvider {
	static var previews: some View {
		VStack {
			HStack {
				AvatarView(.text("JR", .red()), size: .smallIcon)
				AvatarView(.text("JR", .red()), size: .standardIcon)
				AvatarView(.text("JR", .red()), size: .largeIcon)
			}

			HStack {
				AvatarView(.text("JR", .red()), size: .smallIcon)
				AvatarView(.text("JR", .red()), size: .standardIcon)
				AvatarView(.text("JR", .red()), size: .largeIcon)
			}
		}
	}
}
#endif
