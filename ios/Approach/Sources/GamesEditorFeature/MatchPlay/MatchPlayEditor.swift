import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import MatchPlaysRepositoryInterface
import ModelsLibrary
import ModelsViewsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

@Reducer
public struct MatchPlayEditor: Reducer {
	public struct State: Equatable {
		public var matchPlay: MatchPlay.Edit

		@PresentationState public var opponentPicker: ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.State?

		init(matchPlay: MatchPlay.Edit) {
			self.matchPlay = matchPlay
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction {
			case onAppear
			case didTapOpponent
			case didSetScore(String)
			case didSetResult(MatchPlay.Result?)
			case didTapDeleteButton
		}
		@CasePathable public enum DelegateAction {
			case didEditMatchPlay(MatchPlay.Edit?)
		}
		@CasePathable public enum InternalAction {
			case opponentPicker(PresentationAction<ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case savingScore }

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapOpponent:
					state.opponentPicker = .init(
						selected: Set([state.matchPlay.opponent?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none

				case let .didSetScore(value):
					if let score = Int(value) {
						state.matchPlay.opponentScore = min(max(score, 0), Game.MAXIMUM_SCORE)
					} else {
						state.matchPlay.opponentScore = nil
					}

					return .run { [matchPlay = state.matchPlay] send in
						try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
						await send(.delegate(.didEditMatchPlay(matchPlay)))
					}
					.cancellable(id: CancelID.savingScore, cancelInFlight: true)

				case let .didSetResult(result):
					state.matchPlay.result = result
					return .send(.delegate(.didEditMatchPlay(state.matchPlay)))

				case .didTapDeleteButton:
					return .concatenate(
						.send(.delegate(.didEditMatchPlay(nil))),
						.run { _ in await dismiss() }
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .opponentPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(opponents):
						state.matchPlay.opponent = opponents.first?.summary
						return .send(.delegate(.didEditMatchPlay(state.matchPlay)))
					}

				case .opponentPicker(.dismiss),
						.opponentPicker(.presented(.internal)), .opponentPicker(.presented(.view)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$opponentPicker, action: /Action.internal..Action.InternalAction.opponentPicker) {
			ResourcePicker { _ in bowlers.opponents(ordering: .byName) }
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

public struct MatchPlayEditorView: View {
	let store: StoreOf<MatchPlayEditor>

	struct ViewState: Equatable {
		let matchPlay: MatchPlay.Edit

		init(state: MatchPlayEditor.State) {
			self.matchPlay = state.matchPlay
		}
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			Form {
				Section(Strings.MatchPlay.Editor.Fields.Opponent.title) {
					NavigationButton { viewStore.send(.didTapOpponent) } content: {
						LabeledContent(
							Strings.MatchPlay.Editor.Fields.Opponent.title,
							value: viewStore.matchPlay.opponent?.name ?? Strings.none
						)
					}

					TextField(
						Strings.MatchPlay.Editor.Fields.Opponent.score,
						text: viewStore.binding(
							get: {
								if let score = $0.matchPlay.opponentScore, score > 0 {
									return String(score)
								} else {
									return ""
								}
							},
							send: { .didSetScore($0) }
						)
					)
					.keyboardType(.numberPad)
				}

				Section {
					Picker(
						Strings.MatchPlay.Editor.Fields.Result.title,
						selection: viewStore.binding(get: { $0.matchPlay.result }, send: { .didSetResult($0) })
					) {
						Text("").tag(nil as MatchPlay.Result?)
						ForEach(MatchPlay.Result.allCases) {
							Text(String(describing: $0)).tag(Optional($0))
						}
					}
				} footer: {
					Text(Strings.MatchPlay.Editor.Fields.Result.footer(String(describing: MatchPlay.Result.won)))
				}

				Section {
					DeleteButton { viewStore.send(.didTapDeleteButton) }
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.navigationTitle(Strings.MatchPlay.title)
		.navigationDestination(
			store: store.scope(state: \.$opponentPicker, action: { .internal(.opponentPicker($0)) })
		) { store in
			ResourcePickerView(store: store) {
				Bowler.View($0)
			}
		}
	}
}

extension MatchPlay.Result: CustomStringConvertible {
	public var description: String {
		switch self {
		case .lost: return Strings.MatchPlay.Properties.Result.lost
		case .tied: return Strings.MatchPlay.Properties.Result.tied
		case .won: return Strings.MatchPlay.Properties.Result.won
		}
	}
}
