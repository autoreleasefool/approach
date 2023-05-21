import AlleyEditorFeature
import AlleysListFeature
import AlleysRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import GearEditorFeature
import GearListFeature
import GearRepositoryInterface
import ModelsLibrary

public struct AccessoriesOverview: Reducer {
	public struct State: Equatable {
		public var recentAlleys: IdentifiedArrayOf<Alley.Summary> = []
		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		@PresentationState public var alleyEditor: AlleyEditor.State?
		@PresentationState public var alleysList: AlleysList.State?
		@PresentationState public var gearEditor: GearEditor.State?
		@PresentationState public var gearList: GearList.State?

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
			case errorDeletingAlley(AlwaysEqual<Error>)

			case gearResponse(TaskResult<[Gear.Summary]>)
			case didLoadEditableGear(TaskResult<Gear.Edit?>)
			case errorDeletingGear(AlwaysEqual<Error>)

			case alleyEditor(PresentationAction<AlleyEditor.Action>)
			case alleysList(PresentationAction<AlleysList.Action>)
			case gearEditor(PresentationAction<GearEditor.Action>)
			case gearList(PresentationAction<GearList.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	enum CancelID { case observe }

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
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
						return .run { _ in
							try await self.gear.delete(id)
						} catch: { error, send in
							await send(.internal(.errorDeletingGear(.init(error))))
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
							await send(.internal(.errorDeletingAlley(.init(error))))
						}
					}

				case .didTapViewAllGear:
					state.gearList = .init(kind: nil)
					return .none

				case .didTapViewAllAlleys:
					state.alleysList = .init()
					return .none

				case .didTapAddAlley:
					state.alleyEditor = .init(value: .create(.default(withId: uuid())))
					return .none

				case .didTapAddGear:
					state.gearEditor = .init(value: .create(.default(withId: uuid())))
					return .none

				case let .didTapGearKind(kind):
					state.gearList = .init(kind: kind)
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
					state.alleyEditor = .init(value: .edit(alley))
					return .none

				case .didLoadEditableAlley(.failure):
					// TODO: show error failed to load alley
					return .none

				case .errorDeletingAlley:
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
					state.gearEditor = .init(value: .edit(gear))
					return .none

				case .didLoadEditableGear(.failure):
					// TODO: show error failed to load gear
					return .none

				case .errorDeletingGear:
					// TODO: show error deleting gear
					return .none

				case let .alleyEditor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .didFinishEditing:
						state.alleyEditor = nil
						return .none
					}

				case let .alleysList(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .gearEditor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .didFinishEditing:
						state.gearEditor = nil
						return .none
					}

				case let .gearList(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .gearEditor(.dismiss),
						.gearEditor(.presented(.binding)),
						.gearEditor(.presented(.view)),
						.gearEditor(.presented(.internal)):
					return .none

				case .alleyEditor(.dismiss),
						.alleyEditor(.presented(.binding)),
						.alleyEditor(.presented(.view)),
						.alleyEditor(.presented(.internal)):
					return .none

				case .alleysList(.dismiss), .alleysList(.presented(.internal)), .alleysList(.presented(.view)):
					return .none

				case .gearList(.dismiss), .gearList(.presented(.internal)), .gearList(.presented(.view)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyEditor, action: /Action.internal..Action.InternalAction.alleyEditor) {
			AlleyEditor()
		}
		.ifLet(\.$gearEditor, action: /Action.internal..Action.InternalAction.gearEditor) {
			GearEditor()
		}
		.ifLet(\.$alleysList, action: /Action.internal..Action.InternalAction.alleysList) {
			AlleysList()
		}
		.ifLet(\.$gearList, action: /Action.internal..Action.InternalAction.gearList) {
			GearList()
		}
	}
}
