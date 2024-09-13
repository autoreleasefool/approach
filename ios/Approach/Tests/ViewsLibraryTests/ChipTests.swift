import SnapshotTesting
import SwiftUI
import Testing
@testable import ViewsLibrary

struct ChipTests {
	@Test("Chip snapshots", .tags(.snapshot))
	@MainActor func snapshotChips() {
		let chips = VStack {
			Chip(title: "Chip", style: .primary)
			Chip(title: "Chip", icon: .person, style: .primary)
			Chip(title: "Radio Box", icon: .star, accessory: .radioBox, style: .primary)
			Chip(title: "Radio Box Selected", icon: .star, accessory: .radioBoxSelected, style: .primary)
			Chip(title: "Info", icon: .star, style: .info)
			Chip(title: "Plain", icon: .star, style: .plain)
			Chip(title: "Primary", icon: .star, style: .primary)
		}
		.frame(width: 428)

		assertSnapshot(of: chips, as: .image)
	}
}
