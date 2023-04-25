import AssetsLibrary
import ModelsLibrary
import SwiftUI

public struct AvatarLabelView: View {
	let avatar: Avatar.Summary
	let size: CGFloat
	let title: String?
	let subtitle: String?

	public init(
		_ avatar: Avatar.Summary,
		size: CGFloat,
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
