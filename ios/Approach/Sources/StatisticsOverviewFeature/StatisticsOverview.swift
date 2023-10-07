import ComposableArchitecture
import FeatureActionLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import TipsLibrary
import TipsServiceInterface

public struct StatisticsOverview: Reducer {
	public struct State: Equatable {
		public var isShowingOverviewTip: Bool
		public var isShowingDetailsTip: Bool

		public var filter: TrackableFilter?
		@PresentationState public var destination: Destination.State?

		public init() {
			@Dependency(\.tips) var tips

			self.isShowingOverviewTip = tips.shouldShow(tipFor: .statisticsOverview)
			self.isShowingDetailsTip = tips.shouldShow(tipFor: .statisticsDetails)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapDismissOverviewTip
			case didTapDismissDetailsTip
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

	@Dependency(\.statistics) var statistics
	@Dependency(\.tips) var tips

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .sourcePickerDidDismiss:
					guard let filter = state.filter else { return .none }
					state.destination = .details(.init(filter: filter))
					state.filter = nil
					return .none

				case .didTapDismissDetailsTip:
					state.isShowingDetailsTip = false
					return .run { _ in await tips.hide(tipFor: .statisticsDetails) }

				case .didTapDismissOverviewTip:
					state.isShowingOverviewTip = false
					return .run { _ in await tips.hide(tipFor: .statisticsOverview)}

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

				case .destination(.dismiss):
					switch state.destination {
					case .details:
						return .run { _ in await statistics.hideNewStatisticLabels() }
					case .sourcePicker, .none:
						return .none
					}

				case .destination(.presented(.sourcePicker(.internal))),
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

extension Tip {
	public static let statisticsOverview = Tip(
		id: "Statistics.Overview.GetAnOverview",
		title: Strings.Statistics.Overview.GetAnOverviewHint.title,
		message: Strings.Statistics.Overview.GetAnOverviewHint.message
	)

	public static let statisticsDetails = Tip(
		id: "Statistics.Overview.ViewMoreDetails",
		title: Strings.Statistics.Overview.ViewMoreDetailsHint.title,
		message: Strings.Statistics.Overview.ViewMoreDetailsHint.message
	)
}
