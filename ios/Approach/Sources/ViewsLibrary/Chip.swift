import AssetsLibrary
import SwiftUI

public struct Chip: View {
	public let title: String
	public let icon: SFSymbol?
	public let accessory: Accessory?
	public let style: Style

	public init(
		title: String,
		icon: SFSymbol? = nil,
		accessory: Accessory? = nil,
		style: Style = .plain
	) {
		self.title = title
		self.icon = icon
		self.accessory = accessory
		self.style = style
	}

	public var body: some View {
		HStack(alignment: .center) {
			if let icon {
				Image(systemSymbol: icon)
					.resizable()
					.frame(width: .smallIcon, height: .smallIcon)
					.padding(.trailing, .smallSpacing)
					.foregroundColor(style.foreground)
			}

			Text(title)
				.font(.body)
				.foregroundColor(style.foreground)

			Spacer()

			if let accessory {
				Image(systemSymbol: accessory.systemSymbol)
					.resizable()
					.frame(width: .smallIcon, height: .smallIcon)
					.padding(.leading, .smallSpacing)
					.foregroundColor(style.foreground)
			}
		}
		.padding(.all, .standardSpacing)
		.background(
			RoundedRectangle(cornerRadius: .largeRadius)
				.fill(style.background.swiftUIColor)
				.strokeBorder(style.foreground.swiftUIColor, lineWidth: 2)
		)
	}
}

extension Chip {
	public enum Accessory {
		case radioBox
		case radioBoxSelected

		var systemSymbol: SFSymbol {
			switch self {
			case .radioBox: .circle
			case .radioBoxSelected: .checkmarkCircleFill
			}
		}
	}
}

extension Chip {
	public struct Style {
		public let foreground: ColorAsset
		public let background: ColorAsset

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
			Chip(title: "Title", icon: .person, accessory: .radioBoxSelected, style: style)
			Chip(title: "Title", icon: .personFill, accessory: .radioBoxSelected, style: style)
		}
	}
}
