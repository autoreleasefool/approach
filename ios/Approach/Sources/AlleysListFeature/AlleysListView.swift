import AlleyEditorFeature
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ExtensionsPackageLibrary
import FeatureActionLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: AlleysList.self)
public struct AlleysListView: View {
	@Bindable public var store: StoreOf<AlleysList>

	public init(store: StoreOf<AlleysList>) {
		self.store = store
	}

	public var body: some View {
		ResourceListView(
			store: store.scope(state: \.list, action: \.internal.list)
		) { alley in
			if store.isShowingAverages {
				VStack {
					Alley.View(alley)
					Text(format(average: alley.average))
						.font(.caption)
				}
			} else {
				Alley.View(alley)
			}
		} header: {
			header
		}
		.navigationTitle(Strings.Alley.List.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				FilterButton(isActive: store.isAnyFilterActive) {
					send(.didTapFiltersButton)
				}
			}

			ToolbarItem(placement: .navigationBarTrailing) {
				SortButton(isActive: false) {
					send(.didTapSortOrderButton)
				}
			}
		}
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.alleyEditor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
		.alleysFilter($store.scope(state: \.destination?.filters, action: \.internal.destination.filters))
		.sortOrder($store.scope(state: \.destination?.sortOrder, action: \.internal.destination.sortOrder))
	}

	@ViewBuilder private var header: some View {
		if store.isShowingAverages {
			Section {
				Button { send(.didTapBowler) } label: {
					LabeledContent(
						Strings.List.Averages.showAverages,
						value: store.bowlerName ?? Strings.List.Averages.allBowlers
					)
				}
				.buttonStyle(.navigation)
			}
		} else {
			EmptyView()
		}
	}
}

extension View {
	fileprivate func alleyEditor(_ store: Binding<StoreOf<AlleyEditor>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
	}

	fileprivate func alleysFilter(_ store: Binding<StoreOf<AlleysFilter>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				AlleysFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}

	fileprivate func sortOrder(_ store: Binding<StoreOf<SortOrderLibrary.SortOrder<Alley.Ordering>>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				SortOrderView(store: store)
			}
			.presentationDetents([.medium])
		}
	}
}
