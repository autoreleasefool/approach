import AnalyticsServiceInterface
import AnnouncementsFeature
import ComposableArchitecture
import FeatureActionLibrary
import GamesListFeature
import QuickLaunchRepositoryInterface
import SeriesEditorFeature

@Reducer
public struct Overview: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var quickLaunch = QuickLaunch.State()

		@Presents public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didFirstAppear
			case didStartTask
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case quickLaunch(QuickLaunch.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case games(GamesList)
		case seriesEditor(SeriesEditor)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Scope(state: \.quickLaunch, action: \.internal.quickLaunch) {
			QuickLaunch()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .none

				case .didStartTask:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .quickLaunch(.delegate(delegateAction)):
					switch delegateAction {
					case let .createSeries(series, league):
						state.destination = .seriesEditor(SeriesEditor.State(value: .create(series), inLeague: league))
						return .none
					}

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						guard let league = state.quickLaunch.source.value?.league else { return .none }
						state.destination = .games(GamesList.State(series: created.asGameHost, host: league))
						return .none

					case .didFinishArchiving, .didFinishUpdating:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.games(.delegate(.doNothing)))),
						.destination(.presented(.games(.view))),
						.destination(.presented(.games(.internal))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.binding))),
						.quickLaunch(.view), .quickLaunch(.internal):
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
