import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import PersistenceServiceInterface
import ResourceListLibrary
import SeriesDataProviderInterface
import SeriesEditorFeature
import SeriesSidebarFeature
import SharedModelsLibrary
import StringsLibrary
import ViewsLibrary

extension Series: ResourceListItem {
	public var name: String { date.longFormat }
}

public struct SeriesList: ReducerProtocol {
	public struct State: Equatable {
		public let league: League

		public var list: ResourceList<Series, Series.FetchRequest>.State
		public var editor: SeriesEditor.State?
		public var selection: Identified<Series.ID, SeriesSidebar.State>?

		public init(league: League) {
			self.league = league
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.persistenceService) var persistenceService: PersistenceService
						try await persistenceService.deleteSeries($0)
					}),
				],
				query: .init(filter: .league(league), ordering: .byDate),
				listTitle: league.name,
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
			case setEditorSheet(isPresented: Bool)
		}

		public enum InternalAction: Equatable {
			case list(ResourceList<Series, Series.FetchRequest>.Action)
			case editor(SeriesEditor.Action)
			case sidebar(SeriesSidebar.Action)
		}

		public enum DelegateAction: Equatable {}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.date) var date
	@Dependency(\.seriesDataProvider) var seriesDataProvider
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: seriesDataProvider.observeSeries)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, state: &state)

				case .setEditorSheet(isPresented: true):
					return startEditing(series: nil, state: &state)

				case .setEditorSheet(isPresented: false):
					state.editor = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(series):
						return startEditing(series: series, state: &state)

					case .didAddNew, .didTapEmptyStateButton:
						return startEditing(series: nil, state: &state)

					case .didDelete, .didTap:
						return .none
					}

				case let .editor(.delegate(delegateAction)):
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

				case .list(.view), .list(.internal), .list(.callback):
					return .none

				case .editor(.view), .editor(.internal), .editor(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.selection, action: /Action.internal..Action.InternalAction.sidebar) {
			Scope(state: \Identified<Series.ID, SeriesSidebar.State>.value, action: /.self) {
				SeriesSidebar()
			}
		}
		.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			SeriesEditor()
		}
	}

	private func navigate(to id: Series.ID?, state: inout State) -> EffectTask<Action> {
		if let id, let selection = state.list.resources?[id: id] {
			state.selection = Identified(.init(series: selection), id: selection.id)
			return .none
		} else {
			state.selection = nil
			return .none
		}
	}

	private func startEditing(series: Series?, state: inout State) -> EffectTask<Action> {
		let mode: SeriesEditor.Form.Mode
		if let series {
			mode = .edit(series)
		} else {
			mode = .create
		}

		state.editor = .init(
			league: state.league,
			mode: mode,
			date: date(),
			hasAlleysEnabled: featureFlags.isEnabled(.alleys),
			hasLanesEnabled: featureFlags.isEnabled(.lanes)
		)

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
