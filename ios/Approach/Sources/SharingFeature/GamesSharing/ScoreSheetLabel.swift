import AssetsLibrary
import SwiftUI

struct ScoreSheetLabel: View {
	@Environment(\.colorScheme) private var colorScheme

	struct Item: Identifiable, Equatable {
		let systemImage: String
		let title: String

		var id: String { title }
	}

	let item: Item
	let style: Style

	init(item: Item, style: Style) {
		self.item = item
		self.style = style
	}

	var body: some View {
		HStack(spacing: style.spacing) {
			Image(systemName: item.systemImage)
				.resizable()
				.scaledToFit()
				.frame(width: style.iconSize, height: style.iconSize)

			Text(item.title)
				.font(style.font)
		}
		.padding(.horizontal, .smallSpacing)
		.padding(.vertical, style.padding)
		.background(
			colorScheme == .dark ? Color.gray.opacity(0.8) : Color.black.opacity(0.2),
			in: RoundedRectangle(cornerRadius: .standardRadius)
		)
	}

	struct Style {
		let font: Font
		let iconSize: CGFloat
		let spacing: CGFloat
		let padding: CGFloat

		static let title: Self = .init(
			font: .title3.weight(.bold),
			iconSize: .smallIcon,
			spacing: .standardSpacing,
			padding: .smallSpacing
		)

		static let plain: Self = .init(
			font: .body,
			iconSize: .smallIcon,
			spacing: .standardSpacing,
			padding: .unitSpacing
		)

		static let small: Self = .init(
			font: .caption2,
			iconSize: 8,
			spacing: .smallSpacing,
			padding: .unitSpacing
		)
	}
}
