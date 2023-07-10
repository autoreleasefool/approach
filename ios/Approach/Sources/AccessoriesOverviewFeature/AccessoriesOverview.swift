import AlleyEditorFeature
import AlleysListFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import GearEditorFeature
import GearListFeature
import GearRepositoryInterface
import ModelsLibrary

public struct AccessoriesOverview: Reducer {
	public struct State: Equatable {
		public var recentAlleys: IdentifiedArrayOf<Alley.Summary> = []
		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

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
			case didLoadEditableAlley(TaskResult<Alley.EditWithLanes?>)
			case didDeleteAlley(TaskResult<Never>)

			case gearResponse(TaskResult<[Gear.Summary]>)
			case didLoadEditableGear(TaskResult<Gear.Edit?>)
			case didDeleteGear(TaskResult<Never>)

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

	enum CancelID { case observe }

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.analytics) var analytics
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return .merge(
						.run { send in
							for try await alleys in self.alleys.overview() {
								await send(.internal(.alleysResponse(.success(alleys))))
							}
						} catch: { error, send in
							await send(.internal(.alleysResponse(.failure(error))))
						},
						.run { send in
							for try await gear in self.gear.overview() {
								await send(.internal(.gearResponse(.success(gear))))
							}
						} catch: { error, send in
							await send(.internal(.gearResponse(.failure(error))))
						}
					)
					.cancellable(id: CancelID.observe)

				case let .didSwipeGear(action, id):
					switch action {
					case .edit:
						return .run { send in
							await send(.internal(.didLoadEditableGear(TaskResult {
								try await self.gear.edit(id)
							})))
						}

					case .delete:
						return .merge(
							.run { _ in
								try await self.gear.delete(id)
							} catch: { error, send in
								await send(.internal(.didDeleteGear(.failure(error))))
							},
							.run { _ in await analytics.trackEvent(Analytics.Gear.Deleted()) }
						)
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
						return .merge(
							.run { _ in
								try await self.alleys.delete(id)

							} catch: { error, send in
								await send(.internal(.didDeleteAlley(.failure(error))))
							},
							.run { _ in await analytics.trackEvent(Analytics.Alley.Deleted()) }
						)
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

				case .alleysResponse(.failure):
					// TODO: handle alley loading failure
					return .none

				case let .didLoadEditableAlley(.success(alley)):
					guard let alley else { return .none } // TODO: show error failed to load alley
					state.destination = .alleyEditor(.init(value: .edit(alley)))
					return .none

				case .didLoadEditableAlley(.failure):
					// TODO: show error failed to load alley
					return .none

				case .didDeleteAlley(.failure):
					// TODO: show error deleting alley
					return .none

				case let .gearResponse(.success(gear)):
					state.recentGear = .init(uniqueElements: gear)
					return .none

				case .gearResponse(.failure):
					// TODO: handle gear loading failure
					return .none

				case let .didLoadEditableGear(.success(gear)):
					guard let gear else { return .none } // TODO: show error failed to load gear
					state.destination = .gearEditor(.init(value: .edit(gear)))
					return .none

				case .didLoadEditableGear(.failure):
					// TODO: show error failed to load gear
					return .none

				case .didDeleteGear(.failure):
					// TODO: show error deleting gear
					return .none

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

				case .destination(.dismiss),
						.destination(.presented(.gearEditor(.view))),
						.destination(.presented(.gearEditor(.internal))),
						.destination(.presented(.alleyEditor(.binding))),
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alleysList(.internal))),
						.destination(.presented(.alleysList(.view))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}
}
