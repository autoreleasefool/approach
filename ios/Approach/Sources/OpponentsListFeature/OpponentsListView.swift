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

@ViewAction(for: OpponentsList.self)
public struct OpponentsListView: View {
	@Bindable public var store: StoreOf<OpponentsList>

	public init(store: StoreOf<OpponentsList>) {
		self.store = store
	}

	public var body: some View {
		ResourceListView(
			store: store.scope(state: \.list, action: \.internal.list)
		) { opponent in
			if store.isOpponentDetailsEnabled {
				Button { send(.didTapOpponent(opponent.id)) } label: {
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
		.onAppear { send(.onAppear) }
		.navigationTitle(Strings.Opponent.List.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) { send(.didTapSortOrderButton) }
			}
		}
		.opponentDetails($store.scope(state: \.destination?.details, action: \.internal.destination.details))
		.opponentEditor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
		.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}
}

@MainActor extension View {
	fileprivate func opponentDetails(_ store: Binding<StoreOf<OpponentDetails>?>) -> some View {
		navigationDestination(item: store) {
			OpponentDetailsView(store: $0)
		}
	}

	fileprivate func opponentEditor(_ store: Binding<StoreOf<BowlerEditor>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}

	fileprivate func sortOrder(_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Bowler.Ordering>>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
