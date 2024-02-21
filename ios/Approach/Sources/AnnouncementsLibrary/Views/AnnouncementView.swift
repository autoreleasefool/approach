import SwiftUI

public struct AnnouncementView: View {
	let announcement: Announcement
	let onAction: (Action) -> Void
	let onDismiss: () -> Void

	public init(
		announcement: Announcement,
		onAction: @escaping (Action) -> Void,
		onDismiss: @escaping () -> Void
	) {
		self.announcement = announcement
		self.onAction = onAction
		self.onDismiss = onDismiss
	}

	public var body: some View {
		switch announcement {
		case .christmas2023:
			Christmas2023AnnouncementView(
				onTapOpenSettings: {
					onAction(.openAppIconSettings)
				}, onTapDismiss: {
					onDismiss()
				}
			)
		}
	}
}

extension AnnouncementView {
	public enum Action {
		case openAppIconSettings
	}
}
