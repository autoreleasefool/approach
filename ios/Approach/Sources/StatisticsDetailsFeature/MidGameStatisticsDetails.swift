import AnalyticsServiceInterface
import ComposableArchitecture
import ComposableExtensionsLibrary
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct MidGameStatisticsDetails: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var filter: TrackableFilter
		public let games: IdentifiedArrayOf<Game.Indexed>
		public let seriesId: Series.ID
		public var selectedGame: Game.ID?

		public var errors: Errors<ErrorID>.State = .init()

		public var list: StatisticsDetailsList.State = .init(listEntries: [], hasTappableElements: false)

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

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didFirstAppear
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didLoadListEntries(Result<[Statistics.ListEntryGroup], Error>)

			case list(StatisticsDetailsList.Action)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum CancelID: Sendable {
		case loadingStaticValues
	}

	public enum ErrorID: Hashable {
		case failedToLoadList
	}

	public init() {}

	@Dependency(StatisticsRepository.self) var statistics

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.list, action: \.internal.list) {
			StatisticsDetailsList()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .task:
					return .cancelling(id: CancelID.loadingStaticValues)

				case .didFirstAppear:
					return refreshStatistics(state: state)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadListEntries(.success(statistics)):
					state.list.listEntries = .init(uniqueElements: statistics)
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

				case .errors(.delegate(.doNothing)):
					return .none

				case .errors(.internal), .errors(.view),
						.list(.internal), .list(.view), .list(.binding):
					return .none
				}

			case .binding(\.selectedGame):
				if let game = state.selectedGame {
					state.filter.source = .game(game)
				} else {
					state.filter.source = .series(state.seriesId)
				}
				return refreshStatistics(state: state)

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadListEntries(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}
