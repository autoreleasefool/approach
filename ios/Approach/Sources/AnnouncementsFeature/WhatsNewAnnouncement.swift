import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import UserDefaultsPackageServiceInterface
import ViewsLibrary

@Reducer
public struct WhatsNewAnnouncement: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didTapClose
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
				case .didTapClose:
					return .run { _ in await dismiss() }
				}

			case .delegate(.doNothing), .internal(.doNothing):
				return .none
			}
		}
	}
}

// MARK: Presentation

extension WhatsNewAnnouncement {
	private static let version = 1
	private static func isVersionDismissedKey(_ version: Int) -> String {
		"announcements.whatsNew.\(version).isDismissed"
	}

	public static func shouldShow() async -> Bool {
		@Dependency(\.userDefaults) var userDefaults

		let isNotDismissed = !(userDefaults.bool(forKey: isVersionDismissedKey(version)) ?? false)

		return isNotDismissed
	}

	public static func didDismiss() async {
		@Dependency(\.userDefaults) var userDefaults
		userDefaults.setBool(forKey: isVersionDismissedKey(version), to: true)
	}
}

// MARK: View

@ViewAction(for: WhatsNewAnnouncement.self)
struct WhatsNewAnnouncementView: View {
	let store: StoreOf<WhatsNewAnnouncement>

	private static let items: [NewItem.ViewState] = [
		.init(
			title: Strings.Announcement.WhatsNew.V1.ShareToSocialMedia.title,
			description: Strings.Announcement.WhatsNew.V1.ShareToSocialMedia.description,
			image: Asset.Media.Icons.Social.instagram.image
		),
	]

	init(store: StoreOf<WhatsNewAnnouncement>) {
		self.store = store
	}

	var body: some View {
		VStack(spacing: 0) {
			ZStack {
				background
				newItems
			}

			Divider()

			continueButton
		}
		.presentationDetents([.large])
	}

	private var background: some View {
		Asset.Media.Onboarding.background.swiftUIImage
			.resizable(resizingMode: .tile)
			.ignoresSafeArea(.all)
			.opacity(0.2)
	}

	private var newItems: some View {
		ScrollView {
			VStack(alignment: .center, spacing: .standardSpacing) {
				Text(Strings.Announcement.WhatsNew.title)
					.font(.title)
					.bold()
					.multilineTextAlignment(.center)
					.modifier(TextBackground())
					.padding(.vertical, .extraLargeSpacing)

				VStack(alignment: .center, spacing: .standardSpacing) {
					ForEach(Self.items) { item in
						NewItem(state: item)
							.padding(.horizontal, .standardSpacing)
					}
				}
				.modifier(TextBackground())

				Spacer()
			}
			.padding(.horizontal, .standardSpacing)
		}
	}

	private var continueButton: some View {
		Button { send(.didTapClose) } label: {
			Text(Strings.Action.continue)
				.frame(maxWidth: .infinity)
		}
		.modifier(PrimaryButton())
		.padding(.horizontal, .standardSpacing)
		.padding(.vertical, .largeSpacing)
	}
}

private struct NewItem: View {
	let state: ViewState

	struct ViewState: Identifiable {
		let title: String
		let description: String
		let image: UIImage

		var id: String { title }
	}

	var body: some View {
		HStack(alignment: .center, spacing: .standardSpacing) {
			Image(uiImage: state.image)
				.resizable()
				.scaledToFit()
				.frame(width: .smallerIcon, height: .smallerIcon)
				.foregroundColor(Asset.Colors.Primary.default)

			VStack(alignment: .leading, spacing: .tinySpacing) {
				Text(state.title)
					.font(.headline)
					.bold()
				Text(state.description)
					.font(.body)
					.italic()
			}
		}
		.frame(maxWidth: .infinity, alignment: .leading)
	}
}

private struct TextBackground: ViewModifier {
	func body(content: Content) -> some View {
		content
			.padding(.standardSpacing)
			.background(
				RoundedRectangle(cornerRadius: .standardRadius)
					.fill(.background.opacity(0.8))
					.blur(radius: .smallRadius)
			)
	}
}

#Preview {
	Text("")
		.sheet(isPresented: .constant(true)) {
			NavigationStack {
				WhatsNewAnnouncementView(
					store: Store(
						initialState: WhatsNewAnnouncement.State(),
						reducer: { WhatsNewAnnouncement() },
					)
				)
			}
		}
}
