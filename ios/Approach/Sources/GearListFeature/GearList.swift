import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import GearDataProviderInterface
import GearEditorFeature
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SharedModelsLibrary
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Gear: ResourceListItem {}

public struct GearList: ReducerProtocol {
	public struct State: Equatable {
		public var list: ResourceList<Gear, Gear.FetchRequest>.State
		public var editor: GearEditor.State?
		public var sortOrder: SortOrder<Gear.FetchRequest.Ordering>.State = .init(initialValue: .byRecentlyUsed)

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.persistenceService) var persistenceService: PersistenceService
						try await persistenceService.deleteGear($0)
					})
				],
				query: .init(filter: nil, ordering: sortOrder.ordering),
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
			case setEditorSheet(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case list(ResourceList<Gear, Gear.FetchRequest>.Action)
			case editor(GearEditor.Action)
			case sortOrder(SortOrder<Gear.FetchRequest.Ordering>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.gearDataProvider) var gearDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.sortOrder, action: /Action.internal..Action.InternalAction.sortOrder) {
			SortOrder()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: gearDataProvider.observeGear)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
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
					case let .didEdit(gear):
						state.editor = .init(mode: .edit(gear))
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
						state.updateQuery()
						return .task { .internal(.list(.callback(.shouldRefreshData))) }
					}

				case let .editor(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.editor = nil
						return .none
					}

				case .list(.internal), .list(.view), .list(.callback):
					return .none

				case .sortOrder(.internal), .sortOrder(.view):
					return .none

				case .editor(.internal), .editor(.view), .editor(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			GearEditor()
		}
	}
}

extension GearList.State {
	mutating func updateQuery() {
		list.query = .init(filter: nil, ordering: sortOrder.ordering)
	}
}
