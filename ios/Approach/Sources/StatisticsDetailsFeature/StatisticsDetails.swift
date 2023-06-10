import ComposableArchitecture
import FeatureActionLibrary
import NotificationsServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import SwiftUI

public struct StatisticsDetails: Reducer {
	public struct State: Equatable {
		public var staticValues: IdentifiedArrayOf<StaticValueGroup> = []

		public var filter: TrackableFilter
		public var sources: TrackableFilter.Sources?

		@PresentationState public var destination: Destination.State?

		public init(filter: TrackableFilter) {
			self.filter = filter
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didTapSourcePicker
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)
			case charts(StatisticsDetailsCharts.Action)

			case didLoadSources(TaskResult<TrackableFilter.Sources?>)
			case didLoadStaticValues(TaskResult<[StaticValueGroup]>)
			case orientationChange(UIDeviceOrientation)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case list(StatisticsDetailsList.State)
			case sourcePicker(StatisticsSourcePicker.State)
		}

		public enum Action: Equatable {
			case list(StatisticsDetailsList.Action)
			case sourcePicker(StatisticsSourcePicker.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.list, action: /Action.list) {
				StatisticsDetailsList()
			}
			Scope(state: /State.sourcePicker, action: /Action.sourcePicker) {
				StatisticsSourcePicker()
			}
		}
	}

	public init() {}

	@Dependency(\.statistics) var statistics
	@Dependency(\.uiDeviceNotifications) var uiDevice

	public var body: some ReducerOf<Self> {
		Scope(state: \.charts, action: /Action.internal..Action.InternalAction.charts) {
			StatisticsDetailsCharts()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .merge(
						refreshStatistics(state: state),
						.run { send in
							for await orientation in uiDevice.orientationDidChange() {
								await send(.internal(.orientationChange(orientation)))
							}
						}
					)

				case .didTapSourcePicker:
					state.destination = .sourcePicker(.init(source: state.filter.source))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadSources(.success(sources)):
					state.sources = sources
					return .none

				case .didLoadSources(.failure):
					// TODO: handle error loading sources
					return .none

				case let .didLoadStaticValues(.success(statistics)):
					state.staticValues = .init(uniqueElements: statistics)
					state.destination = .list(.init(staticValues: state.staticValues))
					return .none

				case .didLoadStaticValues(.failure):
					// TODO: show statistics loading failure
					return .none

				case let .orientationChange(orientation):
					switch orientation {
					case .portrait, .portraitUpsideDown, .faceUp, .faceDown, .unknown:
						state.destination = .list(.init(staticValues: state.staticValues))
					case .landscapeLeft, .landscapeRight:
						state.destination = nil
					@unknown default:
						state.destination = .list(.init(staticValues: state.staticValues))
					}
					return .none

				case let .destination(.presented(.list(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didRequestStaticValue(id):
						// TODO: show static value details
						return .none
					}

				case let .destination(.presented(.sourcePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSource(source):
						state.filter.source = source
						return refreshStatistics(state: state)
					}

				case let .charts(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss):
					state.destination = .list(.init(staticValues: state.staticValues))
					return .none

				case .destination(.presented(.list(.internal))),
						.destination(.presented(.list(.view))),
						.destination(.presented(.sourcePicker(.internal))),
						.destination(.presented(.sourcePicker(.view))):
					return .none

				case .charts(.internal), .charts(.view):
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

	private func refreshStatistics(state: State) -> Effect<Action> {
		.merge(
			.run { [filter = state.filter] send in
				await send(.internal(.didLoadStaticValues(TaskResult {
					try await statistics.load(for: filter).staticValueGroups()
				})))
			},
			.run { [source = state.filter.source] send in
				await send(.internal(.didLoadSources(TaskResult {
					try await statistics.loadSources(source)
				})))
			}
		)
	}
}

extension StatisticsDetails.State {
	var charts: StatisticsDetailsCharts.State {
		get { .init(timeline: filter.timeline) }
		set { filter.timeline = newValue.timeline }
	}
}
