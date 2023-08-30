import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ScoreSheetFeature

public struct Sharing: Reducer {
	public struct State: Equatable {
		public var games: IdentifiedArrayOf<Game.Summary> = []

		public init(games: [Game.ID]) {
			// TODO: share games
		}

		public init(series: Series.ID) {
			// TODO: share series
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapShareButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapShareButton:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
