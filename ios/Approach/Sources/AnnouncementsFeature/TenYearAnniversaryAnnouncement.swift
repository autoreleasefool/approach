import AchievementsFeature
import AchievementsLibrary
import AchievementsServiceInterface
import AppInfoPackageServiceInterface
import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import UserDefaultsPackageServiceInterface
import ViewsLibrary

@Reducer
public struct TenYearAnniversaryAnnouncement: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didFirstAppear
			case didTapClaimButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(AchievementsService.self) var achievements
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .run { _ in
						await achievements.sendEvent(EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(id: uuid()))
					}

				case .didTapClaimButton:
					return .run { _ in await dismiss() }
				}

			case .delegate(.doNothing), .internal(.doNothing):
				return .none
			}
		}
	}
}

// MARK: Presentation

extension TenYearAnniversaryAnnouncement {
	private static let IS_DISMISSED_KEY = "announcements.tenYearAnniversary.isDismissed"

	public static func shouldShow() async -> Bool {
		@Dependency(\.date) var date
		@Dependency(\.userDefaults) var userDefaults
		@Dependency(\.featureFlags) var featureFlags
		@Dependency(\.appInfo) var appInfo

		// Date is after April 1, 2025
		let isAfterApril1 = date() > Date(timeIntervalSince1970: 1_743_480_000)
		let isNotDismissed = !(userDefaults.bool(forKey: IS_DISMISSED_KEY) ?? false)
		let isFeatureEnabled = featureFlags.isFlagEnabled(.achievements)
		let isNotFirstLaunch = appInfo.getNumberOfSessions() > 1
		let isOneWeekSinceFirstLaunch = appInfo.getInstallDate().daysSince(.now) >= .days(7)

		return isAfterApril1 && isNotDismissed && isFeatureEnabled && isNotFirstLaunch && isOneWeekSinceFirstLaunch
	}

	public static func didDismiss() async {
		@Dependency(\.userDefaults) var userDefaults
		userDefaults.setBool(forKey: IS_DISMISSED_KEY, to: true)
	}
}

// MARK: View

@ViewAction(for: TenYearAnniversaryAnnouncement.self)
struct TenYearAnniversaryAnnouncementView: View {
	let store: StoreOf<TenYearAnniversaryAnnouncement>

	@State private var contentSize: CGSize = CGSize(width: 0, height: 100)

	init(store: StoreOf<TenYearAnniversaryAnnouncement>) {
		self.store = store
	}

	var body: some View {
		ZStack {
			background

			VStack(spacing: 0) {
				title
				heroImage
				description
				action
			}
			.padding(.standardSpacing)
			.measure(key: ContentSizeKey.self, to: $contentSize)
		}
		.onFirstAppear { send(.didFirstAppear) }
		.presentationDetents([.height(contentSize.height)])
	}

	private var background: some View {
		Asset.Media.Onboarding.background.swiftUIImage
			.resizable(resizingMode: .tile)
			.ignoresSafeArea(.all)
			.opacity(0.2)
	}

	private var title: some View {
		Text(Strings.Announcement.TenYears.title)
			.font(.title.bold())
			.multilineTextAlignment(.center)
			.padding(.smallSpacing)
			.background(textBackground)
			.padding(.bottom, .largeSpacing)
	}

	private var heroImage: some View {
		FloatingImage(
			image: Asset.Media.Achievements.tenYears
		)
		.frame(maxWidth: .infinity, minHeight: 128, maxHeight: 192)
		.padding(.bottom, .standardSpacing)
	}

	private var description: some View {
		VStack(spacing: .tinySpacing) {
			Text(Strings.Announcement.TenYears.Description.fromBowlingCompanionToApproach)
				.font(.body.bold())
			+ Text(" \(Strings.Announcement.TenYears.Description.hopeYouveEnjoyed)")
				.font(.body)
		}
		.multilineTextAlignment(.center)
		.fixedSize(horizontal: false, vertical: true)
		.padding(.standardSpacing)
		.background(textBackground)
		.padding(.bottom, .standardSpacing)
	}

	private var action: some View {
		Button { send(.didTapClaimButton) } label: {
			Text(Strings.Announcement.TenYears.Action.claimBadge)
				.frame(maxWidth: .infinity)
		}
		.modifier(PrimaryButton())
		.padding(.bottom, .smallSpacing)
	}

	private var textBackground: some View {
		RoundedRectangle(cornerRadius: .standardRadius)
			.fill(.background.opacity(0.8))
			.blur(radius: .smallRadius)
	}
}

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

#Preview {
	Text("")
		.sheet(isPresented: .constant(true)) {
			TenYearAnniversaryAnnouncementView(
				store: Store(
					initialState: .init(),
					reducer: { TenYearAnniversaryAnnouncement() }
				)
			)
		}
}
