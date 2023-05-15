import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import GamesEditorFeature
import GamesRepositoryInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary

extension Game.Summary: ResourceListItem {
	public var name: String { Strings.Game.titleWithOrdinal(index + 1) }
}

public struct GamesList: Reducer {
	public struct State: Equatable {
		public let series: Series.Summary
		public var isLoadingGameDetails = false
		public var list: ResourceList<Game.Summary, Series.ID>.State

		public var selection: Identified<Game.ID, GamesEditor.State>?

		public init(series: Series.Summary) {
			self.series = series
			self.list = .init(
				features: [],
				query: series.id,
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
			case bowlerResponse(Game.ID, TaskResult<Bowler.Summary>)
			case list(ResourceList<Game.Summary, Series.ID>.Action)
			case editor(GamesEditor.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.games) var games

	public var body: some Reducer<State, Action> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { series in games.seriesGames(forId: series, ordering: .byIndex) }
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

				case .list(.internal), .list(.view):
					return .none

				case .editor(.internal), .editor(.view):
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

	private func navigate(to id: Game.ID?, bowler: Bowler.Summary?, state: inout State) -> Effect<Action> {
		if let id, let games = state.list.resources, let selection = games[id: id] {
			state.isLoadingGameDetails = true
			if let bowler {
//				state.selection = Identified(
//					.init(
//						bowlers: .init(uniqueElements: [bowler]),
//						bowlerGames: [bowler.id: games.map(\.id)],
//						currentBowler: bowler.id,
//						currentGame: id
//					),
//					id: selection.id
//				)
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
