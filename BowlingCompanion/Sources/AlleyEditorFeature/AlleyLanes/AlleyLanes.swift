import ComposableArchitecture
import FeatureActionLibrary
import LanesDataProviderInterface
import SharedModelsLibrary

public struct AlleyLanes: ReducerProtocol {
	public struct State: Equatable {
		public let alley: Alley.ID?
		public var lanes: IdentifiedArrayOf<Lane>?

		public init(alley: Alley.ID?) {
			self.alley = alley
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadLanes(TaskResult<[Lane]>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.lanesDataProvider) var lanesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .task { [alley = state.alley] in
						if let alley {
							return await .internal(.didLoadLanes(TaskResult {
								try await lanesDataProvider.fetchLanes(.init(filter: .alley(alley), ordering: .byLabel))
							}))
						} else {
							return .internal(.didLoadLanes(.success([])))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadLanes(.success(lanes)):
					state.lanes = .init(uniqueElements: lanes)
					return .none

				case .didLoadLanes(.failure):
					// TODO: handle failure loading lanes
					state.lanes = []
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
