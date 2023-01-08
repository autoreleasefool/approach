import ComposableArchitecture
import StringsLibrary
import SwiftUI
import AssetsLibrary
import ViewsLibrary

public struct ResourcePickerView<Resource: PickableResource, Row: View>: View {
	let store: StoreOf<ResourcePicker<Resource>>
	let row: (Resource) -> Row

	struct ViewState: Equatable {
		let listState: ListContentState<Resource, ListErrorContent>
		let selected: Set<Resource.ID>
		let isCancellable: Bool
		let limit: Int

		init(state: ResourcePicker<Resource>.State) {
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
		case refreshData
		case saveButtonTapped
		case cancelButtonTapped
		case resourceTapped(Resource)
	}

	public init(store: StoreOf<ResourcePicker<Resource>>, @ViewBuilder row: @escaping (Resource) -> Row) {
		self.store = store
		self.row = row
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: map(viewAction:)) { viewStore in
			ListContent(viewStore.listState) { resources in
				ForEach(resources) { resource in
					Button {
						viewStore.send(.resourceTapped(resource))
					} label: {
						HStack(alignment: .center, spacing: .standardSpacing) {
							Image(systemName: viewStore.selected.contains(resource.id) ? "checkmark.circle.fill" : "circle")
								.resizable()
								.frame(width: .smallIcon, height: .smallIcon)
								.foregroundColor(.appAction)
							row(resource)
								.frame(maxWidth: .infinity)
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
						viewStore.send(.cancelButtonTapped)
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
						viewStore.send(.cancelButtonTapped)
					}
				}
			}
			.scrollContentBackground(.hidden)
			.navigationTitle(Strings.Picker.title(Resource.pickableModelName(forCount: viewStore.limit)))
			.toolbar {
				if viewStore.isCancellable {
					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.cancel) {
							viewStore.send(.cancelButtonTapped)
						}
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.save) {
						viewStore.send(.saveButtonTapped)
					}
				}
			}
			.navigationBarBackButtonHidden(viewStore.isCancellable)
			.onAppear { viewStore.send(.refreshData) }
		}
	}

	private func map(viewAction: ViewAction) -> ResourcePicker<Resource>.Action {
		switch viewAction {
		case .saveButtonTapped:
			return .saveButtonTapped
		case .cancelButtonTapped:
			return .cancelButtonTapped
		case .refreshData:
			return .refreshData
		case let .resourceTapped(resource):
			return .resourceTapped(resource)
		}
	}
}
