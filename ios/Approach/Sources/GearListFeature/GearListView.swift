import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ErrorsFeature
import ExtensionsLibrary
import FeatureActionLibrary
import GearEditorFeature
import ModelsLibrary
import ModelsViewsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearListView: View {
	let store: StoreOf<GearList>

	struct ViewState: Equatable {
		let isAnyFilterActive: Bool

		init(state: GearList.State) {
			self.isAnyFilterActive = state.kindFilter != nil
		}
	}

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) {
				Gear.ViewWithAvatar($0)
			}
			.navigationTitle(Strings.Gear.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFilterButton)
					}
				}
				ToolbarItem(placement: .navigationBarTrailing) {
					SortButton(isActive: false) { viewStore.send(.didTapSortOrderButton) }
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.gearEditor(store.scope(state: \.$destination.editor, action: \.internal.destination.editor))
		.gearFilters(store.scope(state: \.$destination.filters, action: \.internal.destination.filters))
		.sortOrder(store.scope(state: \.$destination.sortOrder, action: \.internal.destination.sortOrder))
	}
}

@MainActor extension View {
	fileprivate func gearEditor(_ store: PresentationStoreOf<GearEditor>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				GearEditorView(store: store)
			}
		}
	}

	fileprivate func gearFilters(_ store: PresentationStoreOf<GearFilter>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				GearFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}

	fileprivate func sortOrder(_ store: PresentationStoreOf<SortOrderLibrary.SortOrder<Gear.Ordering>>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
