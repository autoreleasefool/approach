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

		@PresentationState public var editor: GamesEditor.State?

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
			case didTapGame(Game.ID)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case list(ResourceList<Game.List, Series.ID>.Action)
			case editor(PresentationAction<GamesEditor.Action>)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.games) var games

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { series in games.seriesGames(forId: series, ordering: .byIndex) }
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapGame(id):
					if let game = state.list.resources?[id: id] {
						state.editor = .init(bowlerIds: [game.bowlerId], bowlerGameIds: [game.bowlerId: [id]])
					}
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

				case .list(.internal), .list(.view):
					return .none

				case .editor(.presented(.internal)), .editor(.presented(.view)), .editor(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			GamesEditor()
		}
	}
}
