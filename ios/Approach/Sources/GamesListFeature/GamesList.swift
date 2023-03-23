import BowlersDataProviderInterface
import ComposableArchitecture
import FeatureActionLibrary
import GamesDataProviderInterface
import GamesEditorFeature
import ResourceListLibrary
import SharedModelsFetchableLibrary
import SharedModelsLibrary
import StringsLibrary

extension Game: ResourceListItem {
	public var name: String { Strings.Game.title(ordinal) }
}

public struct GamesList: Reducer {
	public struct State: Equatable {
		public let series: Series
		public var isLoadingGameDetails = false
		public var list: ResourceList<Game, Game.FetchRequest>.State

		public var selection: Identified<Game.ID, GamesEditor.State>?

		public init(series: Series) {
			self.series = series
			self.list = .init(
				features: [],
				query: .init(filter: .series(series), ordering: .byOrdinal),
				listTitle: series.date.longFormat,
				emptyContent: .init(
					image: .emptyGames,
					title: Strings.Error.Generic.title,
					action: Strings.Action.reload
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setNavigation(selection: Game.ID?)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case bowlerResponse(Game.ID, TaskResult<Bowler>)
			case list(ResourceList<Game, Game.FetchRequest>.Action)
			case editor(GamesEditor.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.gamesDataProvider) var gamesDataProvider
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some Reducer<State, Action> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: gamesDataProvider.observeGames)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, bowler: nil, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, bowler: nil, state: &state)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .bowlerResponse(id, .success(bowler)):
					return navigate(to: id, bowler: bowler, state: &state)

				case .bowlerResponse(_, .failure):
					// TODO: handle failure to load bowler
					return navigate(to: nil, bowler: nil, state: &state)

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case .didEdit, .didDelete, .didTap, .didAddNew, .didTapEmptyStateButton:
						return .none
					}

				case let .editor(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view), .list(.callback):
					return .none

				case .editor(.internal), .editor(.view), .editor(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.selection, action: /Action.internal..Action.InternalAction.editor) {
			Scope(state: \Identified<Game.ID, GamesEditor.State>.value, action: /.self) {
				GamesEditor()
			}
		}
	}

	private func navigate(to id: Game.ID?, bowler: Bowler?, state: inout State) -> EffectTask<Action> {
		if let id, let games = state.list.resources, let selection = games[id: id] {
			state.isLoadingGameDetails = true
			if let bowler {
				state.selection = Identified(
					.init(
						bowlers: .init(uniqueElements: [bowler]),
						bowlerGames: [bowler.id: games.map(\.id)],
						currentBowler: bowler.id,
						currentGame: id
					),
					id: selection.id
				)
			} else {
				// TODO: need to fetch bowlers for games
				return .none
//				return .task {
//					await .internal(.bowlerResponse(id, TaskResult {
//						guard let bowler = try await bowlersDataProvider.fetchBowlers(
//							.init(filter: .forGame(selection), ordering: .byName)
//						).first else {
//							throw BowlerNotFoundError()
//						}
//						return bowler
//					}))
//				}
			}
		} else {
			state.isLoadingGameDetails = false
			state.selection = nil
		}

		return .none
	}
}

struct BowlerNotFoundError: Error {}
