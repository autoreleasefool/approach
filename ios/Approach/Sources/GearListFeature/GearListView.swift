import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ErrorsFeature
import ExtensionsPackageLibrary
import FeatureActionLibrary
import GearEditorFeature
import ModelsLibrary
import ModelsViewsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: GearList.self)
public struct GearListView: View {
	@Bindable public var store: StoreOf<GearList>

	public init(store: StoreOf<GearList>) {
		self.store = store
	}

	public var body: some View {
		ResourceListView(
			store: store.scope(state: \.list, action: \.internal.list)
		) {
			Gear.ViewWithAvatar($0)
		}
		.navigationTitle(Strings.Gear.List.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				FilterButton(isActive: store.isAnyFilterActive) {
					send(.didTapFilterButton)
				}
			}
			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) { send(.didTapSortOrderButton) }
			}
		}
		.onAppear { send(.onAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.gearEditor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
		.gearFilters($store.scope(state: \.destination?.filters, action: \.internal.destination.filters))
		.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
	}
}

@MainActor extension View {
	fileprivate func gearEditor(_ store: Binding<StoreOf<GearEditor>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				GearEditorView(store: store)
			}
		}
	}

	fileprivate func gearFilters(_ store: Binding<StoreOf<GearFilter>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				GearFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}

	fileprivate func sortOrder(_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Gear.Ordering>>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
