import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GearEditorFeature
import GearRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

extension Gear.Summary: ResourceListItem {}

extension Gear.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

public struct GearList: Reducer {
	public struct State: Equatable {
		public var list: ResourceList<Gear.Summary, Query>.State
		public var ordering: Gear.Ordering = .byRecentlyUsed
		public var kindFilter: Gear.Kind?

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init(kind: Gear.Kind?) {
			self.kindFilter = kind
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete,
				],
				query: .init(kind: kindFilter, sortOrder: ordering),
				listTitle: Strings.Gear.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.gear,
					title: Strings.Gear.Error.Empty.title,
					message: Strings.Gear.Error.Empty.message,
					action: Strings.Gear.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapFilterButton
			case didTapSortOrderButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadEditableGear(TaskResult<Gear.Edit>)
			case didDeleteGear(TaskResult<Gear.Summary>)

			case list(ResourceList<Gear.Summary, Query>.Action)
			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Query: Equatable {
		public var kind: Gear.Kind?
		public var sortOrder: Gear.Ordering
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(GearEditor.State)
			case filters(GearFilter.State)
			case sortOrder(SortOrderLibrary.SortOrder<Gear.Ordering>.State)
		}

		public enum Action: Equatable {
			case editor(GearEditor.Action)
			case filters(GearFilter.Action)
			case sortOrder(SortOrderLibrary.SortOrder<Gear.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				GearEditor()
			}
			Scope(state: /State.filters, action: /Action.filters) {
				GearFilter()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public enum ErrorID: Hashable {
		case gearNotFound
		case failedToDeleteGear
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.gear) var gear
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList {
				gear.list(ownedBy: nil, ofKind: $0.kind, ordered: $0.sortOrder)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapFilterButton:
					state.destination = .filters(.init(kind: state.kindFilter))
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableGear(.success(gear)):
					state.destination = .editor(.init(value: .edit(gear)))
					return .none

				case .didDeleteGear(.success):
					return .none

				case let .didLoadEditableGear(.failure(error)):
					return state.errors
						.enqueue(.gearNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteGear(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteGear, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(gear):
						return .run { send in
							await send(.internal(.didLoadEditableGear(TaskResult {
								try await self.gear.edit(gear.id)
							})))
						}

					case let .didDelete(gear):
						return .run { send in
							await send(.internal(.didDeleteGear(TaskResult {
								try await self.gear.delete(gear.id)
								return gear
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						let avatar = Avatar.Summary(id: uuid(), value: .text("", .default))
						state.destination = .editor(.init(value: .create(.default(withId: uuid(), avatar: avatar))))
						return .none

					case .didTap:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: .init(kind: state.kindFilter, sortOrder: state.ordering))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.filters(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeFilters(filter):
						state.kindFilter = filter
						return state.list.updateQuery(to: .init(kind: state.kindFilter, sortOrder: state.ordering))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .list(.internal), .list(.view):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))),
						.errors(.internal),
						.errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.list(.delegate(.didDelete))):
				return Analytics.Gear.Deleted()
			default:
				return nil
			}
		}
	}
}
