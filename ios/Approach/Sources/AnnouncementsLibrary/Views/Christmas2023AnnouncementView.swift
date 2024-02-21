import AssetsLibrary
import Dependencies
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct Christmas2023AnnouncementView: View {
	let onTapOpenSettings: () -> Void
	let onTapDismiss: () -> Void

	public init(
		onTapOpenSettings: @escaping () -> Void,
		onTapDismiss: @escaping () -> Void
	) {
		self.onTapOpenSettings = onTapOpenSettings
		self.onTapDismiss = onTapDismiss
	}

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

			Button(action: onTapOpenSettings) {
				Text(Strings.Announcement.Christmas2023.openSettings)
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
			.padding(.bottom, .smallSpacing)

			Button(action: onTapDismiss) {
				Text(Strings.Action.dismiss)
					.frame(maxWidth: .infinity)
					.padding(.vertical, .smallSpacing)
			}
		}
		.padding()
		.presentationDetents([.medium])
	}
}

extension Christmas2023AnnouncementView {
	static func meetsExpectationsToShow() -> Bool {
		@Dependency(\.date) var date
		// Before January 1, 2024
		return date() < Date(timeIntervalSince1970: 1704067200)
	}
}
