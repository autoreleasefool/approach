import AssetsLibrary
import BowlerEditorFeature
import ComposableArchitecture
import ErrorsFeature
import ExtensionsLibrary
import FeatureActionLibrary
import ModelsLibrary
import ModelsViewsLibrary
import OpponentDetailsFeature
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct OpponentsListView: View {
	let store: StoreOf<OpponentsList>

	struct ViewState: Equatable {
		let isOpponentDetailsEnabled: Bool

		init(state: OpponentsList.State) {
			self.isOpponentDetailsEnabled = state.isOpponentDetailsEnabled
		}
	}

	public init(store: StoreOf<OpponentsList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { opponent in
				if viewStore.isOpponentDetailsEnabled {
					Button { store.send(.view(.didTapOpponent(opponent.id))) } label: {
						Bowler.View(opponent)
					}
					.buttonStyle(.navigation)
				} else {
					Bowler.View(opponent)
				}
			} header: {
				Section {
					Text(Strings.Opponent.List.description)
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.navigationTitle(Strings.Opponent.List.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) { store.send(.view(.didTapSortOrderButton)) }
			}
		}
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.opponentDetails(store.scope(state: \.$destination.details, action: \.internal.destination.details))
		.opponentEditor(store.scope(state: \.$destination.editor, action: \.internal.destination.editor))
		.sortOrder(store.scope(state: \.$destination.sortOrder, action: \.internal.destination.sortOrder))
	}
}

@MainActor extension View {
	fileprivate func opponentDetails(_ store: PresentationStoreOf<OpponentDetails>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<OpponentDetails>) in
			OpponentDetailsView(store: store)
		}
	}

	fileprivate func opponentEditor(_ store: PresentationStoreOf<BowlerEditor>) -> some View {
		sheet(store: store) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: PresentationStoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
