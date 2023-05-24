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
		public var list: ResourceList<Alley.Summary, Alley.Summary.FetchRequest>.State
		@PresentationState public var editor: AlleyEditor.State?

		public var isFiltersPresented = false
		public var filter: Alley.Summary.FetchRequest.Filter = .init()

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
				query: .init(filter: filter, ordering: .byRecentlyUsed),
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
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableAlley(Alley.EditWithLanes)
			case list(ResourceList<Alley.Summary, Alley.Summary.FetchRequest>.Action)
			case editor(PresentationAction<AlleyEditor.Action>)
			case filters(AlleysFilter.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		Scope(state: \.filters, action: /Action.internal..Action.InternalAction.filters) {
			AlleysFilter()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { request in
				alleys.filteredList(
					withMaterial: request.filter.material,
					withPinFall: request.filter.pinFall,
					withMechanism: request.filter.mechanism,
					withPinBase: request.filter.pinBase,
					ordered: request.ordering
				)
			}
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
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableAlley(alley):
					state.editor = .init(value: .edit(alley))
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(alley):
						return .run { send in
							guard let editable = try await alleys.edit(alley.id) else {
								// TODO: report alley not found
								return
							}

							await send(.internal(.didLoadEditableAlley(editable)))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(value: .create(.default(withId: uuid())))
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
						return state.list.updateQuery(to: .init(filter: state.filters.filter, ordering: .byRecentlyUsed))
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

				case .editor(.presented(.internal)), .editor(.presented(.view)), .editor(.presented(.binding)), .editor(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			AlleyEditor()
		}
	}
}

extension AlleysList.State {
	var filters: AlleysFilter.State {
		get { .init(filter: filter) }
		set { filter = newValue.filter }
	}
}
