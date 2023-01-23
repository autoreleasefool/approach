import AlleysDataProviderInterface
import AlleyEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import PersistenceServiceInterface
import ResourceListLibrary
import SharedModelsLibrary
import StringsLibrary
import ViewsLibrary

extension Alley: ResourceListItem {}

public struct AlleysList: ReducerProtocol {
	public struct State: Equatable {
		public var list: ResourceList<Alley, Alley.FetchRequest>.State
		public var editor: AlleyEditor.State?

		public var isFiltersPresented = false
		public var filters: AlleysFilter.State = .init()

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete(onDelete: .init {
						@Dependency(\.persistenceService) var persistenceService: PersistenceService
						try await persistenceService.deleteAlley($0)
					})
				],
				query: .init(filter: nil, ordering: .byRecentlyUsed),
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
			case list(ResourceList<Alley, Alley.FetchRequest>.Action)
			case editor(AlleyEditor.Action)
			case filters(AlleysFilter.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.filters, action: /Action.internal..Action.InternalAction.filters) {
			AlleysFilter()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList(fetchResources: alleysDataProvider.observeAlleys)
		}

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .setFilterSheet(isPresented: true):
					state.isFiltersPresented = true
					return .none

				case .setFilterSheet(isPresented: false):
					state.isFiltersPresented = false
					return .task { .internal(.list(.view(.didObserveData))) }

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
				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(alley):
						state.editor = .init(
							mode: .edit(alley),
							hasLanesEnabled: featureFlags.isEnabled(.lanes)
						)
						return .none

					case .didAddNew, .didTapEmptyStateButton:
						state.editor = .init(mode: .create, hasLanesEnabled: featureFlags.isEnabled(.lanes))
						return .none

					case .didDelete, .didTap:
						return .none
					}

				case .editor(.form(.didFinishSaving)),
					.editor(.form(.didFinishDeleting)),
					.editor(.form(.alert(.discardButtonTapped))):
					state.editor = nil
					return .none

				case .list(.internal), .list(.view), .editor, .filters:
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
