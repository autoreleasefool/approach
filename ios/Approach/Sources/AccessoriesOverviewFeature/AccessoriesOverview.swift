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
import StringsLibrary

public struct AccessoriesOverview: Reducer {
	public struct State: Equatable {
		public var alleysOverview: AlleysOverview.State = .init()
		public var gearOverview: GearOverview.State = .init()

		@PresentationState public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapViewAllAlleys
			case didTapViewAllGear
			case didTapAddAlley
			case didTapAddGear
			case didTapGearKind(Gear.Kind)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case alleysOverview(AlleysOverview.Action)
			case gearOverview(GearOverview.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
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

	public init() {}

	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
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
				case let .alleysOverview(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .gearOverview(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

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
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alleysList(.internal))),
						.destination(.presented(.alleysList(.view))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))),
						.alleysOverview(.internal), .alleysOverview(.view),
						.gearOverview(.internal), .gearOverview(.view):
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
