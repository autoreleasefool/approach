import AssetsLibrary
import LeaguesDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary
import SwiftUI

struct LeaguesFilterView: View {
	let store: StoreOf<LeaguesFilter>

	struct ViewState: Equatable {
		@BindableState var recurrence: League.Recurrence?

		init(state: LeaguesFilter.State) {
			self.recurrence = state.recurrence
		}
	}

	enum ViewAction: BindableAction {
		case clearFiltersButtonTapped
		case applyButtonTapped
		case binding(BindingAction<ViewState>)
	}

	init(store: StoreOf<LeaguesFilter>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: LeaguesFilter.Action.init) { viewStore in
			List {
				Section {
					Picker(
						Strings.League.Properties.recurrence,
						selection: viewStore.binding(\.$recurrence)
					) {
						Text("").tag(nil as League.Recurrence?)
						ForEach(League.Recurrence.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				Section {
					Button(Strings.Action.reset) { viewStore.send(.clearFiltersButtonTapped) }
						.tint(.appDestructive)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.scrollContentBackground(.hidden)
			.navigationTitle(Strings.Action.filter)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.applyButtonTapped) }
				}
			}
		}
	}
}

extension LeaguesFilter.State {
	var view: LeaguesFilterView.ViewState {
		get { .init(state: self) }
		set {
			self.recurrence = newValue.recurrence
		}
	}
}

extension LeaguesFilter.Action {
	init(action: LeaguesFilterView.ViewAction) {
		switch action {
		case .clearFiltersButtonTapped:
			self = .clearFiltersButtonTapped
		case .applyButtonTapped:
			self = .applyButtonTapped
		case let .binding(action):
			self = .binding(action.pullback(\LeaguesFilter.State.view))
		}
	}
}
