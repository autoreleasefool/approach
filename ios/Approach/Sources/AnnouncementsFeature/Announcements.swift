import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import SwiftUI
import SwiftUIExtensionsLibrary

@Reducer
public struct Announcements: Reducer {
	public struct State: Equatable {
		@PresentationState public var christmas: Christmas2023Announcement.State?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onFirstAppear
			case didFinishDismissingAnnouncement
		}

		public enum InternalAction: Equatable {
			case showChristmasAnnouncement
			case christmas(PresentationAction<Christmas2023Announcement.Action>)
		}

		public enum DelegateAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onFirstAppear:
					return .run { send in
						if Christmas2023Announcement.meetsExpectationsToShow() {
							await send(.internal(.showChristmasAnnouncement))
						}
					}

				case .didFinishDismissingAnnouncement:
					return .run { _ in preferences.setKey(.announcementChristmasBanner2023Hidden, toBool: true) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showChristmasAnnouncement:
					state.christmas = .init()
					return .none

				case let .christmas(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .openAppIconSettings:
						return .none
					}

				case .christmas(.presented(.view)), .christmas(.presented(.internal)), .christmas(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$christmas, action: /Action.internal..Action.InternalAction.christmas) {
			Christmas2023Announcement()
		}
	}
}

// MARK: - View

extension View {
	public func announcements(
		store: Store<Announcements.State, Announcements.Action>
	) -> some View {
		self
			.onFirstAppear { store.send(.view(.onFirstAppear)) }
			.sheet(
				store: store.scope(state: \.$christmas, action: { .internal(.christmas($0)) }),
				onDismiss: { store.send(.view(.didFinishDismissingAnnouncement)) },
				content: { store in
					Christmas2023AnnouncementView(store: store)
						.presentationDetents([.medium])
				}
			)
	}
}
