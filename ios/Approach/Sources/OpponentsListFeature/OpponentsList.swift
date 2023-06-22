import AssetsLibrary
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
		public var ordering: Bowler.Ordering = .byRecentlyUsed

		@PresentationState public var destination: Destination.State?

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
				query: ordering,
				listTitle: Strings.Opponent.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.opponents,
					title: Strings.Opponent.Error.Empty.title,
					message: Strings.Opponent.Error.Empty.message,
					action: Strings.Opponent.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapSortOrderButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadEditableBowler(Bowler.Edit)
			case list(ResourceList<Bowler.Summary, Bowler.Ordering>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(BowlerEditor.State)
			case sortOrder(SortOrder<Bowler.Ordering>.State)
		}

		public enum Action: Equatable {
			case editor(BowlerEditor.Action)
			case sortOrder(SortOrder<Bowler.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				BowlerEditor()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(\.recentlyUsed) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: bowlers.opponents(ordered:))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableBowler(bowler):
					state.destination = .editor(.init(value: .edit(bowler)))
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
						state.destination = .editor(.init(value: .create(.defaultOpponent(withId: uuid()))))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: state.ordering)
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}
}
