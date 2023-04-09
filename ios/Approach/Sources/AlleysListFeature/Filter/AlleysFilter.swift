import AlleysRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary

public struct AlleysFilter: Reducer {
	public struct State: Equatable {
		@BindingState public var material: Alley.Material?
		@BindingState public var mechanism: Alley.Mechanism?
		@BindingState public var pinBase: Alley.PinBase?
		@BindingState public var pinFall: Alley.PinFall?

		public init() {}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapClearButton
			case didTapApplyButton
		}
		public enum DelegateAction: Equatable {
			case didChangeFilters
			case didApplyFilters
		}
		public enum InternalAction: Equatable {}

		case binding(BindingAction<State>)
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapClearButton:
					state = .init()
					return .task { .delegate(.didApplyFilters) }

				case .didTapApplyButton:
					return .task { .delegate(.didApplyFilters) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .binding:
				return .task { .delegate(.didChangeFilters) }

			case .delegate:
				return .none
			}
		}
	}
}

extension AlleysFilter.State {
	public var hasFilters: Bool {
		filter != .init()
	}

	public var filter: Alley.Filters {
		.init(material: material, pinFall: pinFall, mechanism: mechanism, pinBase: pinBase)
	}
}
