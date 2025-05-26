import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import MatchPlaysRepositoryInterface
import ModelsLibrary
import ModelsViewsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct MatchPlayEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var matchPlay: MatchPlay.Edit

		var score: String {
			if let score = matchPlay.opponentScore, score > 0 {
				String(score)
			} else {
				""
			}
		}

		@Presents public var opponentPicker: ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.State?

		init(matchPlay: MatchPlay.Edit) {
			self.matchPlay = matchPlay
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapOpponent
			case didSetScore(String)
			case didSetResult(MatchPlay.Result?)
			case didTapDeleteButton
		}
		@CasePathable
		public enum Delegate {
			case didEditMatchPlay(MatchPlay.Edit?)
		}
		@CasePathable
		public enum Internal {
			case opponentPicker(PresentationAction<ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	enum CancelID: Sendable { case savingScore }

	@Dependency(BowlersRepository.self) var bowlers
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
		.ifLet(\.$opponentPicker, action: \.internal.opponentPicker) {
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

@ViewAction(for: MatchPlayEditor.self)
public struct MatchPlayEditorView: View {
	@Bindable public var store: StoreOf<MatchPlayEditor>

	public var body: some View {
		Form {
			Section(Strings.MatchPlay.Editor.Fields.Opponent.title) {
				NavigationButton { send(.didTapOpponent) } content: {
					LabeledContent(
						Strings.MatchPlay.Editor.Fields.Opponent.title,
						value: store.matchPlay.opponent?.name ?? Strings.none
					)
				}

				TextField(
					Strings.MatchPlay.Editor.Fields.Opponent.score,
					text: $store.score.sending(\.view.didSetScore)
				)
				.keyboardType(.numberPad)
			}

			Section {
				Picker(
					Strings.MatchPlay.Editor.Fields.Result.title,
					selection: $store.matchPlay.result.sending(\.view.didSetResult)
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
				DeleteButton { send(.didTapDeleteButton) }
			}
		}
		.onAppear { send(.onAppear) }
		.navigationTitle(Strings.MatchPlay.title)
		.navigationDestination(
			item: $store.scope(state: \.opponentPicker, action: \.internal.opponentPicker)
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
		case .lost: Strings.MatchPlay.Properties.Result.lost
		case .tied: Strings.MatchPlay.Properties.Result.tied
		case .won: Strings.MatchPlay.Properties.Result.won
		}
	}
}
