import AssetsLibrary
import SharedModelsLibrary
import SwiftUI

public struct AvatarView: View {
	let avatar: Avatar?
	let size: Size
	let editable: Bool

	public init(
		_ avatar: Avatar,
		size: Size,
		editable: Bool = false
	) {
		self.avatar = avatar
		self.size = size
		self.editable = editable
	}

	public var body: some View {
		HStack(alignment: .center, spacing: .standardSpacing) {
			ZStack {
				if let image = avatar?.image {
					Image(uiImage: image)
						.resizable()
						.scaledToFit()
						.frame(width: size.value, height: size.value)
						.cornerRadius(size.value)
				} else {
					// TODO: support background color
					Color.red
						.frame(width: size.value, height: size.value)
						.cornerRadius(size.value)
				}

				if editable {
					Image(systemName: "pencil.circle.fill")
						.resizable()
						.frame(width: size.value / 4, height: size.value / 4, alignment: .bottomTrailing)
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
				AvatarView(.text("JR", .random()), size: .small)
				AvatarView(.text("JR", .random()), size: .medium)
				AvatarView(.text("JR", .random()), size: .large)
			}

			HStack {
				AvatarView(.text("JR", .random()), size: .small, editable: true)
				AvatarView(.text("JR", .random()), size: .medium, editable: true)
				AvatarView(.text("JR", .random()), size: .large, editable: true)
			}
		}
	}
}
#endif
