import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import GamesListFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SeriesRepositoryInterface
import StringsLibrary
import ViewsLibrary

extension Series.Summary: ResourceListItem {
	public var name: String { date.longFormat }
}

public struct SeriesList: Reducer {
	public struct State: Equatable {
		public let league: League.SeriesHost

		public var list: ResourceList<Series.Summary, League.ID>.State
		public var selection: Identified<Series.ID, GamesList.State>?
		@PresentationState public var editor: SeriesEditor.State?

		public init(league: League.SeriesHost) {
			self.league = league
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.series) var series: SeriesRepository
						try await series.delete($0.id)
					}),
				],
				query: league.id,
				listTitle: Strings.Series.List.title,
				emptyContent: .init(
					image: .emptySeries,
					title: Strings.Series.Error.Empty.title,
					message: Strings.Series.Error.Empty.message,
					action: Strings.Series.List.add
				))
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setNavigation(selection: Series.ID?)
		}

		public enum InternalAction: Equatable {
			case didLoadEditableSeries(Series.EditWithLanes)
			case list(ResourceList<Series.Summary, League.ID>.Action)
			case editor(PresentationAction<SeriesEditor.Action>)
			case sidebar(GamesList.Action)
		}

		public enum DelegateAction: Equatable {}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.date) var date
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList {
				series.list(bowledIn: $0, ordering: .byDate)
			}
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
				case let .didLoadEditableSeries(series):
					return startEditing(series: series, state: &state)

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(series):
						return .run { send in
							guard let editable = try await self.series.edit(series.id) else {
								// TODO: report series not found
								return
							}

							await send(.internal(.didLoadEditableSeries(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						return startEditing(series: nil, state: &state)

					case .didDelete, .didTap:
						return .none
					}

				case let .editor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .didFinishEditing:
						state.editor = nil
						return .none
					}

				case let .sidebar(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .sidebar(.internal), .sidebar(.view):
					return .none

				case .list(.view), .list(.internal):
					return .none

				case .editor(.presented(.view)), .editor(.presented(.internal)), .editor(.presented(.binding)), .editor(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.selection, action: /Action.internal..Action.InternalAction.sidebar) {
			Scope(state: \Identified<Series.ID, GamesList.State>.value, action: /.self) {
				GamesList()
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			SeriesEditor()
		}
	}

	private func navigate(to id: Series.ID?, state: inout State) -> Effect<Action> {
		if let id, let selection = state.list.resources?[id: id] {
			state.selection = Identified(.init(series: selection), id: selection.id)
			return .none
		} else {
			state.selection = nil
			return .none
		}
	}

	private func startEditing(series: Series.EditWithLanes?, state: inout State) -> Effect<Action> {
		if let series {
			state.editor = .init(value: .edit(series), inLeague: state.league)
		} else {
			state.editor = .init(
				value: .create(.default(withId: uuid(), onDate: date(), inLeague: state.league)),
				inLeague: state.league
			)
		}

		return .none
	}
}

extension ListErrorContent {
	static let createError = Self(
		title: Strings.Series.Error.FailedToCreate.title,
		message: Strings.Series.Error.FailedToCreate.message,
		action: Strings.Action.tryAgain
	)
}
