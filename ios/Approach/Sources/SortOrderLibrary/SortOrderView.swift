import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SortOrderView<Ordering: Orderable>: View {
	let store: StoreOf<SortOrder<Ordering>>

	struct ViewState: Equatable {
		let options: [Ordering]
		let selected: Ordering

		init(state: SortOrder<Ordering>.State) {
			self.options = state.options
			self.selected = state.ordering
		}
	}

	public init(store: StoreOf<SortOrder<Ordering>>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				Section(Strings.SortOrder.title) {
					ForEach(viewStore.options, id: \.self) { ordering in
						Button {
							viewStore.send(.didTapOption(ordering))
						} label: {
							HStack(alignment: .center, spacing: .standardSpacing) {
								Image(systemName: viewStore.selected == ordering ? "checkmark.circle.fill" : "circle")
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
		})
	}
}
