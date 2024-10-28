import AssetsLibrary
import Dependencies
import ModelsLibrary
import SwiftUI

public struct AvatarView: View {
	let avatar: Avatar.Value?
	let size: CGFloat

	@State private var image: UIImage?

	public init(_ avatar: Avatar.Summary?, size: CGFloat) {
		self.init(avatar?.value, size: size)
	}

	public init(_ avatar: Avatar.Value?, size: CGFloat) {
		self.avatar = avatar
		self.size = size
	}

	public var body: some View {
		avatarImage
			.task(id: avatar) {
				guard let avatar else { return }
				@Dependency(AvatarService.self) var avatars
				image = await avatars.render(avatar)
			}
	}

	@ViewBuilder private var avatarImage: some View {
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
	}
}
