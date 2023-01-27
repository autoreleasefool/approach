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

			case .applyButtonTapped, .binding:
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
