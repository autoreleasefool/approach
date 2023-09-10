import AssetsLibrary
import ComposableArchitecture
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StackedNotificationContent: Equatable {
	public let title: Item
	public let items: [Item]

	public init(title: Item, items: [Item]) {
		self.title = title
		self.items = items
	}
}

extension StackedNotificationContent {
	public struct Item: Identifiable, Equatable {
		public let message: TextState
		public let icon: SFSymbol?

		public init(message: TextState, icon: SFSymbol?) {
			self.message = message
			self.icon = icon
		}

		public var id: TextState { message }
	}
}

public struct StackedNotificationView: View {
	let content: StackedNotificationContent
	let style: ToastStyle

	@State private var itemWidth: CGFloat = .zero

	public var body: some View {
		VStack(alignment: .leading, spacing: 0) {
			HStack(spacing: .smallSpacing) {
				if let icon = content.title.icon {
					Image(systemSymbol: icon)
						.resizable()
						.scaledToFit()
						.frame(width: .extraTinyIcon, height: .extraTinyIcon)
				}

				Text(content.title.message)
			}
			.padding(.horizontal, .standardSpacing)
			.padding(.bottom, .smallSpacing)
			.matchWidth(byKey: WidthKey.self, to: $itemWidth)
			.border(edges: [.bottom], color: Asset.Colors.Primary.default)

			VStack(alignment: .leading, spacing: .unitSpacing) {
				ForEach(content.items) { item in
					HStack(spacing: .smallSpacing) {
						if let icon = item.icon {
							Image(systemSymbol: icon)
								.resizable()
								.scaledToFit()
								.frame(width: .extraTinyIcon, height: .extraTinyIcon)
						} else {
							Spacer()
								.frame(width: .extraTinyIcon, height: .extraTinyIcon)
						}

						Text(item.message)
					}
					.padding(.horizontal, .standardSpacing)
				}
			}
			.matchWidth(byKey: WidthKey.self, to: $itemWidth)
		}
		.padding(.vertical, .smallSpacing)
		.foregroundColor(style.foregroundColor)
		.background(
			RoundedRectangle(cornerRadius: .standardRadius)
				.fill(style.backgroundColor.swiftUIColor)
		)
		.padding(.horizontal, .standardSpacing)
		.padding(.top, .standardSpacing)
	}
}

private struct WidthKey: PreferenceKey, MatchDimensionPreferenceKey {}

#if DEBUG
struct StackedNotificationViewPreview: PreviewProvider {
	static var previews: some View {
		StackedNotificationView(
			content: .init(
				title: .init(message: .init("Toast!"), icon: .exclamationmarkCircle),
				items: [
					.init(message: .init("Item #1"), icon: ._1Circle),
					.init(message: .init("Item #2"), icon: nil),
				]
			),
			style: .success
		)
	}
}
#endif
