import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary
import UserDefaultsPackageServiceInterface

@Reducer
public struct Halloween2024Announcement: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didTapOpenIconSettingsButton
			case didTapDismissButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapDismissButton:
					return .run { _ in await dismiss() }

				case .didTapOpenIconSettingsButton:
					return .run { _ in await dismiss() }
				}

			case .delegate(.doNothing), .internal(.doNothing):
				return .none
			}
		}
	}
}

// MARK: Presentation

extension Halloween2024Announcement {
	private static let IS_DISMISSED_KEY = "announcements.halloweeen2024.isDismissed"

	public static func shouldShow() async -> Bool {
		@Dependency(\.date) var date
		@Dependency(\.userDefaults) var userDefaults

		// Date is before November 1, 2024
		let isDateValid = date() < Date(timeIntervalSince1970: 1_730_462_400)

		// Has not been dismissed
		let isDismissed = userDefaults.bool(forKey: IS_DISMISSED_KEY) ?? false

		return isDateValid && !isDismissed
	}

	public static func didDismiss() async {
		@Dependency(\.userDefaults) var userDefaults
		userDefaults.setBool(forKey: IS_DISMISSED_KEY, to: true)
	}
}

// MARK: View

@ViewAction(for: Halloween2024Announcement.self)
struct Halloween2024AnnouncementView: View {
	let store: StoreOf<Halloween2024Announcement>

	init(store: StoreOf<Halloween2024Announcement>) {
		self.store = store
	}

	var body: some View {
		VStack(spacing: 0) {
			Spacer()

			Text(Strings.Announcement.Halloween2024.title)
				.font(.headline)
				.multilineTextAlignment(.center)

			HStack(alignment: .center, spacing: 0) {
				Group {
					Image(uiImage: AppIcon.candyCorn.image ?? UIImage())
						.resizable()

					Image(uiImage: AppIcon.devilHorns.image ?? UIImage())
						.resizable()

					Image(uiImage: AppIcon.witchHat.image ?? UIImage())
						.resizable()
				}
				.scaledToFit()
				.frame(width: .extraLargeIcon)
				.cornerRadius(.standardRadius)
				.shadow(radius: .standardRadius)
				.padding(.horizontal, .smallSpacing)
				.padding(.vertical, .standardSpacing)
			}

			Text(Strings.Announcement.Halloween2024.message)
				.font(.body)
				.multilineTextAlignment(.center)

			Spacer()

			Button { send(.didTapOpenIconSettingsButton) } label: {
				Text(Strings.Announcement.Halloween2024.openSettings)
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
			.padding(.bottom, .smallSpacing)

			Button { send(.didTapDismissButton) } label: {
				Text(Strings.Action.dismiss)
					.frame(maxWidth: .infinity)
					.padding(.vertical, .smallSpacing)
			}
		}
		.padding()
		.presentationDetents([.medium])
	}
}
