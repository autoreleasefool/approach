import AssetsLibrary
import SwiftUI

public struct Chip: View {
	public let title: String
	public let systemImage: String?
	public let accessory: Accessory?
	public let size: Sizing
	public let style: Style

	public init(
		title: String,
		systemImage: String? = nil,
		accessory: Accessory? = nil,
		size: Sizing? = nil,
		style: Style
	) {
		self.title = title
		self.systemImage = systemImage
		self.accessory = accessory
		self.size = size ?? .standard
		self.style = style
	}

	public var body: some View {
		HStack(alignment: .center) {
			if let systemImage {
				Image(systemName: systemImage)
					.resizable()
					.frame(width: size.icon, height: size.icon)
					.padding(.trailing, .smallSpacing)
					.foregroundColor(style.foreground)
			}

			Text(title)
				.lineLimit(0)
				.font(size.font)
				.foregroundColor(style.foreground)
				.minimumScaleFactor(0.5)

			if let accessory {
				Spacer()

				Image(systemName: accessory.systemImage)
					.resizable()
					.frame(width: size.icon, height: size.icon)
					.padding(.leading, .smallSpacing)
					.foregroundColor(style.foreground)
			}
		}
		.padding(.all, size.padding)
		.background(
			RoundedRectangle(cornerRadius: size.radius)
				.fill(style.background.swiftUIColor)
				.strokeBorder(style.foreground.swiftUIColor, lineWidth: 2)
		)
	}
}

extension Chip {
	public enum Accessory {
		case radioBox
		case radioBoxSelected

		var systemImage: String {
			switch self {
			case .radioBox: "circle"
			case .radioBoxSelected: "checkmark.circle.fill"
			}
		}
	}
}

extension Chip {
	public struct Sizing: Sendable {
		public let padding: CGFloat
		public let icon: CGFloat
		public let radius: CGFloat
		public let font: Font

		public static let standard = Self(
			padding: .standardSpacing,
			icon: .tinyIcon,
			radius: .largeRadius,
			font: .body
		)

		public static let compact = Self(
			padding: .smallSpacing,
			icon: .extraTinyIcon,
			radius: .standardRadius,
			font: .caption
		)
	}
}

extension Chip {
	public struct Style: Sendable {
		public let foreground: ColorAsset
		public let background: ColorAsset

		public init(foreground: ColorAsset, background: ColorAsset) {
			self.foreground = foreground
			self.background = background
		}

		public static let plain = Self(
			foreground: Asset.Colors.Chip.plainForeground,
			background: Asset.Colors.Chip.plainBackground
		)

		public static let info = Self(
			foreground: Asset.Colors.Chip.infoForeground,
			background: Asset.Colors.Chip.infoBackground
		)

		public static let primary = Self(
			foreground: Asset.Colors.Chip.primaryForeground,
			background: Asset.Colors.Chip.primaryBackground
		)
	}
}

#Preview {
	ForEach([
		Chip.Style.plain,
		Chip.Style.info,
		Chip.Style.primary,
	], id: \.foreground.name) { style in
		VStack {
			Chip(title: "Title", style: style)
			Chip(title: "Title", accessory: .radioBoxSelected, style: style)
			Chip(title: "Title", systemImage: "person", accessory: .radioBoxSelected, style: style)
			Chip(title: "Title", systemImage: "person.fill", accessory: .radioBoxSelected, style: style)
		}
	}
}
