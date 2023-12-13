import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct Christmas2023Announcement: Reducer {
	static func meetsExpectationsToShow() -> Bool {
		@Dependency(\.preferences) var preferences
		let christmas2023AnnouncementHiddden = preferences.bool(forKey: .announcementChristmasBanner2023Hidden) ?? false
		guard !christmas2023AnnouncementHiddden else { return false }

		@Dependency(\.date) var date
		// Before January 1, 2024
		return date() < Date(timeIntervalSince1970: 1704067200)
	}

	public struct State: Equatable {}
	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction {
			case onFirstAppear
			case didTapOpenAppSettings
			case didTapDismiss
		}
		@CasePathable public enum DelegateAction {
			case openAppIconSettings
		}
		@CasePathable public enum InternalAction { case doNothing }

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Dependency(\.dismiss) var dismiss

	init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onFirstAppear:
					return .none

				case .didTapOpenAppSettings:
					return .concatenate(
						.send(.delegate(.openAppIconSettings)),
						.run { _ in await dismiss() }
					)

				case .didTapDismiss:
					return .run { _ in await dismiss() }
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.onFirstAppear):
				return Analytics.Announcement.ChristmasAnnouncementShown()
			default:
				return nil
			}
		}
	}
}

public struct Christmas2023AnnouncementView: View {
	let store: StoreOf<Christmas2023Announcement>

	public var body: some View {
		VStack(spacing: 0) {
			Spacer()

			Text(Strings.Announcement.Christmas2023.title)
				.font(.headline)
				.multilineTextAlignment(.center)

			Image(uiImage: UIImage(named: AppIcon.christmas.rawValue) ?? UIImage())
				.resizable()
				.scaledToFit()
				.frame(width: .extraLargeIcon)
				.cornerRadius(.standardRadius)
				.shadow(radius: .standardRadius)
				.padding(.horizontal, .smallSpacing)
				.padding(.vertical, .standardSpacing)

			Text(Strings.Announcement.Christmas2023.message)
				.font(.body)
				.multilineTextAlignment(.center)

			Spacer()

			Button { store.send(.view(.didTapOpenAppSettings)) } label: {
				Text(Strings.Announcement.Christmas2023.openSettings)
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
			.padding(.bottom, .smallSpacing)

			Button { store.send(.view(.didTapDismiss)) } label: {
				Text(Strings.Action.dismiss)
					.frame(maxWidth: .infinity)
					.padding(.vertical, .smallSpacing)
			}
		}
		.onFirstAppear { store.send(.view(.onFirstAppear)) }
		.padding()
	}
}
