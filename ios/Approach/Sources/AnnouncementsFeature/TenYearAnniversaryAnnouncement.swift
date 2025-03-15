import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import UserDefaultsPackageServiceInterface
import ViewsLibrary

@Reducer
public struct TenYearAnniversaryAnnouncement: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
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

	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
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

		// Date is after April 1, 2025
		let isAfterApril1 = date() > Date(timeIntervalSince1970: 1_743_480_000)
		let isDismissed = userDefaults.bool(forKey: IS_DISMISSED_KEY) ?? false

		return isAfterApril1 && !isDismissed
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

	init(store: StoreOf<TenYearAnniversaryAnnouncement>) {
		self.store = store
	}

	var body: some View {
		VStack(spacing: 0) {
			Text("Approach is turning 10!")
				.font(.headline)
				.multilineTextAlignment(.center)

			Text("I hope you've enjoyed bowling with Approach as much as I've enjoyed building it.")
				.font(.body)
				.multilineTextAlignment(.leading)

			Image(systemSymbol: .trophy)
				.resizable()
				.scaledToFit()
				.frame(width: .extraExtraLargeIcon)
				.padding(.vertical, .standardSpacing)

			Spacer()

			Button { send(.didTapClaimButton) } label: {
				Text("Claim Badge")
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
			.padding(.bottom, .smallSpacing)
		}
		.padding()
		.presentationDetents([.medium, .large])
	}
}
