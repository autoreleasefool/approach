import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GearEditorFeature
import GearRepositoryInterface
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SortOrderLibrary
import StringsLibrary
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
		public var sortOrder: SortOrder<Gear.Ordering>.State = .init(initialValue: .byRecentlyUsed)
		@PresentationState public var editor: GearEditor.State?

		public var isFiltersPresented = false
		public var kindFilter: Gear.Kind?

		public init(kind: Gear.Kind?) {
			self.kindFilter = kind
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.gear) var gear: GearRepository
						try await gear.delete($0.id)
					}),
				],
				query: .init(kind: kindFilter, sortOrder: sortOrder.ordering),
				listTitle: Strings.Gear.List.title,
				emptyContent: .init(
					image: .emptyGear,
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
			case setFilterSheet(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadEditableGear(Gear.Edit)
			case list(ResourceList<Gear.Summary, Query>.Action)
			case editor(PresentationAction<GearEditor.Action>)
			case sortOrder(SortOrder<Gear.Ordering>.Action)
			case filters(GearFilter.Action)

		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Query: Equatable {
		public var kind: Gear.Kind?
		public var sortOrder: Gear.Ordering
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.gear) var gear
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		Scope(state: \.sortOrder, action: /Action.internal..Action.InternalAction.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList {
				gear.list(ownedBy: nil, ofKind: $0.kind, ordered: $0.sortOrder)
			}
		}

		Scope(state: \.filters, action: /Action.internal..Action.InternalAction.filters) {
			GearFilter()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapFilterButton:
					state.isFiltersPresented = true
					return .none

				case let .setFilterSheet(isPresented):
					state.isFiltersPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableGear(gear):
					state.editor = .init(value: .edit(gear))
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(gear):
						return .run { send in
							guard let editable = try await self.gear.edit(gear.id) else {
								// TODO: report gear not found
								return
							}

							await send(.internal(.didLoadEditableGear(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(value: .create(.default(withId: uuid())))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .sortOrder(.delegate(delegateAction)):
					switch delegateAction {
					case .didTapOption:
						return state.list.updateQuery(to: .init(kind: state.kindFilter, sortOrder: state.sortOrder.ordering))
							.map { .internal(.list($0)) }
					}

				case let .filters(.delegate(delegateAction)):
					switch delegateAction {
					case .didApplyFilters:
						state.isFiltersPresented = false
						return .none

					case .didChangeFilters:
						return state.list.updateQuery(to: .init(kind: state.kindFilter, sortOrder: state.sortOrder.ordering))
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

				case .filters(.internal), .filters(.view), .filters(.binding):
					return .none

				case .sortOrder(.internal), .sortOrder(.view):
					return .none

				case .editor(.presented(.internal)), .editor(.presented(.view)), .editor(.presented(.binding)), .editor(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			GearEditor()
		}
	}
}

extension GearList.State {
	var filters: GearFilter.State {
		get { .init(kind: kindFilter) }
		set { kindFilter = newValue.kind }
	}
}
