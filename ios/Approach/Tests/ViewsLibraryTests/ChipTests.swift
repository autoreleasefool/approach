import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("ChipTests", .tags(.library), .snapshots(record: .failed))
struct ChipTests {

	@Test("Chip snapshots", .tags(.snapshot))
	@MainActor
	func snapshotChips() {
		let chips = VStack {
			Chip(title: "Chip", style: .primary)
			Chip(title: "Chip", systemImage: "person", style: .primary)
			Chip(title: "Radio Box", systemImage: "star", accessory: .radioBox, style: .primary)
			Chip(title: "Radio Box Selected", systemImage: "star", accessory: .radioBoxSelected, style: .primary)
			Chip(title: "Info", systemImage: "star", style: .info)
			Chip(title: "Plain", systemImage: "star", style: .plain)
			Chip(title: "Primary", systemImage: "star", style: .primary)
		}
		.frame(width: 428)

		assertSnapshot(of: chips, as: .image)
	}
}
