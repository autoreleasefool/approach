import ComposableArchitecture
import StringsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct ResourcePickerView<Resource: PickableResource>: View {
	let store: StoreOf<ResourcePicker<Resource>>

	struct ViewState: Equatable {
		let listState: ListContentState<Resource, ListErrorContent>
		let selected: Set<Resource.ID>
		let isCancellable: Bool

		init(state: ResourcePicker<Resource>.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let resources = state.resources {
				self.listState = .loaded(resources)
			} else {
				self.listState = .loading
			}
			self.selected = state.selected
			self.isCancellable = state.selected != state.initialSelection
		}
	}

	enum ViewAction {
		case subscribeToResources
		case saveButtonTapped
		case cancelButtonTapped
		case resourceTapped(Resource)
	}

	public init(store: StoreOf<ResourcePicker<Resource>>) {
		self.store = store
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

							VStack(alignment: .leading, spacing: .smallSpacing) {
								Text(resource.pickableTitle)
									.frame(maxWidth: .infinity, alignment: .leading)
								if let subtitle = resource.pickableSubtitle {
									Text(subtitle)
										.frame(maxWidth: .infinity, alignment: .leading)
										.font(.caption)
								}
							}
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
					EmptyContentAction(title: Strings.Picker.Empty.cancel) {
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
			.navigationTitle(Strings.Picker.title(Resource.pickableModelName))
			.toolbar {
				if viewStore.isCancellable {
					ToolbarItem(placement: .navigationBarLeading) {
						Button("Cancel") {
							viewStore.send(.cancelButtonTapped)
						}
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button("Save") {
						viewStore.send(.saveButtonTapped)
					}
				}
			}
			.task { await viewStore.send(.subscribeToResources).finish() }
		}
	}

	private func map(viewAction: ViewAction) -> ResourcePicker<Resource>.Action {
		switch viewAction {
		case .saveButtonTapped:
			return .saveButtonTapped
		case .cancelButtonTapped:
			return .cancelButtonTapped
		case .subscribeToResources:
			return .subscribeToResources
		case let .resourceTapped(resource):
			return .resourceTapped(resource)
		}
	}
}
