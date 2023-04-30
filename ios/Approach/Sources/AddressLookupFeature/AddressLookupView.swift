import AddressLookupServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct AddressLookupView: View {
	let store: StoreOf<AddressLookup>

	struct ViewState: Equatable {
		@BindingState var query: String
		let results: IdentifiedArrayOf<AddressLookupResult>
		let isLoading: Bool
		let loadingAddressError: String?
		let loadingResultsError: String?

		init(state: AddressLookup.State) {
			self.query = state.query
			self.results = state.results
			self.isLoading = state.isLoadingAddress
			self.loadingAddressError = state.loadingAddressError
			self.loadingResultsError = state.loadingResultsError
		}
	}

	enum ViewAction: BindableAction {
		case didAppear
		case didDisappear
		case didTapCancelButton
		case didTapResult(AddressLookupResult.ID)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<AddressLookup>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AddressLookup.Action.init) { viewStore in
			List {
				if viewStore.isLoading {
					Section {
						HStack(alignment: .center) {
							ProgressView()
						}
						.frame(maxWidth: .infinity)
					}
					.listRowBackground(Color(uiColor: .secondarySystemBackground))
				} else {
					if let error = viewStore.loadingAddressError ?? viewStore.loadingResultsError {
						Section {
							Banner(
								Strings.Error.Generic.title,
								message: error,
								style: .error
							)
						}
						.listRowInsets(EdgeInsets())
					}

					Section(Strings.List.results) {
						if viewStore.results.isEmpty {
							Text(Strings.Address.Error.Empty.title)
								.font(.caption)
								.multilineTextAlignment(.center)
								.listRowBackground(Color(uiColor: .secondarySystemBackground))
						} else {
							ForEach(viewStore.results) { result in
								Button { viewStore.send(.didTapResult(result.id)) } label: {
									VStack(alignment: .leading, spacing: .tinySpacing) {
										Text(result.completion.wrapped.title)
											.font(.body)
											.foregroundColor(Color(uiColor: .label))
										if !result.completion.wrapped.subtitle.isEmpty {
											Text(result.completion.wrapped.subtitle)
												.font(.caption)
												.foregroundColor(Color(uiColor: .secondaryLabel))
										}
									}
								}
							}
						}
					}
				}
			}
			.searchable(
				text: viewStore.binding(\.$query),
				prompt: Text(Strings.Action.search)
			)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.cancel) { viewStore.send(.didTapCancelButton) }
				}
			}
			.onAppear { viewStore.send(.didAppear) }
			.onDisappear { viewStore.send(.didDisappear) }
		}
	}
}

extension AddressLookup.State {
	var view: AddressLookupView.ViewState {
		get { .init(state: self) }
		set {
			self.query = newValue.query
		}
	}
}

extension AddressLookup.Action {
	init(action: AddressLookupView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case .didDisappear:
			self = .view(.didDisappear)
		case .didTapCancelButton:
			self = .view(.didTapCancelButton)
		case let .didTapResult(id):
			self = .view(.didTapResult(id))
		case let .binding(action):
			self = .binding(action.pullback(\.view))
		}
	}
}
