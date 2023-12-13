import AlleyEditorFeature
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ExtensionsLibrary
import FeatureActionLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourceListLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct AlleysListView: View {
	let store: StoreOf<AlleysList>

	struct ViewState: Equatable {
		let bowlerNameForAverages: String?
		let isAnyFilterActive: Bool
		let isShowingAverages: Bool

		init(state: AlleysList.State) {
			self.bowlerNameForAverages = state.bowlerForAverages?.name
			self.isAnyFilterActive = state.filter != .init()
			self.isShowingAverages = state.isAlleyAndGearAveragesEnabled
		}
	}

	public init(store: StoreOf<AlleysList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { alley in
				if viewStore.isShowingAverages {
					VStack {
						Alley.View(alley)
						Text(format(average: alley.average))
							.font(.caption)
					}
				} else {
					Alley.View(alley)
				}
			} header: {
				header(viewStore)
			}
			.navigationTitle(Strings.Alley.List.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: viewStore.isAnyFilterActive) {
						viewStore.send(.didTapFiltersButton)
					}
				}
			}
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.alleyEditor(store.scope(state: \.$destination.editor, action: \.internal.destination.editor))
		.alleysFilter(store.scope(state: \.$destination.filters, action: \.internal.destination.filters))
	}

	@MainActor @ViewBuilder private func header(
		_ viewStore: ViewStore<ViewState, AlleysList.Action.ViewAction>
	) -> some View {
		if viewStore.isShowingAverages {
			Section {
				Button { viewStore.send(.didTapBowler) } label: {
					LabeledContent(
						Strings.List.Averages.showAverages,
						value: viewStore.bowlerNameForAverages ?? Strings.List.Averages.allBowlers
					)
				}
				.buttonStyle(.navigation)
			}
		} else {
			EmptyView()
		}
	}
}

@MainActor extension View {
	fileprivate func alleyEditor(_ store: PresentationStoreOf<AlleyEditor>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
	}

	fileprivate func alleysFilter(_ store: PresentationStoreOf<AlleysFilter>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				AlleysFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}
}
