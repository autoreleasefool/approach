import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI

public struct MidGameStatisticsDetails: Reducer {
	public struct State: Equatable {
		public var filter: TrackableFilter
		public let games: IdentifiedArrayOf<Game.Indexed>
		public let seriesId: Series.ID
		@BindingState public var selectedGame: Game.ID?

		public var listEntries: IdentifiedArrayOf<Statistics.ListEntryGroup> = []

		public var errors: Errors<ErrorID>.State = .init()

		public var _list: StatisticsDetailsList.State = .init(listEntries: [], hasTappableElements: false)

		public init(filter: TrackableFilter, seriesId: Series.ID, games: IdentifiedArrayOf<Game.Indexed>) {
			self.filter = filter
			self.seriesId = seriesId
			self.games = games

			switch filter.source {
			case .bowler, .league, .series:
				self.selectedGame = nil
			case let .game(id):
				self.selectedGame = id
			}
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didFirstAppear
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadListEntries(TaskResult<[Statistics.ListEntryGroup]>)

			case list(StatisticsDetailsList.Action)
			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum CancelID {
		case loadingStaticValues
	}

	public enum ErrorID: Hashable {
		case failedToLoadList
	}

	public init() {}

	@Dependency(\.statistics) var statistics

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			StatisticsDetailsList()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return refreshStatistics(state: state)

				case .binding(\.$selectedGame):
					if let game = state.selectedGame {
						state.filter.source = .game(game)
					} else {
						state.filter.source = .series(state.seriesId)
					}
					return refreshStatistics(state: state)

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadListEntries(.success(statistics)):
					state.listEntries = .init(uniqueElements: statistics)
					return .none

				case let .didLoadListEntries(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadList, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case .didRequestEntryDetails:
						return .none

					case .listRequiresReload:
						return refreshStatistics(state: state)
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .errors(.internal), .errors(.view),
						.list(.internal), .list(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func refreshStatistics(state: State) -> Effect<Action> {
		.run { [filter = state.filter] send in
			await send(.internal(.didLoadListEntries(TaskResult {
				try await statistics.load(for: filter)
			})))
		}
		.cancellable(id: CancelID.loadingStaticValues, cancelInFlight: true)
	}
}

extension MidGameStatisticsDetails.State {
	var list: StatisticsDetailsList.State {
		get {
			var list = _list
			list.listEntries = listEntries
			return list
		}
		set {
			_list = newValue
		}
	}
}
