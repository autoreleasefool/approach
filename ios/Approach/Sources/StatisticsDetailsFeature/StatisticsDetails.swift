import ComposableArchitecture
import FeatureActionLibrary
import NotificationsServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import SwiftUI

public struct StatisticsDetails: Reducer {
	public struct State: Equatable {
		public var isListSheetVisible = true
		public var statistics: IdentifiedArrayOf<TrackedGroup> = []

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didTapTrackedValue(id: String)
			case setListSheet(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didLoadStatistics(TaskResult<[TrackedGroup]>)
			case orientationChange(UIDeviceOrientation)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.statistics) var statistics
	@Dependency(\.uiDeviceNotifications) var uiDevice

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .merge(
						.run { send in
							await send(.internal(.didLoadStatistics(TaskResult {
								try await statistics.load(forBowler: UUID(0)).trackedValues()
							})))
						},
						.run { send in
							for await orientation in uiDevice.orientationDidChange() {
								await send(.internal(.orientationChange(orientation)))
							}
						}
					)

				case let .didTapTrackedValue(id):
					return .none

				case let .setListSheet(isPresented):
					state.isListSheetVisible = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadStatistics(.success(statistics)):
					state.statistics = .init(uniqueElements: statistics)
					return .none

				case .didLoadStatistics(.failure):
					// TODO: show statistics loading failure
					return .none

				case let .orientationChange(orientation):
					switch orientation {
					case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown:
						state.isListSheetVisible = true
					case .landscapeLeft, .landscapeRight:
						state.isListSheetVisible = false
					@unknown default:
						state.isListSheetVisible = true
					}
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
