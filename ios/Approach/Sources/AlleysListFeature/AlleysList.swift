import AlleyEditorFeature
import AlleysRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import ViewsLibrary

extension Alley.Summary: ResourceListItem {}

public struct AlleysList: Reducer {
	public struct State: Equatable {
		public var list: ResourceList<Alley.Summary, Alley.FetchRequest>.State
		public var editor: AlleyEditor.State?

		public var isFiltersPresented = false
		public var filters: AlleysFilter.State = .init()

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.alleys) var alleys: AlleysRepository
						try await alleys.delete($0.id)
					}),
				],
				query: .init(filter: filters.filter, ordering: .byRecentlyUsed),
				listTitle: Strings.Alley.List.title,
				emptyContent: .init(
					image: .emptyAlleys,
					title: Strings.Alley.Error.Empty.title,
					message: Strings.Alley.Error.Empty.message,
					action: Strings.Alley.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case setFilterSheet(isPresented: Bool)
			case setEditorSheet(isPresented: Bool)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableAlley(Alley.Editable)
			case list(ResourceList<Alley.Summary, Alley.FetchRequest>.Action)
			case editor(AlleyEditor.Action)
			case filters(AlleysFilter.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService

	public var body: some Reducer<State, Action> {
		Scope(state: \.filters, action: /Action.internal..Action.InternalAction.filters) {
			AlleysFilter()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: alleys.list)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .setFilterSheet(isPresented: true):
					state.isFiltersPresented = true
					return .none

				case .setFilterSheet(isPresented: false):
					state.isFiltersPresented = false
					return .task { .internal(.list(.callback(.shouldRefreshData))) }

				case .setEditorSheet(isPresented: true):
					state.editor = .init(
						mode: .create,
						hasLanesEnabled: featureFlags.isEnabled(.lanes)
					)
					return .none

				case .setEditorSheet(isPresented: false):
					state.editor = nil
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableAlley(alley):
					state.editor = .init(
						mode: .edit(alley),
						hasLanesEnabled: featureFlags.isEnabled(.lanes)
					)
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(alley):
						return .run { send in
							guard let editable = try await alleys.edit(alley.id) else {
								return
							}

							await send(.internal(.didLoadEditableAlley(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(mode: .create, hasLanesEnabled: featureFlags.isEnabled(.lanes))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case let .filters(.delegate(delegateAction)):
					switch delegateAction {
					case .didApplyFilters:
						state.isFiltersPresented = false
						return .none

					case .didChangeFilters:
						state.updateQuery()
						return .task { .`internal`(.list(.callback(.shouldRefreshData))) }
					}

				case let .editor(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.editor = nil
						return .none
					}

				case .list(.internal), .list(.view), .list(.callback):
					return .none

				case .filters(.internal), .filters(.view), .filters(.binding):
					return .none

				case .editor(.internal), .editor(.view), .editor(.binding):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.editor, action: /Action.internal..Action.InternalAction.editor) {
			AlleyEditor()
		}
	}
}

extension AlleysList.State {
	mutating func updateQuery() {
		list.query = .init(filter: filters.filter, ordering: .byRecentlyUsed)
	}
}
