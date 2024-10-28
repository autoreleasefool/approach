import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct SortOrderView<Ordering: Orderable>: View {
	public var store: StoreOf<SortOrder<Ordering>>

	public init(store: StoreOf<SortOrder<Ordering>>) {
		self.store = store
	}

	public var body: some View {
		List {
			Section(Strings.SortOrder.title) {
				ForEach(store.options, id: \.self) { ordering in
					Button {
						store.send(.view(.didTapOption(ordering)))
					} label: {
						HStack(alignment: .center, spacing: .standardSpacing) {
							Image(systemSymbol: store.ordering == ordering ? .checkmarkCircleFill : .circle)
								.resizable()
								.frame(width: .smallIcon, height: .smallIcon)
								.foregroundColor(Asset.Colors.Action.default)
							Text(String(describing: ordering))
								.frame(maxWidth: .infinity, alignment: .leading)
						}
						.frame(maxWidth: .infinity)
						.contentShape(Rectangle())
					}
					.buttonStyle(TappableElement())
				}
			}
		}
	}
}
