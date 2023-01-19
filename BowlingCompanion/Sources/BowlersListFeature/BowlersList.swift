import BowlersDataProviderInterface
import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Bowler: ResourceListItem {}

public struct BowlersList: ReducerProtocol {
	public struct State: Equatable {
		public var list: ResourceList<Bowler, Bowler.FetchRequest>.State
		public var sortOrder: SortOrder<Bowler.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed) {
			didSet {
				list.query = .init(filter: list.query.filter, ordering: sortOrder.ordering)
			}
		}

		public var selection: Identified<Bowler.ID, LeaguesList.State>?
		public var bowlerEditor: BowlerEditor.State?

		public init() {
			self.list = .init(
				features: [
					.add,
					.tappable,
					.swipeToEdit,
					.swipeToDelete(
						onDelete: .init {
							@Dependency(\.persistenceService) var persistenceService: PersistenceService
							try await persistenceService.deleteBowler($0)
						}
					),
				],
				query: .init(filter: nil, ordering: sortOrder.ordering),
				listTitle: Strings.Bowler.List.title,
				emptyContent: .init(
					image: .emptyBowlers,
					title: Strings.Bowler.Error.Empty.title,
					message: Strings.Bowler.Error.Empty.message,
					action: Strings.Bowler.List.add
				)
			)
		}
	}

	public enum Action: Equatable {
		case configureStatisticsButtonTapped

		case setNavigation(selection: Bowler.ID?)
		case setEditorFormSheet(isPresented: Bool)

		case list(ResourceList<Bowler, Bowler.FetchRequest>.Action)
		case bowlerEditor(BowlerEditor.Action)
		case leagues(LeaguesList.Action)
		case sortOrder(SortOrder<Bowler.FetchRequest.Ordering>.Action)
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.sortOrder, action: /BowlersList.Action.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /BowlersList.Action.list) {
			ResourceList {
				bowlersDataProvider.observeBowlers($0)
			}
		}

		Reduce { state, action in
			switch action {
			case .configureStatisticsButtonTapped:
				// TODO: handle configure statistics button press
				return .none

			case let .setNavigation(selection: .some(id)):
				return navigate(to: id, state: &state)

			case .setNavigation(selection: .none):
				return navigate(to: nil, state: &state)

			case let .list(.delegate(delegateAction)):
				switch delegateAction {
				case let .didEdit(bowler):
					state.bowlerEditor = .init(mode: .edit(bowler))
					return .none

				case let .didTap(bowler):
					return navigate(to: bowler.id, state: &state)

				case .didAddNew, .didTapEmptyStateButton:
					state.bowlerEditor = .init(mode: .create)
					return .none

				case .didDelete:
					return .none
				}

			case .setEditorFormSheet(isPresented: true):
				state.bowlerEditor = .init(mode: .create)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.bowlerEditor(.form(.didFinishSaving)),
					.bowlerEditor(.form(.didFinishDeleting)),
					.bowlerEditor(.form(.alert(.discardButtonTapped))):
				state.bowlerEditor = nil
				return .none

			case .sortOrder(.optionTapped):
				return .task { .list(.view(.didObserveData)) }

			case .bowlerEditor, .leagues, .sortOrder, .list(.internal), .list(.view):
				return .none
			}
		}
		.ifLet(\.bowlerEditor, action: /BowlersList.Action.bowlerEditor) {
			BowlerEditor()
		}
		.ifLet(\.selection, action: /BowlersList.Action.leagues) {
			Scope(state: \Identified<Bowler.ID, LeaguesList.State>.value, action: /.self) {
				LeaguesList()
			}
		}
	}

	private func navigate(to id: Bowler.ID?, state: inout State) -> EffectTask<Action> {
		if let id, let selection = state.list.resources?[id: id] {
			state.selection = Identified(.init(bowler: selection), id: selection.id)
			return .fireAndForget {
				try await clock.sleep(for: .seconds(1))
				recentlyUsedService.didRecentlyUseResource(.bowlers, selection.id)
			}
		} else {
			state.selection = nil
			return .none
		}
	}
}
