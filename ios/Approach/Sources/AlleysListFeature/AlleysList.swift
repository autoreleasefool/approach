import AlleyEditorFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsServiceInterface
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import ViewsLibrary

extension Alley.List: ResourceListItem {}

public struct AlleysList: Reducer {
	public struct State: Equatable {
		public var list: ResourceList<Alley.List, Alley.List.FetchRequest>.State
		public var filter: Alley.List.FetchRequest.Filter = .init()
		public var bowlerForAverages: Bowler.Summary?

		public let isAlleyAndGearAveragesEnabled: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete,
				],
				query: .init(filter: filter, ordering: .byRecentlyUsed),
				listTitle: Strings.Alley.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.alleys,
					title: Strings.Alley.Error.Empty.title,
					message: Strings.Alley.Error.Empty.message,
					action: Strings.Alley.List.add
				)
			)

			@Dependency(\.featureFlags) var featureFlags
			self.isAlleyAndGearAveragesEnabled = featureFlags.isEnabled(.alleyAndGearAverages)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapFiltersButton
			case didTapBowler
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableAlley(TaskResult<Alley.EditWithLanes>)
			case didDeleteAlley(TaskResult<Alley.List>)

			case errors(Errors<ErrorID>.Action)
			case list(ResourceList<Alley.List, Alley.List.FetchRequest>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(AlleyEditor.State)
			case filters(AlleysFilter.State)
		}

		public enum Action: Equatable {
			case editor(AlleyEditor.Action)
			case filters(AlleysFilter.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				AlleyEditor()
			}
			Scope(state: /State.filters, action: /Action.filters) {
				AlleysFilter()
			}
		}
	}

	public enum ErrorID: Hashable {
		case failedToDeleteAlley
		case alleyNotFound
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
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
				case .didTapBowler:
					// FIXME: Present picker for bowler to control averages shown
					return .none

				case .didTapFiltersButton:
					state.destination = .filters(.init(filter: state.filter))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableAlley(.success(alley)):
					state.destination = .editor(.init(value: .edit(alley)))
					return .none

				case .didDeleteAlley(.success):
					return .none

				case let .didLoadEditableAlley(.failure(error)):
					return state.errors
						.enqueue(.alleyNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteAlley(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteAlley, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(alley):
						return .run { send in
							await send(.internal(.didLoadEditableAlley(TaskResult {
								try await alleys.edit(alley.id)
							})))
						}

					case let .didDelete(alley):
						return .run { send in
							await send(.internal(.didDeleteAlley(TaskResult {
								try await alleys.delete(alley.id)
								return alley
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.default(withId: uuid()))))
						return .none

					case .didTap, .didArchive, .didMove:
						return .none
					}

				case let .destination(.presented(.filters(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeFilters(filter):
						state.filter = filter
						return state.list.updateQuery(to: .init(filter: filter, ordering: .byRecentlyUsed))
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
				return Analytics.Alley.Deleted()
			default:
				return nil
			}
		}
	}
}
