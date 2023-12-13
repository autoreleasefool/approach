import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

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

	public init(store: StoreOf<OpponentDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				if let details = viewStore.details {
					Section(Strings.Opponent.record) {
						LabeledContent(Strings.Opponent.Record.matchesPlayed, value: String(details.gamesPlayed))
						LabeledContent(Strings.Opponent.Record.matchesWon, value: String(details.gamesWon))
						LabeledContent(Strings.Opponent.Record.matchesLost, value: String(details.gamesLost))
						LabeledContent(Strings.Opponent.Record.matchesTied, value: String(details.gamesTied))
					}

					Section(Strings.Opponent.matches) {
						if details.matchesAgainst.isEmpty {
							Text(Strings.Opponent.Matches.none)
						} else {
							ForEach(details.matchesAgainst) {
								LabeledContent(String($0.score), value: String($0.opponentScore ?? 0))
									.listRowBackground($0.result.listBackgroundColor)
							}
						}
					}
				} else {
					ListProgressView()
				}
			}
			.navigationTitle(viewStore.opponentName)
			.onFirstAppear { viewStore.send(.didFirstAppear) }
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
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
