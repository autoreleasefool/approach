import ComposableArchitecture
import FeatureActionLibrary
import OpponentsDataProviderInterface
import OpponentEditorFeature
import PersistenceServiceInterface
import ResourceListLibrary
import SharedModelsLibrary
import SharedModelsFetchableLibrary
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Opponent: ResourceListItem {}

public struct OpponentsList: ReducerProtocol {
	public struct State: Equatable {
		public var list: ResourceList<Opponent, Opponent.FetchRequest>.State
		public var editor: OpponentEditor.State?
		public var sortOrder: SortOrder<Opponent.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed)

		public var selection: Identified<Opponent.ID, Int>?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.persistenceService) var persistenceService: PersistenceService
						try await persistenceService.deleteOpponent($0)
					})
				],
				query: .init(filter: nil, ordering: sortOrder.ordering),
				listTitle: Strings.Opponent.List.title,
				emptyContent: .init(
					image: .emptyOpponents,
					title: Strings.Opponent.Error.Empty.title,
					message: Strings.Opponent.Error.Empty.message,
					action: Strings.Opponent.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setNavigation(selection: Opponent.ID?)
			case setEditorSheet(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case list(ResourceList<Opponent, Opponent.FetchRequest>.Action)
			case editor(OpponentEditor.Action)
			case sortOrder(SortOrder<Opponent.FetchRequest.Ordering>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.opponentsDataProvider) var opponentsDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.sortOrder, action: /Action.internal..Action.InternalAction.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: opponentsDataProvider.observeOpponents)
		}

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setNavigation(selection: .some(id)):
					return navigate(to: id, state: &state)

				case .setNavigation(selection: .none):
					return navigate(to: nil, state: &state)

				case .setEditorSheet(isPresented: true):
					state.editor = .init(mode: .create)
					return .none

				case .setEditorSheet(isPresented: false):
					state.editor = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(opponent):
						state.editor = .init(mode: .edit(opponent))
						return .none

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(mode: .create)
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .sortOrder(.delegate(delegateAction)):
					switch delegateAction {
					case .didTapOption:
						return .task { .internal(.list(.view(.didObserveData))) }
					}

				case let .editor(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.editor = nil
						return .none
					}

				case .list(.internal),
						.list(.view),
						.editor(.internal),
						.editor(.view),
						.editor(.binding),
						.sortOrder(.internal),
						.sortOrder(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			OpponentEditor()
		}
	}

	private func navigate(to id: Opponent.ID?, state: inout State) -> EffectTask<Action> {
		// TODO: show/hide opponent profile
		return .none
	}
}
