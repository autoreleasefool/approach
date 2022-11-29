import AlleysDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary

public struct AlleysFilter: ReducerProtocol {
	public struct State: Equatable {
		@BindableState public var material: Alley.Material?
		@BindableState public var mechanism: Alley.Mechanism?
		@BindableState public var pinBase: Alley.PinBase?
		@BindableState public var pinFall: Alley.PinFall?

		public init() {}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case applyButtonTapped
		case clearFiltersButtonTapped
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Reduce { state, action in
			switch action {
			case .clearFiltersButtonTapped:
				state = .init()
				return .task { .applyButtonTapped }

			case .applyButtonTapped:
				return .none

			case .binding:
				return .none
			}
		}
	}
}

extension AlleysFilter.State {
	public var filters: [Alley.FetchRequest.Filter] {
		var filters: [Alley.FetchRequest.Filter] = []
		if let material {
			filters.append(.material(material))
		}
		if let mechanism {
			filters.append(.mechanism(mechanism))
		}
		if let pinFall {
			filters.append(.pinFall(pinFall))
		}
		if let pinBase {
			filters.append(.pinBase(pinBase))
		}
		return filters
	}
}
