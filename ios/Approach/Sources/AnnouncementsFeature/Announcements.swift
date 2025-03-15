import ComposableArchitecture
import FeatureActionLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct Announcements: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Presents public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didFirstAppear
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case showHalloweenAnnouncement
			case showTenYearAnniversaryAnnouncement

			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case halloween2024(Halloween2024Announcement)
		case tenYearAnniversary(TenYearAnniversaryAnnouncement)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .run { send in
						// Check for announcements
						if await Halloween2024Announcement.shouldShow() {
							await send(.internal(.showHalloweenAnnouncement))
						} else if await TenYearAnniversaryAnnouncement.shouldShow() {
							await send(.internal(.showTenYearAnniversaryAnnouncement))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showHalloweenAnnouncement:
					state.destination = .halloween2024(Halloween2024Announcement.State())
					return .none

				case .showTenYearAnniversaryAnnouncement:
					state.destination = .tenYearAnniversary(TenYearAnniversaryAnnouncement.State())
					return .none

				case .destination(.dismiss):
					switch state.destination {
					case .halloween2024:
						return .run { _ in await Halloween2024Announcement.didDismiss() }

					case .tenYearAnniversary:
						return .run { _ in await TenYearAnniversaryAnnouncement.didDismiss() }

					case .none:
						return .none
					}

				case .destination(.presented(.halloween2024(.delegate(.doNothing)))),
						.destination(.presented(.halloween2024(.internal))),
						.destination(.presented(.halloween2024(.view))),
						.destination(.presented(.tenYearAnniversary(.delegate(.doNothing)))),
						.destination(.presented(.tenYearAnniversary(.internal))),
						.destination(.presented(.tenYearAnniversary(.view))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)
	}
}

// MARK: - View

public struct AnnouncementsViewModifier: ViewModifier {
	@SwiftUI.State var store: StoreOf<Announcements>

	public func body(content: Content) -> some View {
		content
			.onFirstAppear { store.send(.view(.didFirstAppear)) }
			.sheet(
				item: $store.scope(state: \.destination?.halloween2024, action: \.internal.destination.halloween2024)
			) {
				Halloween2024AnnouncementView(store: $0)
			}
			.sheet(
				item: $store.scope(state: \.destination?.tenYearAnniversary, action: \.internal.destination.tenYearAnniversary)
			) {
				TenYearAnniversaryAnnouncementView(store: $0)
			}
	}
}

extension View {
	public func announcements(store: StoreOf<Announcements>) -> some View {
		self.modifier(AnnouncementsViewModifier(store: store))
	}
}
