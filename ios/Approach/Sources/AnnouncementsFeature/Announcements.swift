import AnnouncementsLibrary
import AnnouncementsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import SwiftUI

@Reducer
public struct Announcements: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let announcement: Announcement

		public init(announcement: Announcement) {
			self.announcement = announcement
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didDoAction(AnnouncementView.Action)
			case didDismiss
		}
		@CasePathable public enum Delegate {
			case openAppIconSettings
		}
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didDoAction(action):
					switch action {
					case .openAppIconSettings:
						return .concatenate(
							.send(.delegate(.openAppIconSettings)),
							.run { _ in await dismiss() }
						)
					}

				case .didDismiss:
					return .run { _ in await dismiss() }
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: Announcements.self)
public struct AnnouncementsView: View {
	public let store: StoreOf<Announcements>

	public init(store: StoreOf<Announcements>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			AnnouncementsLibrary.AnnouncementView(
				announcement: store.announcement,
				onAction: { send(.didDoAction($0)) },
				onDismiss: { send(.didDismiss) }
			)
		}
	}
}
