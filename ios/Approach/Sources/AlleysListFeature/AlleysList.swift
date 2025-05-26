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
import Sharing
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Alley.List: ResourceListItem {}

extension Alley.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byName: Strings.Ordering.alphabetical
		case .byRecentlyUsed: Strings.Ordering.mostRecentlyUsed
		}
	}
}

@Reducer
public struct AlleysList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared public var fetchRequest: Alley.List.FetchRequest
		@Shared(.ordering) public var ordering: Alley.Ordering

		public var list: ResourceList<Alley.List, Alley.List.FetchRequest>.State

		public var bowlerForAverages: Bowler.Summary?
		public let isAlleyAndGearAveragesEnabled: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		var bowlerName: String? { bowlerForAverages?.name }
		var isAnyFilterActive: Bool { fetchRequest.filter != .init() }
		var isShowingAverages: Bool { isAlleyAndGearAveragesEnabled }

		public init() {
			let ordering = Shared(.ordering)
			let fetchRequest = Shared(
				value: Alley.List.FetchRequest(
					filter: .init(),
					ordering: ordering.wrappedValue
				)
			)

			self._fetchRequest = fetchRequest
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete,
				],
				query: SharedReader(fetchRequest),
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
			case onAppear
			case didTapFiltersButton
			case didTapSortOrderButton
			case didTapBowler
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case didLoadEditableAlley(Result<Alley.EditWithLanes, Error>)
			case didDeleteAlley(Result<Alley.List, Error>)
			case didChangeOrdering(Alley.Ordering)

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
		case sortOrder(SortOrderLibrary.SortOrder<Alley.Ordering>)
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
				case .onAppear:
					return .publisher {
						state.$ordering.publisher
							.map { .internal(.didChangeOrdering($0)) }
					}

				case .didTapBowler:
					// FIXME: Present picker for bowler to control averages shown
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.$ordering))
					return .none

				case .didTapFiltersButton:
					state.destination = .filters(AlleysFilter.State(filter: state.$fetchRequest.filter))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didChangeOrdering(ordering):
					state.$fetchRequest.withLock { $0.ordering = ordering }
					return .none

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

				case .destination(.presented(.sortOrder(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.filters(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.filters(.binding))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.editor(.delegate(.doNothing)))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))),
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
