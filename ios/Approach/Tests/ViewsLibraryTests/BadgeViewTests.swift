import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("BadgeView", .tags(.library), .snapshots(record: .failed))
struct BadgeViewTests {

	@Test("Badge snapshots", .tags(.snapshot))
	@MainActor
	func snapshotBadges() {
		let badges = VStack {
			BadgeView("Badge", style: .plain)
			BadgeView("Badge", style: .success)
			BadgeView("Badge", style: .destructive)
			BadgeView("Badge", style: .info)
			BadgeView("Badge", style: .primary)
			BadgeView("Badge", style: .init(foreground: .red, background: .blue))
		}
		.frame(width: 428)

		assertSnapshot(of: badges, as: .image)
	}
}
