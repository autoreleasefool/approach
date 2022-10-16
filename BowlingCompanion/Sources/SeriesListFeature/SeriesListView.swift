import ComposableArchitecture
import DateTimeLibrary
import SharedModelsLibrary
import SwiftUI

public struct SeriesListView: View {
	let store: StoreOf<SeriesList>

	struct ViewState: Equatable {
		let series: IdentifiedArrayOf<Series>
		let leagueName: String

		init(state: SeriesList.State) {
			self.series = state.series
			self.leagueName = state.league.name
		}
	}

	enum ViewAction {
		case onAppear
		case onDisappear
		case setFormSheet(isPresented: Bool)
	}

	public init(store: StoreOf<SeriesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesList.Action.init) { viewStore in
			List(viewStore.series) {
				Text($0.date.regularDateFormat)
			}
			.navigationTitle(viewStore.leagueName)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button {
						viewStore.send(.setFormSheet(isPresented: true))
					} label: {
						Image(systemName: "plus")
					}
				}
			}
			.onAppear { viewStore.send(.onAppear) }
			.onDisappear { viewStore.send(.onDisappear) }
		}
	}
}

extension SeriesList.Action {
	init(action: SeriesListView.ViewAction) {
		switch action {
		case .onAppear:
			self = .onAppear
		case .onDisappear:
			self = .onDisappear
		case let .setFormSheet(isPresented):
			self = .setFormSheet(isPresented: isPresented)
		}
	}
}
