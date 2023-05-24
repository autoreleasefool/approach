import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
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
			case list(ResourceList<Game.List, Series.ID>.Action)
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

	private func navigate(to id: Game.ID?, state: inout State) -> Effect<Action> {
		if let id, let games = state.list.resources, let selection = games[id: id] {
			state.selection = Identified(
				.init(bowlerIds: [selection.bowlerId], bowlerGameIds: [selection.bowlerId: [id]]),
				id: id
			)
		} else {
			state.selection = nil
		}

		return .none
	}
}

struct BowlerNotFoundError: Error {}
