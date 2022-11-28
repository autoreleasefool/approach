import ComposableArchitecture
import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct AlleyPickerView: View {
	let store: StoreOf<AlleyPicker>

	struct ViewState: Equatable {
		let listState: ListContentState<Alley, ListErrorContent>

		init(state: AlleyPicker.State) {
			if let error = state.error {
				self.listState = .error(error)
			} else if let alleys = state.alleys {
				self.listState = .loaded(alleys)
			} else {
				self.listState = .loading
			}
		}
	}

	enum ViewAction {
		case subscribeToAlleys
		case saveButtonTapped
		case errorButtonTapped
		case dismissButtonTapped
		case alleyTapped(Alley)
	}

	public init(store: StoreOf<AlleyPicker>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyPicker.Action.init) { viewStore in
			ListContent(viewStore.listState) { alleys in
				ForEach(alleys) { alley in
					VStack(alignment: .leading, spacing: .smallSpacing) {
						Text(alley.name)
						if let address = alley.address {
							Text(address)
								.font(.caption)
						}
					}
				}
			} empty: {
				ListEmptyContent(
					.emptyAlleys,
					title: "No alleys found"
				) {
					EmptyContentAction(title: "Dismiss") {
						viewStore.send(.dismissButtonTapped)
					}
				}
			} error: { error in
				ListEmptyContent(
					.errorNotFound,
					title: error.title,
					message: error.message,
					style: .error
				) {
					EmptyContentAction(title: "Dismiss") {
						viewStore.send(.errorButtonTapped)
					}
				}
			}
		}
	}
}

extension AlleyPicker.Action {
	init(action: AlleyPickerView.ViewAction) {
		switch action {
		case .subscribeToAlleys:
			self = .subscribeToAlleys
		case .saveButtonTapped:
			self = .saveButtonTapped
		case .errorButtonTapped:
			self = .errorButtonTapped
		case .dismissButtonTapped:
			self = .dismissButtonTapped
		case let .alleyTapped(alley):
			self = .alleyTapped(alley)
		}
	}
}
