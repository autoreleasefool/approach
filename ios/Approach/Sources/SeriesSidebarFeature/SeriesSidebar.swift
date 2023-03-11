import ComposableArchitecture
import FeatureActionLibrary
import GamesDataProviderInterface
import GamesEditorFeature
import ResourceListLibrary
import SharedModelsLibrary
import StringsLibrary

extension Game: ResourceListItem {
	public var name: String { "Game \(ordinal)" }
}

public struct SeriesSidebar: ReducerProtocol {
	public struct State: Equatable {
		public let series: Series
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
			case list(ResourceList<Game, Game.FetchRequest>.Action)
			case editor(GamesEditor.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.gamesDataProvider) var gamesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: gamesDataProvider.observeGames)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, state: &state)
				}

			case let .internal(internalAction):
				switch internalAction {
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

	private func navigate(to id: Game.ID?, state: inout State) -> EffectTask<Action> {
		if let id, let selection = state.list.resources?[id: id] {
			state.selection = Identified(.init(games: state.list.resources ?? [], selected: id), id: selection.id)
		} else {
			state.selection = nil
		}

		return .none
	}
}
