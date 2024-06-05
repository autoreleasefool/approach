import AnalyticsServiceInterface
import Collections
import ComposableArchitecture
import FeatureActionLibrary
import RecentlyUsedServiceInterface
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import TipsLibrary
import TipsServiceInterface

@Reducer
public struct StatisticsOverview: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var isShowingOverviewTip: Bool
		public var isShowingDetailsTip: Bool

		public var recentlyUsedFilters = OrderedDictionary<TrackableFilter, TrackableFilter.Sources>()

		public var filter: TrackableFilter?
		@Presents public var destination: Destination.State?

		public init() {
			@Dependency(TipsService.self) var tips
			self.isShowingOverviewTip = tips.shouldShow(tipFor: .statisticsOverview)
			self.isShowingDetailsTip = tips.shouldShow(tipFor: .statisticsDetails)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case onAppear
			case task
			case didTapDismissOverviewTip
			case didTapDismissDetailsTip
			case didTapViewDetailedStatistics
			case sourcePickerDidDismiss
			case didTapFilter(TrackableFilter)
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didLoadRecentlyUsedFilters(Result<OrderedDictionary<TrackableFilter, TrackableFilter.Sources>, Error>)

			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case sourcePicker(StatisticsSourcePicker)
		case details(StatisticsDetails)
	}

	public init() {}

	@Dependency(StatisticsRepository.self) var statistics
	@Dependency(TipsService.self) var tips

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .task:
					return .run { send in
						for try await filters in statistics.observeRecentlyUsedFilters() {
							await send(.internal(.didLoadRecentlyUsedFilters(.success(filters))), animation: .default)
						}
					} catch: { error, send in
						await send(.internal(.didLoadRecentlyUsedFilters(.failure(error))))
					}

				case let .didTapFilter(filter):
					state.destination = .details(.init(filter: filter))
					return .none

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
				case let .didLoadRecentlyUsedFilters(.success(filters)):
					state.recentlyUsedFilters = filters
					return .none

				case .didLoadRecentlyUsedFilters(.failure):
					// TODO: Handle errors
					return .none

				case let .destination(.presented(.sourcePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSource(source):
						state.filter = .init(source: source)
						state.destination = nil
						return .none
					}

				case .destination(.presented(.details(.delegate(.doNothing)))):
					return .none

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
						.destination(.presented(.details(.view))),
						.destination(.presented(.details(.binding))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
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
