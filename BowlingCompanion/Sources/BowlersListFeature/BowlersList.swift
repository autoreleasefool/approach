import BowlersDataProviderInterface
import BowlerEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
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
		public var editor: BowlerEditor.State?
		public var sortOrder: SortOrder<Bowler.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed) {
			didSet {
				list.query = .init(filter: list.query.filter, ordering: sortOrder.ordering)
			}
		}

		public var selection: Identified<Bowler.ID, LeaguesList.State>?

		public init() {
			self.list = .init(
				features: [
					.add,
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapConfigureStatisticsButton
			case setNavigation(selection: Bowler.ID?)
			case setEditorFormSheet(isPresented: Bool)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case list(ResourceList<Bowler, Bowler.FetchRequest>.Action)
			case editor(BowlerEditor.Action)
			case leagues(LeaguesList.Action)
			case sortOrder(SortOrder<Bowler.FetchRequest.Ordering>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.sortOrder, action: /Action.internal..Action.InternalAction.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: bowlersDataProvider.observeBowlers)
		}

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapConfigureStatisticsButton:
					// TODO: handle configure statistics button press
					return .none

				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, state: &state)

				case .setEditorFormSheet(isPresented: true):
					state.editor = .init(mode: .create)
					return .none

				case .setEditorFormSheet(isPresented: false):
					state.editor = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(bowler):
						state.editor = .init(mode: .edit(bowler))
						return .none

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(mode: .create)
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case .editor(.form(.didFinishSaving)),
					.editor(.form(.didFinishDeleting)),
					.editor(.form(.alert(.discardButtonTapped))):
					state.editor = nil
					return .none

				case .sortOrder(.optionTapped):
					return .task { .`internal`(.list(.view(.didObserveData))) }

				case .list(.internal), .list(.view), .editor, .leagues, .sortOrder:
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			BowlerEditor()
		}
		.ifLet(\.selection, action: /Action.internal..Action.InternalAction.leagues) {
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
