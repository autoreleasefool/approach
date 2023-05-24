import BowlerEditorFeature
import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Bowler.Summary: ResourceListItem {}

extension Bowler.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

public struct OpponentsList: Reducer {
	public struct State: Equatable {
		public var list: ResourceList<Bowler.Summary, Bowler.Ordering>.State
		public var sortOrder: SortOrder<Bowler.Ordering>.State = .init(initialValue: .byRecentlyUsed)

		@PresentationState public var editor: BowlerEditor.State?
		public var selection: Identified<Bowler.ID, Int>?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.bowlers) var bowlers: BowlersRepository
						try await bowlers.delete($0.id)
					}),
				],
				query: sortOrder.ordering,
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
			case setNavigation(selection: Bowler.ID?)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadEditableBowler(Bowler.Edit)
			case list(ResourceList<Bowler.Summary, Bowler.Ordering>.Action)
			case editor(PresentationAction<BowlerEditor.Action>)
			case sortOrder(SortOrder<Bowler.Ordering>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(\.recentlyUsed) var recentlyUsed

	public var body: some Reducer<State, Action> {
		Scope(state: \.sortOrder, action: /Action.internal..Action.InternalAction.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: bowlers.opponents(ordered:))
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
				case let .didLoadEditableBowler(bowler):
					state.editor = .init(value: .edit(bowler))
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(opponent):
						return .run { send in
							guard let editable = try await bowlers.edit(opponent.id) else {
								// TODO: report bowler not found
								return
							}

							await send(.internal(.didLoadEditableBowler(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(value: .create(.defaultOpponent(withId: uuid())))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .sortOrder(.delegate(delegateAction)):
					switch delegateAction {
					case .didTapOption:
						return state.list.updateQuery(to: state.sortOrder.ordering)
							.map { .internal(.list($0)) }
					}

				case let .editor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .didFinishEditing:
						state.editor = nil
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .editor(.presented(.internal)), .editor(.presented(.view)), .editor(.presented(.binding)), .editor(.dismiss):
					return .none

				case .sortOrder(.internal), .sortOrder(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			BowlerEditor()
		}
	}

	private func navigate(to id: Bowler.ID?, state: inout State) -> Effect<Action> {
		if let id, let selection = state.list.resources?[id: id] {
			// TODO: show opponent profile
//			state.selection = Identified(.init(bowler: selection), id: selection.id)
			return .run { _ in
				try await clock.sleep(for: .seconds(1))
				recentlyUsed.didRecentlyUseResource(.opponents, selection.id)
			}
		} else {
			state.selection = nil
			return .none
		}
	}
}
