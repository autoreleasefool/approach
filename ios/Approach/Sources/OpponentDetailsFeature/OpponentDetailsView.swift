import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct OpponentDetailsView: View {
	let store: StoreOf<OpponentDetails>

	struct ViewState: Equatable {
		let opponentName: String
		let details: Bowler.OpponentDetails?

		init(state: OpponentDetails.State) {
			self.opponentName = state.opponent.name
			self.details = state.opponentDetails
		}
	}

	enum ViewAction {
		case onAppear
	}

	public init(store: StoreOf<OpponentDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: OpponentDetails.Action.init) { viewStore in
			List {
//				Section(Strings.Opponent.record) {
//
//				}

				if let details = viewStore.details {
					Section(Strings.Opponent.matches) {
						ForEach(details.matchesAgainst) {
							LabeledContent(String($0.score), value: String($0.opponentScore ?? 0))
								.listRowBackground($0.result.listBackgroundColor)
						}
					}
				}
			}
			.navigationTitle(viewStore.opponentName)
			.onAppear { viewStore.send(.onAppear) }
		}
	}
}

extension OpponentDetails.Action {
	init(action: OpponentDetailsView.ViewAction) {
		switch action {
		case .onAppear:
			self = .view(.onAppear)
		}
	}
}

extension Optional where Wrapped == MatchPlay.Result {
	var listBackgroundColor: Color? {
		switch self {
		case .won:
			return Asset.Colors.Success.default.swiftUIColor
		case .lost:
			return Asset.Colors.Error.default.swiftUIColor
		case .tied:
			return Asset.Colors.Warning.default.swiftUIColor
		case .none:
			return nil
		}
	}
}
