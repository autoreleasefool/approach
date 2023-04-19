import AssetsLibrary
import ComposableArchitecture
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

	enum ViewAction {
		case didObserveData
		case didTapSaveButton
		case didTapCancelButton
		case didTapResource(Resource)
	}

	public init(store: StoreOf<ResourcePicker<Resource, Query>>, @ViewBuilder row: @escaping (Resource) -> Row) {
		self.store = store
		self.row = row
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: map(viewAction:)) { viewStore in
			ListContent(viewStore.listState) { resources in
				ForEach(resources) { resource in
					Button {
						viewStore.send(.didTapResource(resource))
					} label: {
						HStack(alignment: .center, spacing: .standardSpacing) {
							Image(systemName: viewStore.selected.contains(resource.id) ? "checkmark.circle.fill" : "circle")
								.resizable()
								.frame(width: .smallIcon, height: .smallIcon)
								.foregroundColor(.appAction)
							row(resource)
								.frame(maxWidth: .infinity, alignment: .leading)
						}
						.frame(maxWidth: .infinity)
						.contentShape(Rectangle())
					}
					.buttonStyle(TappableElement())
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			} empty: {
				ListEmptyContent(
					.emptyAlleys, // TODO: empty picker image
					title: Strings.Picker.Empty.title
				) {
					EmptyContentAction(title: Strings.Action.cancel) {
						viewStore.send(.didTapCancelButton)
					}
				}
			} error: { error in
				ListEmptyContent(
					.errorNotFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: error.action) {
						viewStore.send(.didTapCancelButton)
					}
				}
			}
			.scrollContentBackground(.hidden)
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
		}
	}

	private func map(viewAction: ViewAction) -> ResourcePicker<Resource, Query>.Action {
		switch viewAction {
		case .didObserveData:
			return .view(.didObserveData)
		case .didTapSaveButton:
			return .view(.didTapSaveButton)
		case .didTapCancelButton:
			return .view(.didTapCancelButton)
		case let .didTapResource(resource):
			return .view(.didTapResource(resource))
		}
	}
}
