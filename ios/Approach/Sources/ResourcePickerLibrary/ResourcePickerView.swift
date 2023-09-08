import AssetsLibrary
import ComposableArchitecture
import ListContentLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ResourcePickerView<Resource: PickableResource, Query: Equatable, Row: View>: View {
	let store: StoreOf<ResourcePicker<Resource, Query>>
	let row: (Resource) -> Row

	struct ViewState: Equatable {
		let listState: ListContentState<Resource, ListErrorContent>
		let selected: Set<Resource.ID>
		let isCancellable: Bool
		let limit: Int

		init(state: ResourcePicker<Resource, Query>.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let resources = state.resources {
				self.listState = .loaded(resources)
			} else {
				self.listState = .loading
			}
			self.selected = state.selected
			self.isCancellable = state.showsCancelHeaderButton && state.selected != state.initialSelection
			self.limit = state.limit
		}
	}

	public init(store: StoreOf<ResourcePicker<Resource, Query>>, @ViewBuilder row: @escaping (Resource) -> Row) {
		self.store = store
		self.row = row
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ListContent(viewStore.listState) { resources in
				Section {
					ForEach(resources) { resource in
						Button {
							viewStore.send(.didTapResource(resource))
						} label: {
							HStack(alignment: .center, spacing: .standardSpacing) {
								Image(systemSymbol: viewStore.selected.contains(resource.id) ? .checkmarkCircleFill : .circle)
									.resizable()
									.frame(width: .smallIcon, height: .smallIcon)
									.foregroundColor(Asset.Colors.Action.default)
								row(resource)
									.frame(maxWidth: .infinity, alignment: .leading)
							}
							.frame(maxWidth: .infinity)
							.contentShape(Rectangle())
						}
						.buttonStyle(TappableElement())
					}
				}

				Section {
					Button(role: .destructive) {
						viewStore.send(.didTapDeselectAllButton)
					} label: {
						Label(Strings.Action.deselectAll, systemSymbol: .trash)
					}
					.disabled(viewStore.selected.isEmpty)
				}
			} empty: {
				ListEmptyContent(
					Asset.Media.EmptyState.picker,
					title: Strings.Picker.Empty.title
				) {
					EmptyContentAction(title: Strings.Action.cancel) {
						viewStore.send(.didTapCancelButton)
					}
				}
			} error: { error in
				ListEmptyContent(
					Asset.Media.Error.notFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) {
						viewStore.send(.didTapCancelButton)
					}
				}
			}
			.navigationTitle(Strings.Picker.title(Resource.pickableModelName(forCount: viewStore.limit)))
			.toolbar {
				if viewStore.isCancellable {
					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.cancel) {
							viewStore.send(.didTapCancelButton)
						}
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.save) {
						viewStore.send(.didTapSaveButton)
					}
				}
			}
			.navigationBarBackButtonHidden(viewStore.isCancellable)
			.task { await viewStore.send(.didObserveData).finish() }
		})
	}
}
