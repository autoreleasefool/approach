import ComposableArchitecture
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SortOrderView<Ordering: Orderable>: View {
	let store: StoreOf<SortOrder<Ordering>>

	struct ViewState: Equatable {
		let options: [Ordering]
		let selected: Ordering
		let isSheetPresented: Bool

		init(state: SortOrder<Ordering>.State) {
			self.options = state.options
			self.selected = state.ordering
			self.isSheetPresented = state.isSheetPresented
		}
	}

	enum ViewAction {
		case setSheetPresented(isPresented: Bool)
		case optionTapped(Ordering)
	}

	public init(store: StoreOf<SortOrder<Ordering>>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SortOrder<Ordering>.Action.init) { viewStore in
			SortButton(isActive: false) {
				viewStore.send(.setSheetPresented(isPresented: true))
			}
			.disabled(viewStore.isSheetPresented)
			.sheet(isPresented: viewStore.binding(
				get: \.isSheetPresented,
				send: ViewAction.setSheetPresented(isPresented:)
			)) {
				List {
					Section("Sort Order") {
						ForEach(viewStore.options, id: \.self) { ordering in
							Button {
								viewStore.send(.optionTapped(ordering))
							} label: {
								HStack(alignment: .center, spacing: .standardSpacing) {
									Image(systemName: viewStore.selected == ordering ? "checkmark.circle.fill" : "circle")
										.resizable()
										.frame(width: .smallIcon, height: .smallIcon)
											.foregroundColor(.appAction)
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
				.presentationDetents([.medium, .large])
			}
		}
	}
}

extension SortOrder.Action {
	init(action: SortOrderView<Ordering>.ViewAction) {
		switch action {
		case let .setSheetPresented(isPresented):
			self = .setSheetPresented(isPresented: isPresented)
		case let .optionTapped(option):
			self = .optionTapped(option)
		}
	}
}
