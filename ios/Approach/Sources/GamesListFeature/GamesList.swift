import AssetsLibrary
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import GamesEditorFeature
import GamesRepositoryInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary

extension Game.List: ResourceListItem {
	public var name: String { Strings.Game.titleWithOrdinal(index + 1) }
}

public struct GamesList: Reducer {
	public struct State: Equatable {
		public let series: Series.Summary
		public var list: ResourceList<Game.List, Series.ID>.State

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var editor: GamesEditor.State?

		public init(series: Series.Summary) {
			self.series = series
			self.list = .init(
				features: [],
				query: series.id,
				listTitle: nil,
				emptyContent: .init(
					image: Asset.Media.EmptyState.games,
					title: Strings.Error.Generic.title,
					action: Strings.Action.reload
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapGame(Game.ID)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case errors(Errors<ErrorID>.Action)
			case list(ResourceList<Game.List, Series.ID>.Action)
			case editor(PresentationAction<GamesEditor.Action>)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum ErrorID: Hashable {
		case gamesNotFound
		case gameNotFound
	}

	public init() {}

	@Dependency(\.games) var games
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { series in games.seriesGames(forId: series, ordering: .byIndex) }
		}

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapGame(id):
					guard let games = state.list.resources else {
						return state.errors
							.enqueue(
								.gamesNotFound,
								thrownError: GamesListError.gamesNotFound,
								toastMessage: Strings.Error.Toast.dataNotFound
							)
							.map { .internal(.errors($0)) }
					}

					guard let game = games[id: id] else {
						return state.errors
							.enqueue(
								.gameNotFound,
								thrownError: GamesListError.gameNotFound(id),
								toastMessage: Strings.Error.Toast.dataNotFound
							)
							.map { .internal(.errors($0)) }
					}

					state.editor = .init(
						bowlerIds: [game.bowlerId],
						bowlerGameIds: [game.bowlerId: games.map(\.id)],
						initialBowlerId: game.bowlerId,
						initialGameId: id
					)

					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case .didEdit, .didDelete, .didTap, .didAddNew, .didTapEmptyStateButton:
						return .none
					}

				case let .editor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view),
						.editor(.presented(.internal)), .editor(.presented(.view)), .editor(.dismiss),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			GamesEditor()
				.dependency(\.analyticsGameSessionId, uuid())
		}
	}
}

public enum GamesListError: LocalizedError {
	case gameNotFound(Game.ID)
	case gamesNotFound

	public var errorDescription: String? {
		switch self {
		case let .gameNotFound(id):
			return "Could not find Game with ID '\(id)'"
		case .gamesNotFound:
			return "Games were not loaded in List"
		}
	}
}
