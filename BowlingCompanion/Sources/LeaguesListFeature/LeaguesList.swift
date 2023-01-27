import ComposableArchitecture
import FeatureActionLibrary
import LeaguesDataProviderInterface
import LeagueEditorFeature
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SeriesListFeature
import SharedModelsLibrary
import StringsLibrary
import ViewsLibrary

extension League: ResourceListItem {}

public struct LeaguesList: ReducerProtocol {
	public struct State: Equatable {
		public let bowler: Bowler

		public var list: ResourceList<League, League.FetchRequest>.State
		public var editor: LeagueEditor.State?

		public var isFiltersPresented = false
		public var filters: LeaguesFilter.State = .init()

		public var selection: Identified<League.ID, SeriesList.State>?

		public init(bowler: Bowler) {
			self.bowler = bowler
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.persistenceService) var persistenceService: PersistenceService
						try await persistenceService.deleteLeague($0)
					})
				],
				query: .init(filter: nil, ordering: .byRecentlyUsed),
				listTitle: Strings.Alley.List.title,
				emptyContent: .init(
					image: .emptyLeagues,
					title: Strings.League.Error.Empty.title,
					message: Strings.League.Error.Empty.message,
					action: Strings.League.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setNavigation(selection: League.ID?)
			case setFilterSheet(isPresented: Bool)
			case setEditorSheet(isPresented: Bool)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case list(ResourceList<League, League.FetchRequest>.Action)
			case editor(LeagueEditor.Action)
			case filters(LeaguesFilter.Action)
			case series(SeriesList.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.leaguesDataProvider) var leaguesDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService
	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.filters, action: /Action.internal..Action.InternalAction.filters) {
			LeaguesFilter()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: leaguesDataProvider.observeLeagues)
		}

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, state: &state)

				case .setFilterSheet(isPresented: true):
					state.isFiltersPresented = true
					return .none

				case .setFilterSheet(isPresented: false):
					state.isFiltersPresented = false
					return .task { .internal(.list(.view(.didObserveData))) }

				case .setEditorSheet(isPresented: true):
					return startEditing(league: nil, state: &state)

				case .setEditorSheet(isPresented: false):
					state.editor = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(league):
						return startEditing(league: league, state: &state)

					case .didAddNew, .didTapEmptyStateButton:
						return startEditing(league: nil, state: &state)

					case .didDelete, .didTap:
						return .none
					}

				case .editor(.form(.didFinishSaving)),
						.editor(.form(.didFinishDeleting)),
						.editor(.form(.alert(.discardButtonTapped))):
					state.editor = nil
					return .none

				case .list(.internal), .list(.view), .editor, .filters, .series:
					return .none

				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			LeagueEditor()
		}
		.ifLet(\.selection, action: /Action.internal..Action.InternalAction.series) {
			Scope(state: \Identified<League.ID, SeriesList.State>.value, action: /.self) {
				SeriesList()
			}
		}
	}

	private func navigate(to id: League.ID?, state: inout State) -> EffectTask<Action> {
		if let id, let selection = state.list.resources?[id: id] {
			state.selection = Identified(.init(league: selection), id: selection.id)
			return .fireAndForget {
				try await clock.sleep(for: .seconds(1))
				recentlyUsedService.didRecentlyUseResource(.leagues, selection.id)
			}
		} else {
			state.selection = nil
			return .none
		}
	}

	private func startEditing(league: League?, state: inout State) -> EffectTask<Action> {
		let mode: LeagueEditor.Form.Mode
		if let league {
			mode = .edit(league)
		} else {
			mode = .create
		}

		state.editor = .init(
			bowler: state.bowler,
			mode: mode,
			hasAlleysEnabled: featureFlags.isEnabled(.alleys)
		)

		return .none
	}
}
