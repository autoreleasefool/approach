import AlleyEditorFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsLibrary
import ModelsLibrary
import ResourceListLibrary
import StringsLibrary
import ViewsLibrary

extension Alley.List: ResourceListItem {}

@Reducer
public struct AlleysList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var list: ResourceList<Alley.List, Alley.List.FetchRequest>.State
		public var filter: Alley.List.FetchRequest.Filter = .init()
		public var bowlerForAverages: Bowler.Summary?

		public let isAlleyAndGearAveragesEnabled: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		var bowlerName: String? { bowlerForAverages?.name }
		var isAnyFilterActive: Bool { filter != .init() }
		var isShowingAverages: Bool { isAlleyAndGearAveragesEnabled }

		public init() {
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete,
				],
				query: .init(filter: .init(), ordering: .default),
				listTitle: Strings.Alley.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.alleys,
					title: Strings.Alley.Error.Empty.title,
					message: Strings.Alley.Error.Empty.message,
					action: Strings.Alley.List.add
				)
			)

			@Dependency(\.featureFlags) var featureFlags
			self.isAlleyAndGearAveragesEnabled = featureFlags.isFlagEnabled(.alleyAndGearAverages)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didTapFiltersButton
			case didTapBowler
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case didLoadEditableAlley(Result<Alley.EditWithLanes, Error>)
			case didDeleteAlley(Result<Alley.List, Error>)

			case errors(Errors<ErrorID>.Action)
			case list(ResourceList<Alley.List, Alley.List.FetchRequest>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case editor(AlleyEditor)
		case filters(AlleysFilter)
	}

	public enum ErrorID: Hashable {
		case failedToDeleteAlley
		case alleyNotFound
	}

	public init() {}

	@Dependency(AlleysRepository.self) var alleys
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.list, action: \.internal.list) {
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
							await send(.internal(.didLoadEditableAlley(Result {
								try await alleys.edit(alley.id)
							})))
						}

					case let .didDelete(alley):
						return .run { send in
							await send(.internal(.didDeleteAlley(Result {
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

				case .destination(.dismiss),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.filters(.binding))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.editor(.delegate(.doNothing)))),
						.list(.internal), .list(.view),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.list(.delegate(.didDelete))):
				return Analytics.Alley.Deleted()
			default:
				return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadEditableAlley(.failure(error))),
				let .internal(.didDeleteAlley(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}
