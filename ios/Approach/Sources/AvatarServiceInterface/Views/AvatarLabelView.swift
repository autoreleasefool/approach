import AssetsLibrary
import SharedModelsLibrary
import SwiftUI

public struct AvatarLabelView: View {
	let avatar: Avatar
	let size: AvatarView.Size
	let title: String?
	let subtitle: String?

	public init(
		_ avatar: Avatar,
		size: AvatarView.Size,
		title: String? = nil,
		subtitle: String? = nil
	) {
		self.avatar = avatar
		self.size = size
		self.title = title
		self.subtitle = subtitle
	}

	public var body: some View {
		HStack(alignment: .center, spacing: .standardSpacing) {
			AvatarView(avatar, size: size)

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

#if DEBUG
struct AvatarLabelViewPreviews: PreviewProvider {
	static var previews: some View {
		VStack {
			HStack {
				AvatarLabelView(.text("J", .red()), size: .small)
				AvatarLabelView(.text("J", .red()),size: .medium)
				AvatarLabelView(.text("J", .red()),size: .large)
			}

			HStack {
				AvatarLabelView(.text("J", .red()), size: .small, title: "Ball Rolled")
				AvatarLabelView(.text("J", .red()), size: .medium, title: "Ball Rolled")
				AvatarLabelView(.text("J", .red()), size: .large, title: "Ball Rolled")
			}

			HStack {
				AvatarLabelView(.text("J", .red()), size: .small, title: "Ball Rolled", subtitle: "Something")
				AvatarLabelView(.text("J", .red()), size: .medium, title: "Ball Rolled", subtitle: "Something")
				AvatarLabelView(.text("J", .red()), size: .large, title: "Ball Rolled", subtitle: "Something")
			}
		}
	}
}
#endif
