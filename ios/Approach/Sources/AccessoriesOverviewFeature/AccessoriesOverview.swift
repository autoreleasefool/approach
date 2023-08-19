import AlleyEditorFeature
import AlleysListFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GearEditorFeature
import GearListFeature
import GearRepositoryInterface
import ModelsLibrary
import StringsLibrary

// swiftlint:disable:next type_body_length
public struct AccessoriesOverview: Reducer {
	public struct State: Equatable {
		public var recentAlleys: IdentifiedArrayOf<Alley.Summary> = []
		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapViewAllAlleys
			case didTapViewAllGear
			case didTapGearKind(Gear.Kind)
			case didTapAddAlley
			case didTapAddGear
			case didSwipeAlley(SwipeAction, Alley.ID)
			case didSwipeGear(SwipeAction, Gear.ID)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case alleysResponse(TaskResult<[Alley.Summary]>)
			case didLoadEditableAlley(TaskResult<Alley.EditWithLanes>)
			case didDeleteAlley(TaskResult<Never>)

			case gearResponse(TaskResult<[Gear.Summary]>)
			case didLoadEditableGear(TaskResult<Gear.Edit>)
			case didDeleteGear(TaskResult<Never>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case alleyEditor(AlleyEditor.State)
			case alleysList(AlleysList.State)
			case gearEditor(GearEditor.State)
			case gearList(GearList.State)
		}

		public enum Action: Equatable {
			case alleyEditor(AlleyEditor.Action)
			case alleysList(AlleysList.Action)
			case gearEditor(GearEditor.Action)
			case gearList(GearList.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.alleyEditor, action: /Action.alleyEditor) {
				AlleyEditor()
			}
			Scope(state: /State.alleysList, action: /Action.alleysList) {
				AlleysList()
			}
			Scope(state: /State.gearEditor, action: /Action.gearEditor) {
				GearEditor()
			}
			Scope(state: /State.gearList, action: /Action.gearList) {
				GearList()
			}
		}
	}

	enum CancelID {
		case observeAlleys
		case observeGear
	}

	public enum ErrorID: Hashable {
		case alleyNotFound
		case loadingAlleysFailed
		case failedToDeleteAlley
		case gearNotFound
		case loadingGearFailed
		case failedToDeleteGear
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return .merge(
						observeAlleys(),
						observeGear()
					)

				case let .didSwipeGear(action, id):
					switch action {
					case .edit:
						return .run { send in
							await send(.internal(.didLoadEditableGear(TaskResult {
								try await self.gear.edit(id)
							})))
						}

					case .delete:
						return .run { _ in
							try await self.gear.delete(id)
						} catch: { error, send in
							await send(.internal(.didDeleteGear(.failure(error))))
						}
					}

				case let .didSwipeAlley(action, id):
					switch action {
					case .edit:
						return .run { send in
							await send(.internal(.didLoadEditableAlley(TaskResult {
								try await self.alleys.edit(id)
							})))
						}

					case .delete:
						return .run { _ in
							try await self.alleys.delete(id)
						} catch: { error, send in
							await send(.internal(.didDeleteAlley(.failure(error))))
						}
					}

				case .didTapViewAllGear:
					state.destination = .gearList(.init(kind: nil))
					return .none

				case .didTapViewAllAlleys:
					state.destination = .alleysList(.init())
					return .none

				case .didTapAddAlley:
					state.destination = .alleyEditor(.init(value: .create(.default(withId: uuid()))))
					return .none

				case .didTapAddGear:
					state.destination = .gearEditor(.init(value: .create(.default(withId: uuid()))))
					return .none

				case let .didTapGearKind(kind):
					state.destination = .gearList(.init(kind: kind))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleysResponse(.success(alleys)):
					state.recentAlleys = .init(uniqueElements: alleys)
					return .none

				case let .didLoadEditableAlley(.success(alley)):
					state.destination = .alleyEditor(.init(value: .edit(alley)))
					return .none

				case let .gearResponse(.success(gear)):
					state.recentGear = .init(uniqueElements: gear)
					return .none

				case let .didLoadEditableGear(.success(gear)):
					state.destination = .gearEditor(.init(value: .edit(gear)))
					return .none

				case let .alleysResponse(.failure(error)):
					return state.errors
						.enqueue(.loadingAlleysFailed, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableAlley(.failure(error)):
					return state.errors
						.enqueue(.alleyNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteAlley(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteAlley, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .gearResponse(.failure(error)):
					return state.errors
						.enqueue(.loadingGearFailed, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableGear(.failure(error)):
					return state.errors
						.enqueue(.gearNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteGear(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteGear, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .destination(.presented(.alleyEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.alleysList(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.gearEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.gearList(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.gearEditor(.view))),
						.destination(.presented(.gearEditor(.internal))),
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alleysList(.internal))),
						.destination(.presented(.alleysList(.view))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))),
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
			case .view(.didSwipeGear(.delete, _)):
				return Analytics.Gear.Deleted()
			case .view(.didSwipeAlley(.delete, _)):
				return Analytics.Alley.Deleted()
			default:
				return nil
			}
		}
	}

	private func observeAlleys() -> Effect<Action> {
		.run { send in
			for try await alleys in self.alleys.overview() {
				await send(.internal(.alleysResponse(.success(alleys))))
			}
		} catch: { error, send in
			await send(.internal(.alleysResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observeAlleys)
	}

	private func observeGear() -> Effect<Action> {
		.run { send in
			for try await gear in self.gear.overview() {
				await send(.internal(.gearResponse(.success(gear))))
			}
		} catch: { error, send in
			await send(.internal(.gearResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observeGear)
	}
}
