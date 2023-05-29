import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StatisticsDetailsFeature
import StatisticsRepositoryInterface

public struct StatisticsOverview: Reducer {
	public struct State: Equatable {
		public var isShowingOverviewHint: Bool
		public var isShowingDetailsHint: Bool

		@PresentationState public var details: StatisticsDetails.State?

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
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case details(PresentationAction<StatisticsDetails.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.preferences) var preferences

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapDismissDetailsHint:
					state.isShowingDetailsHint = false
					return .run { _ in preferences.setKey(.statisticsDetailsHintHidden, toBool: true) }

				case .didTapDismissOverviewHint:
					state.isShowingOverviewHint = false
					return .run { _ in preferences.setKey(.statisticsOverviewHintHidden, toBool: true) }

				case .didTapViewDetailedStatistics:
					state.details = .init()
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .details(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .details(.presented(.internal)), .details(.presented(.view)), .details(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$details, action: /Action.internal..Action.InternalAction.details) {
			StatisticsDetails()
		}
	}
}
