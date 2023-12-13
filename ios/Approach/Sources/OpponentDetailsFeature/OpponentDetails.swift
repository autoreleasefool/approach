import AnalyticsServiceInterface
import AssetsLibrary
import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import ViewsLibrary

@Reducer
public struct OpponentDetails: Reducer {
	public struct State: Equatable {
		public let opponent: Bowler.Summary
		public var opponentDetails: Bowler.OpponentDetails?

		public var errors: Errors<ErrorID>.State = .init()

		public init(opponent: Bowler.Summary) {
			self.opponent = opponent
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction {
			case onAppear
			case didFirstAppear
		}
		@CasePathable public enum DelegateAction { case doNothing }
		@CasePathable public enum InternalAction {
			case didLoadDetails(Result<Bowler.OpponentDetails, Error>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum CancelID { case details }
	public enum ErrorID { case failedToLoadDetails }

	public init() {}

	@Dependency(\.bowlers) var bowlers

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return refreshDetails(forOpponent: state.opponent.id)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadDetails(.success(details)):
					state.opponentDetails = details
					return.none

				case let .didLoadDetails(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadDetails, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)):
					return .none

				case .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}

	private func refreshDetails(forOpponent: Bowler.ID) -> Effect<Action> {
		.run { send in
			await send(.internal(.didLoadDetails(Result {
				try await bowlers.record(againstOpponent: forOpponent)
			})))
		}
		.cancellable(id: CancelID.details, cancelInFlight: true)
	}
}
