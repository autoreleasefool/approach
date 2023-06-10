import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsRepositoryInterface

public struct StatisticsOverview: Reducer {
	public struct State: Equatable {
		public var isShowingOverviewHint: Bool
		public var isShowingDetailsHint: Bool

		public var filter: TrackableFilter?
		@PresentationState public var destination: Destination.State?

		public init() {
			@Dependency(\.preferences) var preferences

			self.isShowingOverviewHint = preferences.bool(forKey: .statisticsOverviewHintHidden) != true
			self.isShowingDetailsHint = preferences.bool(forKey: .statisticsDetailsHintHidden) != true
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapDismissOverviewHint
			case didTapDismissDetailsHint
			case didTapViewDetailedStatistics
			case sourcePickerDidDismiss
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case sourcePicker(StatisticsSourcePicker.State)
			case details(StatisticsDetails.State)
		}

		public enum Action: Equatable {
			case sourcePicker(StatisticsSourcePicker.Action)
			case details(StatisticsDetails.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.sourcePicker, action: /Action.sourcePicker) {
				StatisticsSourcePicker()
			}
			Scope(state: /State.details, action: /Action.details) {
				StatisticsDetails()
			}
		}
	}

	public init() {}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .sourcePickerDidDismiss:
					guard let filter = state.filter else { return .none }
					state.destination = .details(.init(filter: filter))
					return .none

				case .didTapDismissDetailsHint:
					state.isShowingDetailsHint = false
					return .run { _ in preferences.setKey(.statisticsDetailsHintHidden, toBool: true) }

				case .didTapDismissOverviewHint:
					state.isShowingOverviewHint = false
					return .run { _ in preferences.setKey(.statisticsOverviewHintHidden, toBool: true) }

				case .didTapViewDetailedStatistics:
					state.destination = .sourcePicker(.init(source: nil))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .destination(.presented(.sourcePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSource(source):
						state.filter = .init(source: source)
						state.destination = nil
						return .none
					}

				case let .destination(.presented(.details(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.sourcePicker(.internal))),
						.destination(.presented(.sourcePicker(.view))),
						.destination(.presented(.details(.internal))),
						.destination(.presented(.details(.view))):
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
