import AlleysDataProviderInterface
import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct AlleysFilter: ReducerProtocol {
	public struct State: Equatable {
		@BindableState public var material: Alley.Material?
		@BindableState public var mechanism: Alley.Mechanism?
		@BindableState public var pinBase: Alley.PinBase?
		@BindableState public var pinFall: Alley.PinFall?

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

	public var body: some ReducerProtocol<State, Action> {
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
		filter != nil
	}

	public var filter: Alley.FetchRequest.Filter? {
		if material != nil || pinFall != nil || pinBase != nil || mechanism != nil {
			return .properties(material: material, pinFall: pinFall, pinBase: pinBase, mechanism: mechanism)
		}

		return nil
	}
}
