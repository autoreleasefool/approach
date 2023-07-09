import AssetsLibrary
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import ViewsLibrary

public struct OpponentDetails: Reducer {
	public struct State: Equatable {
		public let opponent: Bowler.Summary
		public var opponentDetails: Bowler.OpponentDetails?

		public init(opponent: Bowler.Summary) {
			self.opponent = opponent
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadDetails(TaskResult<Bowler.OpponentDetails?>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum CancelID { case details }

	public init() {}

	@Dependency(\.bowlers) var bowlers

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return refreshDetails(forOpponent: state.opponent.id)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadDetails(.success(details)):
					state.opponentDetails = details
					return.none

				case .didLoadDetails(.failure):
					// TODO: handle error loading opponent details
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func refreshDetails(forOpponent: Bowler.ID) -> Effect<Action> {
		.run { send in
			await send(.internal(.didLoadDetails(TaskResult {
				try await bowlers.record(againstOpponent: forOpponent)
			})))
		}
		.cancellable(id: CancelID.details, cancelInFlight: true)
	}
}
